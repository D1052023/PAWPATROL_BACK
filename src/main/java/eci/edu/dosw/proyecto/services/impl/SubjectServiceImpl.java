package eci.edu.dosw.proyecto.services.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import eci.edu.dosw.proyecto.services.HistoryService;
import eci.edu.dosw.proyecto.services.SubjectService;
import eci.edu.dosw.proyecto.util.MessageExceptions;
import eci.edu.dosw.proyecto.models.Group;
import eci.edu.dosw.proyecto.models.Student;
import eci.edu.dosw.proyecto.models.Subject;
import eci.edu.dosw.proyecto.models.Teacher;
import eci.edu.dosw.proyecto.dtos.SubjectDTO;
import eci.edu.dosw.proyecto.mappers.SubjectMapper;
import eci.edu.dosw.proyecto.repositories.GroupRepository;
import eci.edu.dosw.proyecto.repositories.StudentRepository;
import eci.edu.dosw.proyecto.repositories.SubjectRepository;
import eci.edu.dosw.proyecto.repositories.TeacherRepository;


/**
 * Clase servicio que implementa la interfaz y maneja la lógica de las materias.
 */
@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;

    private final SubjectMapper subjectMapper;

    private final HistoryService historyService;

    private final MessageExceptions message;
    

    @Override
    public SubjectDTO createSubject(SubjectDTO dto) {
        Teacher teacher = teacherRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesor no encontrado"));

        Subject subject = subjectMapper.toModel(dto);
        subject.setFaculty(teacher.getFaculty());
        subject.setCreatedAt(LocalDateTime.now());
        subject.setUpdatedAt(LocalDateTime.now());

        subject = subjectRepository.save(subject);
        return subjectMapper.toDTO(subject);
    }


    @Override
    public List<SubjectDTO> getAllSubjects() {
        return subjectMapper.toDTOList(subjectRepository.findAll());
    }

    @Override
    public SubjectDTO getSubjectById(String subjectId) {
        return subjectRepository.findBySubjectId(subjectId)
                .map(subjectMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));
    }

    @Override
    public SubjectDTO updateSubject(String subjectId, SubjectDTO dto) {
        if (!subjectRepository.existsBySubjectId(subjectId)) {
            throw new RuntimeException("Materia no encontrada");
        }

        Subject updated = subjectMapper.toModel(dto);
        updated.setSubjectId(subjectId); 
        updated.setUpdatedAt(LocalDateTime.now());
        updated = subjectRepository.save(updated);
        return subjectMapper.toDTO(updated);
    }


    @Override
    public void deleteSubject(String subjectId) {
        subjectRepository.deleteById(subjectId);
    }

    @Override
    public SubjectDTO partialUpdateSubject(String subjectId, SubjectDTO dto) {
        Subject existing = subjectRepository.findBySubjectId(subjectId)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getCredits() != 0) existing.setCredits(dto.getCredits());
        if (dto.getCurriculum() != null) existing.setCurriculum(dto.getCurriculum());
        if (dto.getType() != null) existing.setType(dto.getType());
        if (dto.getSubjectStatus() != null) existing.setSubjectStatus(dto.getSubjectStatus());
        if (dto.getPrerequisites() != null) existing.setPrerequisites(dto.getPrerequisites());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());

        if (dto.getMaximumCapacity() != 0) {
            int totalCuposGrupos = groupRepository.findBySubjectId(existing.getSubjectId())
                    .stream()
                    .mapToInt(Group::getMaximumCapacity)
                    .sum();

            if (dto.getMaximumCapacity() < totalCuposGrupos) {
                throw new RuntimeException("El nuevo cupo máximo es menor que la suma de los grupos existentes");
            }

            existing.setMaximumCapacity(dto.getMaximumCapacity());
        }

        existing.setUpdatedAt(LocalDateTime.now());
        existing = subjectRepository.save(existing);
        return subjectMapper.toDTO(existing);
    }


    @Override
    public List<SubjectDTO> getSubjectsByTeacher(int teacherId) {
        if (!teacherRepository.existsById(teacherId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesor no encontrado");
        }

        List<Subject> subjects = subjectRepository.findByTeacherId(teacherId);
        return subjectMapper.toDTOList(subjects);
    }

    @Override
    public SubjectDTO assignStudentToSubject(String subjectId, int studentId) {
        Subject subject = message.findSubjectOrThrow(subjectId);
        Student student = message.findStudentOrThrow(studentId);

        message.ensureCurriculumMatchesStudent(student, subject);

        if (student.getEnrolledSubjects() != null && student.getEnrolledSubjects().contains(subjectId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estudiante ya está inscrito en esta materia");
        }

        int totalCuposUsados = groupRepository.findBySubjectId(subjectId)
                .stream()
                .mapToInt(g -> g.getCurrentCapacity() != null ? g.getCurrentCapacity() : 0)
                .sum();

        if (totalCuposUsados >= subject.getMaximumCapacity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La materia ya alcanzó su capacidad máxima");
        }

        if (student.getEnrolledSubjects() == null) {
            student.setEnrolledSubjects(new ArrayList<>());
        }
        student.getEnrolledSubjects().add(subjectId);

        studentRepository.save(student);

        historyService.addHistoryEvent(UUID.randomUUID(), "SYSTEM", "STUDENT_ASSIGNED",
                "Estudiante " + studentId + " inscrito en materia " + subjectId, "SYSTEM");

        return subjectMapper.toDTO(subject);
    }

    @Override
    public SubjectDTO removeStudentFromSubject(String subjectId, int studentId) {
        Subject subject = message.findSubjectOrThrow(subjectId);
        Student student = message.findStudentOrThrow(studentId);

        if (student.getEnrolledSubjects() == null || !student.getEnrolledSubjects().contains(subjectId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estudiante no está inscrito en esta materia");
        }

        student.getEnrolledSubjects().removeIf(sid -> sid.equals(subjectId));

        if (student.getSchedule() != null) {
            student.getSchedule().removeIf(se ->
                    se.getSubject() != null && se.getSubject().equals(subjectId));
        }

        studentRepository.save(student);

        historyService.addHistoryEvent(UUID.randomUUID(), "SYSTEM", "STUDENT_REMOVED",
                "Estudiante " + studentId + " retirado de la materia " + subjectId, "SYSTEM");

        return subjectMapper.toDTO(subject);
    }

}
