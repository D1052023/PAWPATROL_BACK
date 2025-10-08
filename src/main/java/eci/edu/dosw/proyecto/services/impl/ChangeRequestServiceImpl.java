package eci.edu.dosw.proyecto.services.impl;

import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.enums.Curriculum;
import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.mappers.ChangeRequestMapper;
import eci.edu.dosw.proyecto.models.ChangeRequest;
import eci.edu.dosw.proyecto.models.Student;
import eci.edu.dosw.proyecto.models.Subject;
import eci.edu.dosw.proyecto.models.Group;
import eci.edu.dosw.proyecto.models.Secretariat;
import eci.edu.dosw.proyecto.repositories.ChangeRequestRepository;
import eci.edu.dosw.proyecto.repositories.StudentRepository;
import eci.edu.dosw.proyecto.repositories.SubjectRepository;
import eci.edu.dosw.proyecto.repositories.GroupRepository;
import eci.edu.dosw.proyecto.repositories.SecretariatRepository;
import eci.edu.dosw.proyecto.services.ChangeRequestService;

import eci.edu.dosw.proyecto.services.HistoryService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Clase servicio que implementa la interfaz y maneja la lógica de la solicitud de cambio.
 */
@Service
@RequiredArgsConstructor
public class ChangeRequestServiceImpl implements ChangeRequestService {

    private final ChangeRequestRepository changeRequestRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final GroupRepository groupRepository;
    private final ChangeRequestMapper changeRequestMapper;
    private final SecretariatRepository secretariatRepository;
    private final HistoryService historyService;

    @Override
    public ChangeRequestDTO createChangeRequest(Integer studentId, ChangeRequestDTO requestDTO) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Estudiante no encontrado con id: " + studentId));

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        Secretariat sec = secretariatRepository
                .findFirstByRequestStartDateBeforeAndRequestEndDateAfter(now, now)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No hay Secretaría con período activo"));
        if (sec == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No hay Secretaría activa. Período esperado entre X y Y");
        }

        Subject currentSubject = subjectRepository.findBySubjectId(requestDTO.getCurrentSubject())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Materia actual no encontrada"));
        Subject targetSubject = subjectRepository.findBySubjectId(requestDTO.getTargetSubject())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Materia objetivo no encontrada"));

        Group currentGroup = groupRepository.findByGroupId(requestDTO.getCurrentGroup())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo actual no encontrado"));

        Group targetGroup = groupRepository.findByGroupId(requestDTO.getTargetGroup())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo objetivo no encontrado"));

        if (!student.getCurriculum().equals(targetSubject.getCurriculum())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Materia objetivo del pensum no coincide con el pensum del estudiante");
        }
        if (!student.getCurriculum().equals(targetGroup.getCurriculum())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Grupo objetivo del pensum no coincide con el pensum del estudiante");
        }

        ChangeRequest request = changeRequestMapper.toEntity(requestDTO);
        request.setId(UUID.randomUUID());
        request.setStudentId(student.getId());
        request.setStudentName(student.getName());
        request.setCurrentSubject(currentSubject.getSubjectId());
        request.setCurrentGroup(currentGroup.getGroupId());
        request.setTargetSubject(targetSubject.getSubjectId());
        request.setTargetGroup(targetGroup.getGroupId());
        request.setFaculty(mapCurriculumToFaculty(targetSubject.getCurriculum()));
        request.setStatus(RequestStatus.SENT_TO_DEANERY);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(now);
        request.setUpdatedAt(now);
        int priority = changeRequestRepository.findByStudentId(studentId).size() + 1;
        request.setPriority(priority);


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
        if (!studentRepository.existsById(studentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado con id: " + studentId);
        }
        return changeRequestMapper.toDTOList(changeRequestRepository.findByStudentId(studentId));
    }

    @Override
    public ChangeRequestDTO getRequestById(Integer studentId, UUID requestId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado con id: " + studentId);
        }

        ChangeRequest request = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitación de cambio no encontrada: " + requestId));

        if (request.getStudentId() != studentId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta solicitud no pertenece al estudiante con id: " + studentId);
        }

        return changeRequestMapper.toDTO(request);
    }

    /**
     * Pensum con la facultad correspodiente
     * @param curriculum
     * @return
     */
    private Faculty mapCurriculumToFaculty(Curriculum curriculum) {
        return switch (curriculum) {
            case ISIS_14, ISIS_15 -> Faculty.INGENIERIA_DE_SISTEMAS;
            case ICIV_09, ICIV_10 -> Faculty.INGENIERIA_CIVIL;
            case IBIO_RO -> Faculty.INGENIERIA_BIOMEDICA;
            case IMEC_03, IMEC_02 -> Faculty.INGENIERIA_MECANICA;
            case MATE_04, MATE_03 -> Faculty.MATEMATICAS;
            case ADMI_04, ADMI_05 -> Faculty.ADMINISTRACION_DE_EMPRESAS;
            case ECON_07, ECON_08 -> Faculty.ECONOMIA;
            case IELN_08, IELN_07 -> Faculty.INGENIERIA_ELECTRONICA;
            case IIND_09, IIND_08 -> Faculty.INGENIERIA_INDUSTRIAL;
            case IELC_14, IELC_13 -> Faculty.INGENIERIA_ELECTRICA;
            case IEST_02, IEST_01 -> Faculty.INGENIERIA_ESTADISTICA;
            case IAMB_02, IAMB_01 -> Faculty.INGENIERIA_AMBIENTAL;
            case ICIB_01 -> Faculty.INGENIERIA_DE_CIBERSEGURIDAD;
            case IDIA_01 -> Faculty.INGENIERIA_DE_INTELIGENCIA_ARTIFICIAL;
            case IBTC_01 -> Faculty.INGENIERIA_DE_BIOTECNOLOGIA;
        };
    }

    @Override
    public ChangeRequestDTO updateChangeRequest(Integer studentId, UUID requestId, ChangeRequestDTO dto) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Estudiante no encontrado con id: " + studentId));

        ChangeRequest request = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Solicitud no encontrada: " + requestId));

        if (!Objects.equals(request.getStudentId(), studentId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Esta solicitud no pertenece al estudiante con id: " + studentId);
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Sólo solicitudes en estado PENDING pueden ser actualizadas");
        }

        if (dto.getObservations() != null) {
            request.setObservations(dto.getObservations());
        }

        if (dto.getTargetSubject() != null && !dto.getTargetSubject().isBlank()) {
            Subject targetSubject = subjectRepository.findBySubjectId(dto.getTargetSubject())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Materia objetivo no encontrada"));
            if (!student.getCurriculum().equals(targetSubject.getCurriculum())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Materia objetivo del pensum no coincide con el pensum del estudiante");
            }
            request.setTargetSubject(targetSubject.getSubjectId());
            request.setFaculty(mapCurriculumToFaculty(targetSubject.getCurriculum()));
        }

        if (dto.getTargetGroup() != null && !dto.getTargetGroup().isBlank()) {
            Group targetGroup = groupRepository.findByGroupId(dto.getTargetGroup())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Grupo objetivo no encontrado"));
            if (!student.getCurriculum().equals(targetGroup.getCurriculum())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Grupo objetivo del pensum no coincide con el pensum del estudiante");
            }

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
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Estudiante no encontrado con id: " + studentId));

        ChangeRequest request = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Solicitud no encontrada: " + requestId));

        if (!Objects.equals(request.getStudentId(), studentId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Esta solicitud no pertenece al estudiante con id: " + studentId);
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Sólo solicitudes en estado PENDING pueden ser eliminadas");
        }

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



}
