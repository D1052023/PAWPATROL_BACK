package eci.edu.dosw.proyecto.util;

import eci.edu.dosw.proyecto.dtos.RequestDatesDTO;
import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.models.*;
import eci.edu.dosw.proyecto.repositories.*;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MessageExceptions {

    private final StudentRepository studentRepository;
    private final ChangeRequestRepository changeRequestRepository;
    private final SecretariatRepository secretariatRepository;
    private final SubjectRepository subjectRepository;
    private final GroupRepository groupRepository;
    private final DeaneryRepository deaneryRepository;

    public Student findStudentOrThrow(Integer studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Estudiante no encontrado con id: " + studentId));
    }

    public ChangeRequest findChangeRequestOrThrow(UUID requestId) {
        return changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Solicitud no encontrada: " + requestId));
    }

    public Secretariat findActiveSecretariatOrThrow(LocalDateTime now) {
        return secretariatRepository
                .findFirstByRequestStartDateBeforeAndRequestEndDateAfter(now, now)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No hay Secretaría con período activo"));
    }

    public Subject findSubjectOrThrow(String subjectId) {
        return subjectRepository.findBySubjectId(subjectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Materia no encontrada: " + subjectId));
    }

    public Group findGroupOrThrow(String groupId) {
        return groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Grupo no encontrado: " + groupId));
    }

    public Deanery findDeaneryOrThrow(int deaneryId) {
        return deaneryRepository.findById(deaneryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Decan@ no encontrado"));
    }

    public void ensureStudentOwnsRequest(ChangeRequest request, Integer studentId) {
        if (!Objects.equals(request.getStudentId(), studentId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Esta solicitud no pertenece al estudiante con id: " + studentId);
        }
    }

    public void ensureRequestPending(ChangeRequest request) {
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Sólo solicitudes en estado PENDING pueden ser procesadas o actualizadas");
        }
    }


    public void ensureCurriculumMatchesStudent(Student student, Subject subject) {
        if (!student.getCurriculum().equals(subject.getCurriculum())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Materia objetivo del pensum no coincide con el pensum del estudiante");
        }
    }

    public void ensureCurriculumMatchesStudentGroup(Student student, Group group) {
        if (!student.getCurriculum().equals(group.getCurriculum())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Grupo objetivo del pensum no coincide con el pensum del estudiante");
        }
    }

    public void ensureIsExceptional(ChangeRequest request) {
        if (!Boolean.TRUE.equals(request.isExceptional())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La solicitud no está marcada como excepcional");
        }
    }

    public void ensureFacultyMatches(Optional<Deanery> deaneryOpt, ChangeRequest request) {
        if (deaneryOpt.isPresent() && request.getFaculty() != deaneryOpt.get().getFaculty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puede gestionar solicitudes de otra facultad");
        }
    }

    public Deanery findDeaneryByFacultyOrThrow(Faculty faculty) {
        return deaneryRepository.findByFaculty(faculty)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró un decan@ para la facultad: " + faculty));
    }

    public void ensureResolutionDeadlineNotExceeded(ChangeRequest request, LocalDateTime now) {
        if (request.getResolutionDeadline() != null && now.isAfter(request.getResolutionDeadline())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "La solicitud excedió el plazo máximo de resolución. Contacte administración.");
        }
    }

    public void ensureDatesProvided(RequestDatesDTO dates) {
        if (dates == null || dates.getStartDate() == null || dates.getEndDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Fechas de periodo obligatorias");
        }
    }

    public void ensureNowWithinDates(LocalDateTime now, RequestDatesDTO dates) {
        if (now.isBefore(dates.getStartDate()) || now.isAfter(dates.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No se pueden gestionar solicitudes fuera del periodo académico habilitado");
        }
    }

    public void ensureDeaneryFacultyMatches(Deanery deanery, ChangeRequest request) {
        if (request.getFaculty() != deanery.getFaculty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No puede gestionar solicitudes de otra facultad");
        }
    }

    public void ensureGroupHasCapacity(Group group) {
        if (group.getCurrentCapacity() >= group.getMaximumCapacity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El grupo ya alcanzó su capacidad máxima");
        }
    }

    public void ensureNowWithinDatesIfPresent(LocalDateTime now, RequestDatesDTO dates) {
        if (dates == null || dates.getStartDate() == null || dates.getEndDate() == null) return;
        ensureNowWithinDates(now, dates);
    }

    public Secretariat findSecretariatOrThrow(int id) {
        return secretariatRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Secretari@ no encontrad@ con id: " + id));
    }

}
