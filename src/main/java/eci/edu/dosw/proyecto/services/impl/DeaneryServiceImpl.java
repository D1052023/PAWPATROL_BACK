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

import eci.edu.dosw.proyecto.services.HistoryService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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


    @Override
    public DeaneryDTO createDeanery(DeaneryDTO deaneryDTO) {
        Deanery deanery = deaneryMapper.toEntity(deaneryDTO);
        Deanery savedDeanery = deaneryRepository.save(deanery);
        return deaneryMapper.toDTO(savedDeanery);
    }

    @Override
    public DeaneryDTO getDeaneryById(int id) {
        Deanery deanery = deaneryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Decan@ no encontrado con id: " + id));
        return deaneryMapper.toDTO(deanery);
    }

    @Override
    public DeaneryDTO getDeaneryByFaculty(Faculty faculty) {
        Deanery deanery = deaneryRepository.findByFaculty(faculty)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró un decan@ para la facultad: " + faculty));
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
        Deanery existingDeanery = deaneryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Decan@ no encontrado con id: " + id));

        existingDeanery.setName(deaneryDTO.getName());
        existingDeanery.setEmail(deaneryDTO.getEmail());

        Deanery updatedDeanery = deaneryRepository.save(existingDeanery);
        return deaneryMapper.toDTO(updatedDeanery);
    }

    @Override
    public void deleteDeanery(int id) {
        if (!deaneryRepository.existsById(id)) {
            throw new RuntimeException("Decan@ no encontrado con id: " + id);
        }
        deaneryRepository.deleteById(id);
    }

    @Override
    public ChangeRequestDTO respondRequestByDeanery(int deaneryId, UUID requestId, RequestDecisionDTO decision, RequestDatesDTO dates) {
        Deanery deanery = deaneryRepository.findById(deaneryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Decanato no encontrado"));

        ChangeRequest request = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(dates.getStartDate()) || now.isAfter(dates.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No se pueden gestionar solicitudes fuera del periodo académico habilitado");
        }

        if (request.getFaculty() != deanery.getFaculty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No puede gestionar solicitudes de otra facultad");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La solicitud ya fue procesada");
        }


        request.setStatus(decision.getStatus());
        request.setUpdatedAt(LocalDateTime.now());
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

    private void processApprovedRequest(ChangeRequest request, RequestDecisionDTO decision, int deaneryId) {

        Group currentGroup = groupRepository.findByGroupId(request.getCurrentGroup())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Grupo actual no encontrado: " + request.getCurrentGroup()));

        Group targetGroup = groupRepository.findByGroupId(request.getTargetGroup())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Grupo destino no encontrado: " + request.getTargetGroup()));

        targetGroup.attach(alertService);

        if (targetGroup.getCurrentCapacity() >= targetGroup.getMaximumCapacity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El grupo ya alcanzó su capacidad máxima");
        }


        targetGroup.setCurrentCapacity(targetGroup.getCurrentCapacity() + 1);
        targetGroup.enrollStudent();

        if (currentGroup.getWaitlist() != null) {
            currentGroup.getWaitlist().removeIf(id -> id.equals(request.getStudentId()));
        }

        groupRepository.save(currentGroup);
        groupRepository.save(targetGroup);

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Estudiante no encontrado: " + request.getStudentId()));

        if (student.getSchedule() == null) {
            student.setSchedule(new ArrayList<>());
        }

        student.getSchedule().removeIf(se ->
                se.getSubject() != null && se.getGroup() != null &&
                        se.getSubject().equals(request.getCurrentSubject()) &&
                        se.getGroup().equals(request.getCurrentGroup())
        );

        if (student.getEnrolledSubjects() == null) {
            student.setEnrolledSubjects(new ArrayList<>());
        }

        student.getEnrolledSubjects().removeIf(sid -> sid.equals(request.getCurrentSubject()));

        if (!student.getEnrolledSubjects().contains(request.getTargetSubject())) {
            student.getEnrolledSubjects().add(request.getTargetSubject());
        }

        String subjectIdFromGroup = targetGroup.getSubjectId();
        String subjectId = (subjectIdFromGroup != null && !subjectIdFromGroup.isBlank())
                ? subjectIdFromGroup
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

        studentRepository.save(student);
        historyService.addHistoryEvent(request.getId(), "DEANERY", RequestStatus.APPROVED.name(),
                decisionToNote(decision), "DEANERY:" + deaneryId);

        historyService.addHistoryEvent(request.getId(), "DEANERY", "STUDENT_SCHEDULE_UPDATED",
                "Horario actualizado al aprobar la solicitud", "DEANERY:" + deaneryId);
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

}
