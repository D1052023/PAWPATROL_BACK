package eci.edu.dosw.proyecto.util;

import eci.edu.dosw.proyecto.dtos.RequestDatesDTO;
import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.models.*;
import eci.edu.dosw.proyecto.repositories.*;
import eci.edu.dosw.proyecto.enums.RequestStatus;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Clase central que agrupa busquedas y validaciones para lanzar execpciones
 */
@Component
@RequiredArgsConstructor
public class MessageExceptions extends RuntimeException {

    private static final Logger LOG = LoggerFactory.getLogger(MessageExceptions.class);
    private static final String STUDENT_NOT_FOUND = "Estudiante no encontrado con id: %s";
    private static final String REQUEST_NOT_FOUND = "Solicitud no encontrada: %s";
    private static final String SECRETARIAT_INACTIVE = "No hay Secretaría con período activo";
    private static final String SUBJECT_NOT_FOUND = "Materia no encontrada: %s";
    private static final String GROUP_NOT_FOUND = "Grupo no encontrado: %s";
    private static final String DEANERY_NOT_FOUND = "Decan@ no encontrado";
    private static final String ONLY_PENDING = "Sólo solicitudes en estado PENDING pueden ser procesadas o actualizadas";
    private static final String NOT_OWN_REQUEST = "Esta solicitud no pertenece al estudiante con id: %s";
    private static final String CURRICULUM_MISMATCH = "Materia objetivo del pensum no coincide con el pensum del estudiante";
    private static final String GROUP_CURRICULUM_MISMATCH = "Grupo objetivo del pensum no coincide con el pensum del estudiante";
    private static final String NOT_EXCEPTIONAL = "La solicitud no está marcada como excepcional";
    private static final String FORBIDDEN_FACULTY = "No puede gestionar solicitudes de otra facultad";
    private static final String RESOLUTION_DEADLINE_EXCEEDED = "La solicitud excedió el plazo máximo de resolución. Contacte administración.";
    private static final String DATES_REQUIRED = "Fechas de periodo obligatorias";
    private static final String OUTSIDE_PERIOD = "No se pueden gestionar solicitudes fuera del periodo académico habilitado";
    private static final String GROUP_FULL = "El grupo ya alcanzó su capacidad máxima";
    private static final String SUBJECT_ID_REQUIRED = "El identificador de la materia es obligatorio";
    private static final String SUBJECT_NO_TOTAL_CAPACITY = "La materia no tiene cupo total asignado, no se pueden crear grupos todavía";
    private static final String TOTAL_GROUP_CAPACITY_EXCEEDED = "La suma de los cupos de los grupos excede el cupo total permitido para la materia";
    private static final String TEACHER_NOT_FOUND = "Profesor no encontrado";
    private static final String GROUP_HAS_TEACHER = "El grupo ya tiene un profesor asignado";
    private static final String GROUP_NO_TEACHER = "El grupo no tiene un profesor asignado";
    private static final String STUDENT_ALREADY_IN_GROUP = "Estudiante ya está inscrito en el grupo: %s";
    private static final String STUDENT_NOT_IN_GROUP = "Estudiante no está inscrito en el grupo: %s";
    private static final String CANNOT_ENROLL_ATOMIC = "No se puede inscribir: el grupo está lleno o se actualizó simultáneamente";
    private static final String CANNOT_REMOVE_ATOMIC = "No se pudo retirar: capacidad ya en 0 o modificación concurrente";

    private final StudentRepository studentRepository;
    private final ChangeRequestRepository changeRequestRepository;
    private final SecretariatRepository secretariatRepository;
    private final SubjectRepository subjectRepository;
    private final GroupRepository groupRepository;
    private final DeaneryRepository deaneryRepository;
    private final TeacherRepository teacherRepository;


    public Student findStudentOrThrow(Integer studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> notFound(HttpStatus.NOT_FOUND, STUDENT_NOT_FOUND.formatted(studentId)));
    }

    public ChangeRequest findChangeRequestOrThrow(UUID requestId) {
        return changeRequestRepository.findById(requestId)
                .orElseThrow(() -> notFound(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND.formatted(requestId)));
    }

    public Secretariat findActiveSecretariatOrThrow(LocalDateTime now) {
        return secretariatRepository
                .findFirstByRequestStartDateBeforeAndRequestEndDateAfter(now, now)
                .orElseThrow(() -> badRequest(SECRETARIAT_INACTIVE));
    }

    public Subject findSubjectOrThrow(String subjectId) {
        return subjectRepository.findBySubjectId(subjectId)
                .orElseThrow(() -> notFound(HttpStatus.NOT_FOUND, SUBJECT_NOT_FOUND.formatted(subjectId)));
    }

    public Group findGroupOrThrow(String groupId) {
        return groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> notFound(HttpStatus.NOT_FOUND, GROUP_NOT_FOUND.formatted(groupId)));
    }

    public Deanery findDeaneryOrThrow(int deaneryId) {
        return deaneryRepository.findById(deaneryId)
                .orElseThrow(() -> notFound(HttpStatus.NOT_FOUND, DEANERY_NOT_FOUND));
    }


    public void ensureStudentOwnsRequest(ChangeRequest request, Integer studentId) {
        if (!Objects.equals(request.getStudentId(), studentId)) {
            throw badRequest(NOT_OWN_REQUEST.formatted(studentId));
        }
    }

    public void ensureRequestPending(ChangeRequest request) {
        if (request.getStatus() != RequestStatus.PENDING) {
            throw badRequest(ONLY_PENDING);
        }
    }

    public void ensureCurriculumMatchesStudent(Student student, Subject subject) {
        if (!student.getCurriculum().equals(subject.getCurriculum())) {
            throw badRequest(CURRICULUM_MISMATCH);
        }
    }

    public void ensureCurriculumMatchesStudentGroup(Student student, Group group) {
        if (!student.getCurriculum().equals(group.getCurriculum())) {
            throw badRequest(GROUP_CURRICULUM_MISMATCH);
        }
    }

    public void ensureIsExceptional(ChangeRequest request) {
        if (!Boolean.TRUE.equals(request.isExceptional())) {
            throw badRequest(NOT_EXCEPTIONAL);
        }
    }

    public void ensureFacultyMatches(Optional<Deanery> deaneryOpt, ChangeRequest request) {
        if (deaneryOpt.isPresent() && request.getFaculty() != deaneryOpt.get().getFaculty()) {
            throw forbidden(FORBIDDEN_FACULTY);
        }
    }

    public Deanery findDeaneryByFacultyOrThrow(Faculty faculty) {
        return deaneryRepository.findByFaculty(faculty)
                .orElseThrow(() -> notFound(HttpStatus.NOT_FOUND, "No se encontró un decan@ para la facultad: " + faculty));
    }

    public void ensureResolutionDeadlineNotExceeded(ChangeRequest request, LocalDateTime now) {
        if (request.getResolutionDeadline() != null && now.isAfter(request.getResolutionDeadline())) {
            throw forbidden(RESOLUTION_DEADLINE_EXCEEDED);
        }
    }

    public void ensureDatesProvided(RequestDatesDTO dates) {
        if (dates == null || dates.getStartDate() == null || dates.getEndDate() == null) {
            throw badRequest(DATES_REQUIRED);
        }
    }

    public void ensureNowWithinDates(LocalDateTime now, RequestDatesDTO dates) {
        if (now.isBefore(dates.getStartDate()) || now.isAfter(dates.getEndDate())) {
            throw forbidden(OUTSIDE_PERIOD);
        }
    }

    public void ensureDeaneryFacultyMatches(Deanery deanery, ChangeRequest request) {
        if (request.getFaculty() != deanery.getFaculty()) {
            throw forbidden(FORBIDDEN_FACULTY);
        }
    }

    public void ensureGroupHasCapacity(Group group) {
        if (group.getCurrentCapacity() >= group.getMaximumCapacity()) {
            throw badRequest(GROUP_FULL);
        }
    }

    public void ensureNowWithinDatesIfPresent(LocalDateTime now, RequestDatesDTO dates) {
        if (dates == null || dates.getStartDate() == null || dates.getEndDate() == null) return;
        ensureNowWithinDates(now, dates);
    }

    public Secretariat findSecretariatOrThrow(int id) {
        return secretariatRepository.findById(id)
                .orElseThrow(() -> notFound(HttpStatus.NOT_FOUND, "Secretari@ no encontrad@ con id: " + id));
    }


    public void ensureSubjectIdProvided(String subjectId) {
        if (subjectId == null || subjectId.isBlank()) {
            throw badRequest(SUBJECT_ID_REQUIRED);
        }
    }

    public void ensureSubjectCurriculumMatchesGroup(Subject subject, Group group) {
        if (subject == null || group == null) return;
        if (!subject.getCurriculum().equals(group.getCurriculum())) {
            throw badRequest(GROUP_CURRICULUM_MISMATCH);
        }
    }

    public void ensureSubjectHasTotalCapacity(Subject subject) {
        if (subject == null) return;
        Integer max = subject.getMaximumCapacity();
        if (max == null || max <= 0) {
            throw badRequest(SUBJECT_NO_TOTAL_CAPACITY);
        }
    }

    public void ensureTotalGroupCapacityNotExceeded(int totalCuposGrupos, int newGroupCapacity, Integer subjectMaximumCapacity) {
        int subjectMax = subjectMaximumCapacity == null ? 0 : subjectMaximumCapacity;
        if (totalCuposGrupos + newGroupCapacity > subjectMax) {
            throw badRequest(TOTAL_GROUP_CAPACITY_EXCEEDED);
        }
    }

    public void ensureTeacherExistsOrThrow(int teacherId) {
        if (!teacherRepository.existsById(teacherId)) {
            throw notFound(HttpStatus.NOT_FOUND, TEACHER_NOT_FOUND);
        }
    }

    public void ensureGroupHasNoTeacherAssigned(Group group) {
        if (group != null && group.getTeacher() != 0) {
            throw badRequest(GROUP_HAS_TEACHER);
        }
    }

    public void ensureGroupHasTeacherAssigned(Group group) {
        if (group == null) return;
        if (group.getTeacher() == 0) {
            throw badRequest(GROUP_NO_TEACHER);
        }
    }


    public void ensureStudentNotInGroup(Student student, String groupId) {
        if (studentInGroup(student, groupId)) {
            throw badRequest(STUDENT_ALREADY_IN_GROUP.formatted(groupId));
        }
    }

    public void ensureStudentIsInGroup(Student student, String groupId) {
        if (!studentInGroup(student, groupId)) {
            throw badRequest(STUDENT_NOT_IN_GROUP.formatted(groupId));
        }
    }

    private boolean studentInGroup(Student student, String groupId) {
        return student != null && student.getSchedule() != null &&
                student.getSchedule().stream().anyMatch(se -> groupId.equals(se.getGroup()));
    }

    public void ensureGroupHasAvailableCapacity(Group group) {
        if (group.getCurrentCapacity() >= group.getMaximumCapacity()) {
            throw badRequest(CANNOT_ENROLL_ATOMIC);
        }
    }

    public void ensureGroupHasCapacityGreaterThanZero(Group group) {
        if (group.getCurrentCapacity() <= 0) {
            throw badRequest(CANNOT_REMOVE_ATOMIC);
        }
    }

    public void ensureAtomicUpdateSucceeded(Object updated, String errorMessage) {
        if (updated == null) {
            throw badRequest(errorMessage);
        }
    }


    private ResponseStatusException badRequest(String message) {
        LOG.debug("BadRequest: {}", message);
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseStatusException forbidden(String message) {
        LOG.debug("Forbidden: {}", message);
        return new ResponseStatusException(HttpStatus.FORBIDDEN, message);
    }

    private ResponseStatusException notFound(HttpStatus status, String message) {
        LOG.debug("NotFound: {}", message);
        return new ResponseStatusException(status, message);
    }
}
