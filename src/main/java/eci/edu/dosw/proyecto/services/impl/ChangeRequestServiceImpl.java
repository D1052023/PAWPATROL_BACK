package eci.edu.dosw.proyecto.services.impl;

import eci.edu.dosw.proyecto.dtos.*;
import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.util.CurriculumToFacultyMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import eci.edu.dosw.proyecto.util.TimeUtils;

import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.mappers.ChangeRequestMapper;
import eci.edu.dosw.proyecto.models.*;
import eci.edu.dosw.proyecto.repositories.*;
import eci.edu.dosw.proyecto.services.ChangeRequestService;
import eci.edu.dosw.proyecto.services.DeaneryService;
import eci.edu.dosw.proyecto.services.HistoryService;
import eci.edu.dosw.proyecto.util.MessageExceptions;

/**
 * Clase servicio que implementa la interfaz y maneja la lógica de la solicitud de cambio.
 */
@Service
@RequiredArgsConstructor
public class ChangeRequestServiceImpl implements ChangeRequestService {

    private final ChangeRequestRepository changeRequestRepository;
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final ChangeRequestMapper changeRequestMapper;
    private final HistoryService historyService;
    private final DeaneryRepository deaneryRepository;
    private final DeaneryService deaneryService;
    private final MessageExceptions message;
    private final CurriculumToFacultyMapper curriculumToFacultyMapper;
    private final SecretariatRepository secretariatRepository;

    @Override
    public ChangeRequestDTO createChangeRequest(Integer studentId, ChangeRequestCreateDTO createDto) {
        Student student = message.findStudentOrThrow(studentId);
        LocalDateTime now = TimeUtils.nowUtc();

        ChangeRequest request = buildChangeRequest(student, createDto, now);
        validateSecretariatDatesForFaculty(now, request.getFaculty());
        saveRequestAndLinkToStudent(request, student);
        addStudentToTargetWaitlist(student, createDto.getTargetGroup());
        recordCreationHistory(request, studentId);

        return changeRequestMapper.toDTO(request);
    }


    private void validateSecretariatDatesForFaculty(LocalDateTime now, Faculty faculty) {
        if (faculty == null) return;
        Optional<Secretariat> secOpt = secretariatRepository.findByFaculty(faculty);
        if (secOpt.isEmpty()) {
            return;
        }

        Secretariat sec = secOpt.get();
        LocalDateTime start = sec.getRequestStartDate();
        LocalDateTime end = sec.getRequestEndDate();
        RequestDatesDTO dates = new RequestDatesDTO();
        dates.setStartDate(start);
        dates.setEndDate(end);
        message.ensureNowWithinDatesIfPresent(now, dates);
    }

    private ChangeRequest buildChangeRequest(Student student, ChangeRequestCreateDTO dto, LocalDateTime now) {
        Subject currentSubject = message.findSubjectOrThrow(dto.getCurrentSubject());
        Subject targetSubject = message.findSubjectOrThrow(dto.getTargetSubject());
        Group currentGroup = message.findGroupOrThrow(dto.getCurrentGroup());
        Group targetGroup = message.findGroupOrThrow(dto.getTargetGroup());

        message.ensureCurriculumMatchesStudent(student, targetSubject);
        message.ensureCurriculumMatchesStudentGroup(student, targetGroup);

        ChangeRequest request = new ChangeRequest();
        request.setId(UUID.randomUUID());
        request.setStudentId(student.getId());
        request.setStudentName(student.getName());
        request.setCurrentSubject(currentSubject.getSubjectId());
        request.setCurrentGroup(currentGroup.getGroupId());
        request.setTargetSubject(targetSubject.getSubjectId());
        request.setTargetGroup(targetGroup.getGroupId());
        request.setFaculty(curriculumToFacultyMapper.map(targetSubject.getCurriculum()));
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(now);
        request.setUpdatedAt(now);
        request.setResolutionDeadline(addBusinessDays(now, 5));

        int priority = changeRequestRepository.findByStudentId(student.getId()).size() + 1;
        request.setPriority(priority);

        if (Boolean.TRUE.equals(dto.getExceptional())) {
            applyExceptionalDetails(request, dto, student.getId(), now);
        } else {
            request.setExceptional(false);
        }

        return request;
    }


    private void applyExceptionalDetails(ChangeRequest request, ChangeRequestCreateDTO dto, Integer studentId, LocalDateTime now) {
        message.ensureExceptionalReasonProvided(dto.getExceptionalReason());
        request.setExceptional(true);
        request.setExceptionalReason(dto.getExceptionalReason());
        request.setExceptionalRequestedBy("STUDENT:" + studentId);
        request.setExceptionalRequestedAt(now);
        request.setExceptionalResolutionDeadline(addBusinessDays(now, 5));
    }

    private void saveRequestAndLinkToStudent(ChangeRequest request, Student student) {
        ChangeRequest saved = changeRequestRepository.save(request);

        if (student.getRequests() == null) {
            student.setRequests(new ArrayList<>());
        }
        student.getRequests().add(saved);
        studentRepository.save(student);
    }

    private void addStudentToTargetWaitlist(Student student, String targetGroupId) {
        Group targetGroup = message.findGroupOrThrow(targetGroupId);

        if (targetGroup.getWaitlist() == null) {
            targetGroup.setWaitlist(new ArrayList<>());
        }

        if (!targetGroup.getWaitlist().contains(student.getId())) {
            targetGroup.getWaitlist().add(student.getId());
            groupRepository.save(targetGroup);
        }
    }

    private void recordCreationHistory(ChangeRequest request, int studentId) {
        historyService.addHistoryEvent(request.getId(), "STUDENT", "CREATED",
                "Solicitud creada por estudiante", "STUDENT:" + studentId);

        historyService.addHistoryEvent(request.getId(), "SYSTEM", "SENT_TO_DEANERY",
                "Solicitud enviavda a decanatura para revisión", "SYSTEM");
    }

    @Override
    public List<ChangeRequestDTO> getAllRequestsByStudent(Integer studentId) {
        message.findStudentOrThrow(studentId);
        return changeRequestMapper.toDTOList(changeRequestRepository.findByStudentId(studentId));
    }

    @Override
    public ChangeRequestDTO getRequestById(Integer studentId, UUID requestId) {
        message.findStudentOrThrow(studentId);
        ChangeRequest request = message.findChangeRequestOrThrow(requestId);
        if (request.getStudentId() != studentId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta solicitud no pertenece al estudiante con id: " + studentId);
        }

        return changeRequestMapper.toDTO(request);
    }

    @Override
    public ChangeRequestDTO updateChangeRequest(Integer studentId, UUID requestId, ChangeRequestUpdateDTO updateDTO) {
        Student student = message.findStudentOrThrow(studentId);
        ChangeRequest request = message.findChangeRequestOrThrow(requestId);

        validateUpdatePermissions(request, studentId);
        updateObservationsIfPresent(request, updateDTO);
        updateTargetSubjectIfPresent(student, request, updateDTO);
        updateTargetGroupIfPresent(studentId, student, request, updateDTO);

        message.ensureStudentOwnsRequest(request, studentId);
        message.ensureRequestPending(request);


        if (updateDTO.getObservations() != null) {
            request.setObservations(updateDTO.getObservations());
        }

        if (updateDTO.getTargetSubject() != null && !updateDTO.getTargetSubject().isBlank()) {
            Subject targetSubject = message.findSubjectOrThrow(updateDTO.getTargetSubject());
            message.ensureCurriculumMatchesStudent(student,  targetSubject);
            request.setTargetSubject(targetSubject.getSubjectId());
            request.setFaculty(curriculumToFacultyMapper.map(targetSubject.getCurriculum()));
        }

        if (updateDTO.getTargetGroup() != null && !updateDTO.getTargetGroup().isBlank()) {
            Group targetGroup = message.findGroupOrThrow(updateDTO.getTargetGroup());
            message.ensureCurriculumMatchesStudentGroup(student,  targetGroup);

            if (request.getTargetGroup() != null) {
                groupRepository.findByGroupId(request.getTargetGroup())
                        .ifPresent(g -> {
                            if (g.getWaitlist() != null) g.getWaitlist().removeIf(id -> id.equals(studentId));
                            groupRepository.save(g);
                        });
            }

            if (targetGroup.getWaitlist() == null) targetGroup.setWaitlist(new ArrayList<>());
            if (!targetGroup.getWaitlist().contains(studentId)) {
                targetGroup.getWaitlist().add(studentId);
                groupRepository.save(targetGroup);
            }
            request.setTargetGroup(targetGroup.getGroupId());
        }

        request.setUpdatedAt(TimeUtils.nowUtc());
        ChangeRequest saved = changeRequestRepository.save(request);

        recordStudentUpdateHistory(saved, studentId);

        return changeRequestMapper.toDTO(saved);
    }

    private void validateUpdatePermissions(ChangeRequest request, Integer studentId) {
        message.ensureStudentOwnsRequest(request, studentId);
        message.ensureRequestPending(request);
    }

    private void updateObservationsIfPresent(ChangeRequest request, ChangeRequestUpdateDTO updateDTO) {
        if (updateDTO.getObservations() != null) {
            request.setObservations(updateDTO.getObservations());
        }
    }

    private void updateTargetSubjectIfPresent(Student student, ChangeRequest request, ChangeRequestUpdateDTO updateDTO) {
        if (updateDTO.getTargetSubject() != null && !updateDTO.getTargetSubject().isBlank()) {
            Subject targetSubject = message.findSubjectOrThrow(updateDTO.getTargetSubject());
            message.ensureCurriculumMatchesStudent(student, targetSubject);

            request.setTargetSubject(targetSubject.getSubjectId());
            request.setFaculty(curriculumToFacultyMapper.map(targetSubject.getCurriculum()));
        }
    }

    private void updateTargetGroupIfPresent(Integer studentId, Student student, ChangeRequest request, ChangeRequestUpdateDTO updateDTO) {
        if (updateDTO.getTargetGroup() == null || updateDTO.getTargetGroup().isBlank()) {
            return;
        }

        Group newTargetGroup = message.findGroupOrThrow(updateDTO.getTargetGroup());
        message.ensureCurriculumMatchesStudentGroup(student, newTargetGroup);

        removeStudentFromPreviousWaitlist(studentId, request.getTargetGroup());
        addStudentToWaitlist(studentId, newTargetGroup);

        request.setTargetGroup(newTargetGroup.getGroupId());
    }

    private void removeStudentFromPreviousWaitlist(Integer studentId, String oldGroupId) {
        if (oldGroupId == null) return;

        groupRepository.findByGroupId(oldGroupId)
                .ifPresent(group -> {
                    if (group.getWaitlist() != null) {
                        group.getWaitlist().removeIf(id -> id.equals(studentId));
                        groupRepository.save(group);
                    }
                });
    }


    private void addStudentToWaitlist(Integer studentId, Group newGroup) {
        if (newGroup.getWaitlist() == null) {
            newGroup.setWaitlist(new ArrayList<>());
        }

        if (!newGroup.getWaitlist().contains(studentId)) {
            newGroup.getWaitlist().add(studentId);
            groupRepository.save(newGroup);
        }
    }

    private void recordStudentUpdateHistory(ChangeRequest request, Integer studentId) {
        historyService.addHistoryEvent(
                request.getId(),
                "STUDENT",
                "STUDENT_UPDATED",
                "Request updated by student",
                "STUDENT:" + studentId
        );
    }

    @Override
    public void deleteChangeRequest(Integer studentId, UUID requestId) {
        Student student = message.findStudentOrThrow(studentId);

        ChangeRequest request = message.findChangeRequestOrThrow(requestId);
        message.ensureStudentOwnsRequest(request, studentId);

        message.ensureRequestPending(request);

        if (request.getTargetGroup() != null) {
            groupRepository.findByGroupId(request.getTargetGroup())
                    .ifPresent(g -> {
                        if (g.getWaitlist() != null) {
                            g.getWaitlist().removeIf(id -> id.equals(studentId));
                            groupRepository.save(g);
                        }
                    });
        }

        if (student.getRequests() != null) {
            student.getRequests().removeIf(r -> r.getId().equals(requestId));
            studentRepository.save(student);
        }

        changeRequestRepository.deleteById(requestId);

        historyService.addHistoryEvent(requestId, "STUDENT", "DELETED",
                "Solicitud eliminada por estudiante", "STUDENT:" + studentId);
    }

    private LocalDateTime addBusinessDays(LocalDateTime start, int businessDays) {
        LocalDateTime d = start;
        int added = 0;
        while (added < businessDays) {
            d = d.plusDays(1);
            DayOfWeek dow = d.getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
                added++;
            }
        }
        return d;
    }

    @Override
    public ChangeRequestDTO requestExceptionalReview(Integer studentId, UUID requestId, String reason) {
        ChangeRequest request = message.findChangeRequestOrThrow(requestId);
        message.ensureStudentOwnsRequest(request, studentId);

        request.setExceptional(true);
        request.setExceptionalReason(reason);
        String requestedBy = "STUDENT:" + studentId;
        request.setExceptionalRequestedBy(requestedBy);
        request.setExceptionalRequestedAt(TimeUtils.nowUtc());
        request.setExceptionalResolutionDeadline(addBusinessDays(TimeUtils.nowUtc(), 5));

        changeRequestRepository.save(request);

        historyService.addHistoryEvent(request.getId(), requestedBy, "EXCEPTION_REQUESTED", reason, requestedBy);

        return changeRequestMapper.toDTO(request);
    }


    @Override
    public ChangeRequestDTO requestExceptional(Integer studentId, UUID requestId, ExceptionalRequestDTO dto) {
        ChangeRequest request = message.findChangeRequestOrThrow(requestId);
        message.ensureStudentOwnsRequest(request, studentId);
        message.ensureRequestPending(request);
        message.ensureExceptionalReasonProvided(dto.getReason());

        request.setExceptional(true);
        request.setExceptionalReason(dto.getReason());
        String requestedBy = "STUDENT:" + studentId;
        request.setExceptionalRequestedBy(requestedBy);
        request.setExceptionalRequestedAt(TimeUtils.nowUtc());
        request.setExceptionalResolutionDeadline(addBusinessDays(TimeUtils.nowUtc(), 5));

        changeRequestRepository.save(request);

        historyService.addHistoryEvent(request.getId(), requestedBy, "EXCEPTION_REQUESTED", dto.getReason(), requestedBy);

        return changeRequestMapper.toDTO(request);
    }


    @Override
    public List<ChangeRequestDTO> getExceptionalRequestsByDeanery(int deaneryId) {
        Deanery deanery = message.findDeaneryOrThrow(deaneryId);
        List<ChangeRequest> requests = changeRequestRepository.findByFacultyAndExceptionalTrue(deanery.getFaculty());
        return requests.stream().map(changeRequestMapper::toDTO).toList();
    }



    @Override
    public List<ChangeRequestDTO> getExceptionalRequestsByStudent(Integer studentId) {
        message.findStudentOrThrow(studentId);
        List<ChangeRequest> requests = changeRequestRepository.findByStudentIdAndExceptionalTrue(studentId);
        return requests.stream().map(changeRequestMapper::toDTO).toList();
    }

    @Override
    public List<ChangeRequestDTO> getAllExceptionalRequests() {
        List<ChangeRequest> requests = changeRequestRepository.findByExceptionalTrue();
        return requests.stream().map(changeRequestMapper::toDTO).toList();
    }

    @Override
    public ChangeRequestDTO approveExceptionalRequest(int approverId, UUID requestId, boolean approve, String observations) {
        var deaneryOpt = deaneryRepository.findById(approverId);
        ChangeRequest request = message.findChangeRequestOrThrow(requestId);

        message.ensureIsExceptional(request);
        message.ensureFacultyMatches(deaneryOpt, request);

        request.setExceptionalApproved(approve);
        String approverTag = deaneryOpt.isPresent() ? "DEANERY:" + approverId : "ADMIN:" + approverId;
        request.setExceptionalApprovedBy(approverTag);
        request.setExceptionalApprovedAt(TimeUtils.nowUtc());

        if (!approve) {
            request.setStatus(RequestStatus.REJECTED);
            request.setUpdatedAt(TimeUtils.nowUtc());
        } else {
            request.setStatus(RequestStatus.APPROVED);
            request.setUpdatedAt(TimeUtils.nowUtc());
            request.setProcessedBy(deaneryOpt.isPresent() ? "DEANERY" : "ADMIN");

            RequestDecisionDTO decisionDto = new RequestDecisionDTO();
            decisionDto.setStatus(RequestStatus.APPROVED);
            decisionDto.setObservations(observations == null ? "" : observations);
            deaneryService.processApprovedRequest(request, decisionDto, approverId);
        }

        changeRequestRepository.save(request);

        historyService.addHistoryEvent(
                request.getId(),
                approverTag,
                approve ? "EXCEPTION_APPROVED" : "EXCEPTION_REJECTED",
                observations == null ? "" : observations,
                approverTag
        );

        return changeRequestMapper.toDTO(request);
    }


    @Override
    public List<ChangeRequestDTO> getExceptionalRequestsByStudentForDeanery(int deaneryId, Integer studentId) {
        Deanery deanery = message.findDeaneryOrThrow(deaneryId);
        message.findStudentOrThrow(studentId);

        List<ChangeRequest> studentExceptional = changeRequestRepository.findByStudentIdAndExceptionalTrue(studentId);
        List<ChangeRequest> filtered = studentExceptional.stream()
                .filter(cr -> cr.getFaculty() == deanery.getFaculty())
                .toList();

        return filtered.stream().map(changeRequestMapper::toDTO).toList();
    }
}