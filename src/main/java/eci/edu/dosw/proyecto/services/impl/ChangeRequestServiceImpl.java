package eci.edu.dosw.proyecto.services.impl;

import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
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

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
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
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(now);
        request.setUpdatedAt(now);

        if (targetGroup.getWaitlist() == null) {
            targetGroup.setWaitlist(new ArrayList<>());
        }
        if (!targetGroup.getWaitlist().contains(student.getId())) {
            targetGroup.getWaitlist().add(student.getId());
            groupRepository.save(targetGroup);
        }

        int priority = changeRequestRepository.findByStudentId(studentId).size() + 1;
        request.setPriority(priority);

        ChangeRequest savedRequest = changeRequestRepository.save(request);
        student.getRequests().add(savedRequest);
        studentRepository.save(student);

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

}
