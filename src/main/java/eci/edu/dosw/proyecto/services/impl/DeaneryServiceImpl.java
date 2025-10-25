package eci.edu.dosw.proyecto.services.impl;

import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.DeaneryDTO;
import eci.edu.dosw.proyecto.dtos.RequestDatesDTO;
import eci.edu.dosw.proyecto.dtos.RequestDecisionDTO;
import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.mappers.ChangeRequestMapper;
import eci.edu.dosw.proyecto.mappers.DeaneryMapper;
import eci.edu.dosw.proyecto.models.*;
import eci.edu.dosw.proyecto.repositories.ChangeRequestRepository;
import eci.edu.dosw.proyecto.repositories.DeaneryRepository;
import eci.edu.dosw.proyecto.repositories.GroupRepository;
import eci.edu.dosw.proyecto.repositories.StudentRepository;
import eci.edu.dosw.proyecto.services.AlertService;
import eci.edu.dosw.proyecto.services.DeaneryService;
import eci.edu.dosw.proyecto.util.TimeUtils;

import eci.edu.dosw.proyecto.services.HistoryService;
import eci.edu.dosw.proyecto.util.MessageExceptions;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Clase que implementa la interfaz y maneja la lógica de decanatura.
 */
@Service
@RequiredArgsConstructor
public class DeaneryServiceImpl implements DeaneryService {

    private final DeaneryRepository deaneryRepository;
    private final ChangeRequestRepository changeRequestRepository;
    private final DeaneryMapper deaneryMapper;
    private final  ChangeRequestMapper changeRequestMapper;
    private final GroupRepository groupRepository;
    private final AlertService alertService;
    private final StudentRepository studentRepository;
    private final HistoryService historyService;
    private final MessageExceptions message;


    @Override
    public DeaneryDTO createDeanery(DeaneryDTO deaneryDTO) {
        Deanery deanery = deaneryMapper.toEntity(deaneryDTO);
        Deanery savedDeanery = deaneryRepository.save(deanery);
        return deaneryMapper.toDTO(savedDeanery);
    }

    @Override
    public DeaneryDTO getDeaneryById(int id) {
        Deanery deanery = message.findDeaneryOrThrow(id);
        return deaneryMapper.toDTO(deanery);
    }

    @Override
    public DeaneryDTO getDeaneryByFaculty(Faculty faculty) {
        Deanery deanery = message.findDeaneryByFacultyOrThrow(faculty);
        return deaneryMapper.toDTO(deanery);
    }

    @Override
    public List<DeaneryDTO> getAllDeaneries() {
        return deaneryRepository.findAll().stream()
                .map(deaneryMapper::toDTO)
                .toList();
    }

    @Override
    public DeaneryDTO updateDeanery(int id, DeaneryDTO deaneryDTO) {
        Deanery existingDeanery = message.findDeaneryOrThrow(id);

        existingDeanery.setName(deaneryDTO.getName());
        existingDeanery.setEmail(deaneryDTO.getEmail());

        Deanery updatedDeanery = deaneryRepository.save(existingDeanery);
        return deaneryMapper.toDTO(updatedDeanery);
    }

    @Override
    public void deleteDeanery(int id) {
        message.findDeaneryOrThrow(id);
        deaneryRepository.deleteById(id);
    }

    @Override
    public ChangeRequestDTO respondRequestByDeanery(int deaneryId, UUID requestId, RequestDecisionDTO decision, RequestDatesDTO dates) {
        Deanery deanery = message.findDeaneryOrThrow(deaneryId);
        ChangeRequest request = message.findChangeRequestOrThrow(requestId);

        validateDeaneryRequest(deanery, request, dates);

        if (decision.getRequestAdditionalInfo() != null && decision.getRequestAdditionalInfo()) {
            return handleAdditionalInfoRequest(request, decision, deaneryId);
        }

        return handleFinalDecision(request, decision, deaneryId);
    }

    private void validateDeaneryRequest(Deanery deanery, ChangeRequest request, RequestDatesDTO dates) {
        LocalDateTime now = TimeUtils.nowUtc();
        message.ensureResolutionDeadlineNotExceeded(request, now);
        message.ensureDatesProvided(dates);
        message.ensureNowWithinDates(now, dates);
        message.ensureDeaneryFacultyMatches(deanery, request);
        message.ensureRequestPending(request);
    }

    private ChangeRequestDTO handleAdditionalInfoRequest(ChangeRequest request, RequestDecisionDTO decision, int deaneryId) {
        request.setStatus(RequestStatus.REQUEST_ADDITIONAL_INFO);
        request.setUpdatedAt(TimeUtils.nowUtc());
        request.setProcessedBy("DEANERY");

        if (decision.getAdditionalInfoRequestMessage() != null) {
            String prevObs = request.getObservations() == null ? "" : request.getObservations() + " | ";
            request.setObservations(prevObs + "INFO REQUEST: " + decision.getAdditionalInfoRequestMessage());
        }

        changeRequestRepository.save(request);
        recordAdditionalInfoEvent(request, decision, deaneryId);
        return changeRequestMapper.toDTO(request);
    }

    private void recordAdditionalInfoEvent(ChangeRequest request, RequestDecisionDTO decision, int deaneryId) {
        StringBuilder note = new StringBuilder("Additional information requested");
        if (decision.getAdditionalInfoRequestMessage() != null) {
            note.append(": ").append(decision.getAdditionalInfoRequestMessage());
        }
        if (decision.getInfoDueDate() != null) {
            note.append(" (Deadline: ").append(decision.getInfoDueDate().toString()).append(")");
        }

        historyService.addHistoryEvent(
                request.getId(),
                "DEANERY",
                "REQUEST_ADDITIONAL_INFO",
                note.toString(),
                "DEANERY:" + deaneryId
        );
    }

    private ChangeRequestDTO handleFinalDecision(ChangeRequest request, RequestDecisionDTO decision, int deaneryId) {
        request.setStatus(decision.getStatus());
        request.setUpdatedAt(TimeUtils.nowUtc());
        request.setProcessedBy("DEANERY");

        if (decision.getObservations() != null) {
            request.setObservations(decision.getObservations());
        }

        if (decision.getStatus() == RequestStatus.APPROVED) {
            processApprovedRequest(request, decision, deaneryId);
        }

        changeRequestRepository.save(request);
        return changeRequestMapper.toDTO(request);
    }

    @Override
    public void processApprovedRequest(ChangeRequest request, RequestDecisionDTO decision, int deaneryId) {
        Group currentGroup = message.findGroupOrThrow(request.getCurrentGroup());
        Group targetGroup = message.findGroupOrThrow(request.getTargetGroup());
        targetGroup.attach(alertService);

        message.ensureGroupHasCapacity(targetGroup);
        Student student = message.findStudentOrThrow(request.getStudentId());
        message.ensureNoScheduleConflict(student, targetGroup);

        updateGroupCapacities(request, currentGroup, targetGroup);
        updateStudentEnrollment(request, targetGroup);
        recordApprovalHistory(request, decision, deaneryId);
    }

    private void updateGroupCapacities(ChangeRequest request, Group currentGroup, Group targetGroup) {
        targetGroup.enrollStudent();

        if (currentGroup.getWaitlist() != null) {
            currentGroup.getWaitlist().removeIf(id -> id.equals(request.getStudentId()));
        }

        groupRepository.save(currentGroup);
        groupRepository.save(targetGroup);
    }

    private void updateStudentEnrollment(ChangeRequest request, Group targetGroup) {
        Student student = message.findStudentOrThrow(request.getStudentId());

        if (student.getSchedule() == null) {
            student.setSchedule(new ArrayList<>());
        }

        removeOldScheduleEntries(student, request);
        updateEnrolledSubjects(student, request);
        addNewScheduleEntries(student, targetGroup, request);

        studentRepository.save(student);
    }

    private void removeOldScheduleEntries(Student student, ChangeRequest request) {
        student.getSchedule().removeIf(se ->
                se.getSubject() != null && se.getGroup() != null &&
                        se.getSubject().equals(request.getCurrentSubject()) &&
                        se.getGroup().equals(request.getCurrentGroup())
        );
    }

    private void updateEnrolledSubjects(Student student, ChangeRequest request) {
        if (student.getEnrolledSubjects() == null) {
            student.setEnrolledSubjects(new ArrayList<>());
        }

        student.getEnrolledSubjects().removeIf(sid -> sid.equals(request.getCurrentSubject()));

        if (!student.getEnrolledSubjects().contains(request.getTargetSubject())) {
            student.getEnrolledSubjects().add(request.getTargetSubject());
        }
    }

    private void addNewScheduleEntries(Student student, Group targetGroup, ChangeRequest request) {
        String subjectId = (targetGroup.getSubjectId() != null && !targetGroup.getSubjectId().isBlank())
                ? targetGroup.getSubjectId()
                : request.getTargetSubject();

        if (targetGroup.getSchedule() != null && !targetGroup.getSchedule().isEmpty()) {
            for (ScheduleEntry se : targetGroup.getSchedule()) {
                ScheduleEntry newEntry = new ScheduleEntry(
                        subjectId,
                        targetGroup.getGroupId(),
                        se.getDay(),
                        se.getFrom(),
                        se.getSemester(),
                        se.getClassroom(),
                        se.getTo(),
                        se.getStatus()
                );

                boolean exists = student.getSchedule().stream().anyMatch(existing ->
                        existing.getSubject() != null && existing.getGroup() != null &&
                                existing.getSubject().equals(newEntry.getSubject()) &&
                                existing.getGroup().equals(newEntry.getGroup()) &&
                                existing.getDay().equals(newEntry.getDay()) &&
                                existing.getFrom().equals(newEntry.getFrom()) &&
                                existing.getTo().equals(newEntry.getTo())
                );

                if (!exists) {
                    student.getSchedule().add(newEntry);
                }
            }
        } else {
            ScheduleEntry basic = new ScheduleEntry();
            basic.setSubject(subjectId);
            basic.setGroup(request.getTargetGroup());
            student.getSchedule().add(basic);
        }
    }

    private void recordApprovalHistory(ChangeRequest request, RequestDecisionDTO decision, int deaneryId) {
        historyService.addHistoryEvent(
                request.getId(),
                "DEANERY",
                RequestStatus.APPROVED.name(),
                decisionToNote(decision),
                "DEANERY:" + deaneryId
        );

        historyService.addHistoryEvent(
                request.getId(),
                "DEANERY",
                "STUDENT_SCHEDULE_UPDATED",
                "Student schedule updated after approval",
                "DEANERY:" + deaneryId
        );
    }


    private String decisionToNote(RequestDecisionDTO decision) {
        String obs = decision.getObservations() == null ? "" : decision.getObservations();
        return "Decision: " + (decision.getStatus() == null ? "UNKNOWN" : decision.getStatus().name()) + ". " + obs;
    }


    @Override
    public List<ChangeRequestDTO> getRequestsByFacultyAndStatus(Faculty faculty, RequestStatus status) {
        List<ChangeRequest> requests = changeRequestRepository.findByFacultyAndStatus(faculty, status);
        return requests.stream()
                .map(changeRequestMapper::toDTO)
                .toList();
    }

    @Override
    public ChangeRequestDTO updateRequestAsDeanery(int deaneryId, UUID requestId, RequestDecisionDTO decision, RequestDatesDTO dates) {
        Deanery deanery = message.findDeaneryOrThrow(deaneryId);
        ChangeRequest request = message.findChangeRequestOrThrow(requestId);

        LocalDateTime now = TimeUtils.nowUtc();
        message.ensureNowWithinDatesIfPresent(now, dates);
        message.ensureDeaneryFacultyMatches(deanery, request);
        message.ensureRequestPending(request);


        if (decision.getStatus() != null) {
            request.setStatus(decision.getStatus());
            request.setUpdatedAt(TimeUtils.nowUtc());
            request.setProcessedBy("DEANERY");

            if (decision.getStatus() == RequestStatus.APPROVED) {
                processApprovedRequest(request, decision, deaneryId);
            }

            changeRequestRepository.save(request);
            historyService.addHistoryEvent(request.getId(), "DEANERY",
                    decision.getStatus().name(), decisionToNote(decision), "DEANERY:" + deaneryId);
        } else {
            request.setUpdatedAt(TimeUtils.nowUtc());
            changeRequestRepository.save(request);
            historyService.addHistoryEvent(request.getId(), "DEANERY", "UPDATED",
                    "Solicitud actualizada por decanatura", "DEANERY:" + deaneryId);
        }

        return changeRequestMapper.toDTO(request);
    }

    @Override
    public void deleteRequestAsDeanery(int deaneryId, UUID requestId) {
        Deanery deanery = message.findDeaneryOrThrow(deaneryId);

        ChangeRequest request = message.findChangeRequestOrThrow(requestId);

        message.ensureDeaneryFacultyMatches(deanery, request);
        message.ensureRequestPending(request);

        if (request.getTargetGroup() != null) {
            groupRepository.findByGroupId(request.getTargetGroup())
                    .ifPresent(g -> {
                        if (g.getWaitlist() != null) {
                            g.getWaitlist().removeIf(id -> id.equals(request.getStudentId()));
                            groupRepository.save(g);
                        }
                    });
        }

        changeRequestRepository.deleteById(requestId);
        historyService.addHistoryEvent(requestId, "DEANERY", "DELETED", "Solicitud eliminada por decanatura", "DEANERY:" + deaneryId);
    }


    @Override
    public List<ChangeRequestDTO> getRequestsByFacultyOrderedByPriority(Faculty faculty) {
        List<ChangeRequest> requests = changeRequestRepository.findByFacultyOrderByPriorityAsc(faculty);
        return requests.stream().map(changeRequestMapper::toDTO).toList();
    }

    @Override
    public List<ChangeRequestDTO> getRequestsByFacultyAndPriority(Faculty faculty, int priority) {
        List<ChangeRequest> requests = changeRequestRepository.findByFacultyAndPriorityOrderByPriorityAsc(faculty, priority);
        return requests.stream().map(changeRequestMapper::toDTO).toList();
    }

    @Override
    public List<ChangeRequestDTO> getAllRequestsOrderedByPriority() {
        List<ChangeRequest> requests = changeRequestRepository.findAllByOrderByPriorityAsc();
        return requests.stream().map(changeRequestMapper::toDTO).toList();
    }

    @Override
    public List<ChangeRequestDTO> getAllRequestsByPriority(int priority) {
        List<ChangeRequest> requests = changeRequestRepository.findByPriorityOrderByPriorityAsc(priority);
        return requests.stream().map(changeRequestMapper::toDTO).toList();
    }

    @Override
    public List<ChangeRequestDTO> searchRequestsByFacultyAndOrPriority(Faculty faculty, Integer priority) {
        if (faculty != null && priority != null) {
            return getRequestsByFacultyAndPriority(faculty, priority);
        } else if (faculty != null) {
            return getRequestsByFacultyOrderedByPriority(faculty);
        } else if (priority != null) {
            return getAllRequestsByPriority(priority);
        } else {
            return getAllRequestsOrderedByPriority();
        }
    }

}


