package eci.edu.dosw.proyecto.services.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import eci.edu.dosw.proyecto.util.TimeUtils;
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


/**
 * Clase servicio que implementa la interfaz y maneja la lógica de las materias.
 */
@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;

    private final SubjectMapper subjectMapper;

    private final HistoryService historyService;

    private final MessageExceptions message;
    

    @Override
    public SubjectDTO createSubject(SubjectDTO dto) {
        Teacher teacher = message.findTeacherOrThrow(dto.getTeacherId());

        Subject subject = subjectMapper.toModel(dto);
        subject.setFaculty(teacher.getFaculty());
        subject.setCreatedAt(TimeUtils.nowUtc());
        subject.setUpdatedAt(TimeUtils.nowUtc());

        subject = subjectRepository.save(subject);
        return subjectMapper.toDTO(subject);
    }


    @Override
    public List<SubjectDTO> getAllSubjects() {
        return subjectMapper.toDTOList(subjectRepository.findAll());
    }

    @Override
    public SubjectDTO getSubjectById(String subjectId) {
        Subject subject = message.findSubjectOrThrow(subjectId);
        return subjectMapper.toDTO(subject);
    }

    @Override
    public SubjectDTO updateSubject(String subjectId, SubjectDTO dto) {
        message.findSubjectOrThrow(subjectId);
        Subject updated = subjectMapper.toModel(dto);
        updated.setSubjectId(subjectId); 
        updated.setUpdatedAt(TimeUtils.nowUtc());
        updated = subjectRepository.save(updated);
        return subjectMapper.toDTO(updated);
    }


    @Override
    public void deleteSubject(String subjectId) {
        subjectRepository.deleteById(subjectId);
    }

    @Override
    public SubjectDTO partialUpdateSubject(String subjectId, SubjectDTO dto) {
        Subject existing = message.findSubjectOrThrow(subjectId);

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

            message.ensureNewSubjectMaxNotSmallerThanGroupSum(totalCuposGrupos, dto.getMaximumCapacity());
            existing.setMaximumCapacity(dto.getMaximumCapacity());
        }

        existing.setUpdatedAt(TimeUtils.nowUtc());
        existing = subjectRepository.save(existing);
        return subjectMapper.toDTO(existing);
    }


    @Override
    public List<SubjectDTO> getSubjectsByTeacher(int teacherId) {
        message.ensureTeacherExistsOrThrow(teacherId);
        List<Subject> subjects = subjectRepository.findByTeacherId(teacherId);
        return subjectMapper.toDTOList(subjects);
    }

    @Override
    public SubjectDTO assignStudentToSubject(String subjectId, int studentId) {
        Subject subject = message.findSubjectOrThrow(subjectId);
        Student student = message.findStudentOrThrow(studentId);

        message.ensureCurriculumMatchesStudent(student, subject);
        message.ensureStudentNotEnrolledInSubject(student, subjectId);

        int totalCuposUsados = groupRepository.findBySubjectId(subjectId)
                .stream()
                .mapToInt(g -> g.getCurrentCapacity() != null ? g.getCurrentCapacity() : 0)
                .sum();

        message.ensureSubjectHasAvailableCapacity(subject, totalCuposUsados);

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
        message.ensureStudentIsEnrolledInSubject(student, subjectId);

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