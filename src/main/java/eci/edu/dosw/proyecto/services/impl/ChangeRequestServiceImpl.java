package eci.edu.dosw.proyecto.services.impl;

import eci.edu.dosw.proyecto.dtos.*;
import eci.edu.dosw.proyecto.util.CurriculumToFacultyMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @Override
    public ChangeRequestDTO createChangeRequest(Integer studentId, ChangeRequestCreateDTO createDto) {
        Student student = message.findStudentOrThrow(studentId);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        Subject currentSubject = message.findSubjectOrThrow(createDto.getCurrentSubject());
        Subject targetSubject = message.findSubjectOrThrow(createDto.getTargetSubject());

        Group currentGroup = message.findGroupOrThrow(createDto.getCurrentGroup());
        Group targetGroup = message.findGroupOrThrow(createDto.getTargetGroup());

        message.ensureCurriculumMatchesStudent(student, targetSubject);
        message.ensureCurriculumMatchesStudentGroup(student, targetGroup);

        ChangeRequest request = new ChangeRequest();
        request.setCreatedAt(now);
        request.setUpdatedAt(now);
        request.setResolutionDeadline(addBusinessDays(now, 5));
        request.setId(UUID.randomUUID());
        request.setStudentId(student.getId());
        request.setStudentName(student.getName());
        request.setCurrentSubject(currentSubject.getSubjectId());
        request.setCurrentGroup(currentGroup.getGroupId());
        request.setTargetSubject(targetSubject.getSubjectId());
        request.setTargetGroup(targetGroup.getGroupId());
        request.setFaculty(curriculumToFacultyMapper.map(targetSubject.getCurriculum()));
        request.setStatus(RequestStatus.SENT_TO_DEANERY);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(now);
        request.setUpdatedAt(now);
        int priority = changeRequestRepository.findByStudentId(studentId).size() + 1;
        request.setPriority(priority);

        if (Boolean.TRUE.equals(createDto.getExceptional())) {

            message.ensureExceptionalReasonProvided(createDto.getExceptionalReason());
            request.setExceptional(true);
            request.setExceptionalReason(createDto.getExceptionalReason());
            request.setExceptionalRequestedBy("STUDENT:" + studentId);
            request.setExceptionalRequestedAt(now);
            request.setExceptionalResolutionDeadline(addBusinessDays(now, 5));
        } else {
            request.setExceptional(false);
        }


        ChangeRequest savedRequest = changeRequestRepository.save(request);

        if (student.getRequests() == null) {
            student.setRequests(new ArrayList<>());
        }
        student.getRequests().add(savedRequest);
        studentRepository.save(student);

        historyService.addHistoryEvent(savedRequest.getId(), "STUDENT", "CREATED",
                "Solicitud creada por estudiante", "STUDENT:" + studentId);

        historyService.addHistoryEvent(savedRequest.getId(), "SYSTEM", "SENT_TO_DEANERY",
                "Solicitud enviada a decanatura para revisión", "SYSTEM");


        if (targetGroup.getWaitlist() == null) {
            targetGroup.setWaitlist(new ArrayList<>());
        }

        if (!targetGroup.getWaitlist().contains(student.getId())) {
            targetGroup.getWaitlist().add(student.getId());
            groupRepository.save(targetGroup);
        }

        return changeRequestMapper.toDTO(savedRequest);
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

        request.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        ChangeRequest saved = changeRequestRepository.save(request);

        historyService.addHistoryEvent(saved.getId(), "STUDENT", "STUDENT_UPDATED",
                "Solicitud actualizada por estudiante", "STUDENT:" + studentId);

        return changeRequestMapper.toDTO(saved);
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
        request.setExceptionalRequestedAt(LocalDateTime.now());
        request.setExceptionalResolutionDeadline(addBusinessDays(LocalDateTime.now(), 5));

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
        request.setExceptionalRequestedAt(LocalDateTime.now(ZoneOffset.UTC));
        request.setExceptionalResolutionDeadline(addBusinessDays(LocalDateTime.now(ZoneOffset.UTC), 5));

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
        request.setExceptionalApprovedAt(LocalDateTime.now());

        if (!approve) {
            request.setStatus(RequestStatus.REJECTED);
            request.setUpdatedAt(LocalDateTime.now());
        } else {
            request.setStatus(RequestStatus.APPROVED);
            request.setUpdatedAt(LocalDateTime.now());
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
