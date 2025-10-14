package eci.edu.dosw.proyecto.services.impl;

import eci.edu.dosw.proyecto.dtos.AcademicPlanDTO;
import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.ScheduleEntryDTO;
import eci.edu.dosw.proyecto.dtos.StudentDTO;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.mappers.AcademicPlanMapper;
import eci.edu.dosw.proyecto.mappers.ChangeRequestMapper;
import eci.edu.dosw.proyecto.mappers.ScheduleEntryMapper;
import eci.edu.dosw.proyecto.mappers.StudentMapper;
import eci.edu.dosw.proyecto.models.ScheduleEntry;
import eci.edu.dosw.proyecto.models.Student;
import eci.edu.dosw.proyecto.models.Subject;
import eci.edu.dosw.proyecto.repositories.ChangeRequestRepository;
import eci.edu.dosw.proyecto.repositories.StudentRepository;
import eci.edu.dosw.proyecto.repositories.SubjectRepository;
import eci.edu.dosw.proyecto.services.StudentService;
import eci.edu.dosw.proyecto.util.MessageExceptions;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Clase servicio que implementa la interfaz y maneja la lógica del estudiante.
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final ChangeRequestRepository changeRequestRepository;
    private final AcademicPlanMapper academicPlanMapper;
    private final SubjectRepository subjectRepository;
    private final ChangeRequestMapper changeRequestMapper;
    private final ScheduleEntryMapper scheduleEntryMapper;
    private final MessageExceptions message;


    @Override
    public List<StudentDTO> getAllStudents() {
        return studentMapper.toDTOList(studentRepository.findAll());
    }

    @Override
    public StudentDTO getStudentById(Integer id) {
        Student student = message.findStudentOrThrow(id);
        return studentMapper.toDTO(student);
    }

    @Override
    public StudentDTO createStudent(StudentDTO studentDTO) {
        Student student = studentMapper.toEntity(studentDTO);
        Student saved = studentRepository.save(student);
        return studentMapper.toDTO(saved);
    }

    @Override
    public StudentDTO updateStudent(Integer id, StudentDTO updatedStudentDTO) {
        Student existingStudent = message.findStudentOrThrow(id);
        existingStudent.setName(updatedStudentDTO.getName());
        existingStudent.setEmail(updatedStudentDTO.getEmail());
        existingStudent.setCareer(updatedStudentDTO.getCareer());
        existingStudent.setSemester(updatedStudentDTO.getSemester());
        existingStudent.setCurriculum(updatedStudentDTO.getCurriculum());
        existingStudent.setAcademicTrafficLight(updatedStudentDTO.getAcademicTrafficLight());

        Student saved = studentRepository.save(existingStudent);
        return studentMapper.toDTO(saved);
    }

    @Override
    public void deleteStudent(Integer id) {
        Student student = message.findStudentOrThrow(id);
        studentRepository.delete(student);
    }

    @Override
    public StudentDTO partialUpdateStudent(Integer id, StudentDTO dto) {
        Student student = message.findStudentOrThrow(id);
        if (dto.getName() != null) student.setName(dto.getName());
        if (dto.getEmail() != null) student.setEmail(dto.getEmail());
        if (dto.getCareer() != null) student.setCareer(dto.getCareer());
        if (dto.getSemester() > 0) student.setSemester(dto.getSemester());
        if (dto.getCurriculum() != null) student.setCurriculum(dto.getCurriculum());
        if (dto.getAcademicTrafficLight() != null) student.setAcademicTrafficLight(dto.getAcademicTrafficLight());

        return studentMapper.toDTO(studentRepository.save(student));
    }

    @Override
    public Student getStudentByEmail(String email) {
        return message.findStudentByEmailOrThrow(email);
    }

    @Override
    public List<ChangeRequestDTO> getStudentRequests(int studentId) {
       message.findStudentOrThrow(studentId);
        List<ChangeRequestDTO> requests = changeRequestRepository.findByStudentId(studentId)
                .stream()
                .map(changeRequestMapper::toDTO)
                .toList();

        return requests;
    }

    @Override
    public List<ChangeRequestDTO> getStudentRequestsByStatus(int studentId, RequestStatus status) {
        message.findStudentOrThrow(studentId);
        return changeRequestRepository.findByStudentId(studentId).stream()
                                    .filter(request -> request.getStatus() == status)
                                    .map(changeRequestMapper::toDTO)
                                    .toList();
    }

    @Override
    public StudentDTO getStudentSchedule(int studentId, int semester) {
        Student student = message.findStudentOrThrow(studentId);
        List<ScheduleEntryDTO> schedule = (student.getSchedule() == null ? List.<ScheduleEntry>of() : student.getSchedule()).stream()
                .filter(s -> s.getSemester() == semester)
                .map(scheduleEntryMapper::toDTO)
                .toList();

        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setCareer(student.getCareer());
        dto.setSemester(semester);
        dto.setCurriculum(student.getCurriculum());
        dto.setSchedule(schedule);


        if (student.getRequests() != null) {
            dto.setRequests(changeRequestMapper.toDTOList(student.getRequests()));
        }

        return dto;
    }


    @Override
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }
    
    @Override
    public AcademicPlanDTO getAcademicPlan(Integer studentId) {

        Student student = message.findStudentOrThrow(studentId);
        AcademicPlanDTO dto = academicPlanMapper.toDto(student);

        List<Subject> planSubjects = Collections.emptyList();
        if (student.getCurriculum() != null) {
            planSubjects = Optional.ofNullable(subjectRepository.findByCurriculum(student.getCurriculum()))
                    .orElse(Collections.emptyList());
        }

        int totalCourses = planSubjects.size();
        dto.setTotalCoursesInPlan(totalCourses);

        int creditsPlanSum = planSubjects.stream()
                .map(Subject::getCredits)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        dto.setCreditsPlan(creditsPlanSum == 0 ? null : (double) creditsPlanSum);
        List<String> approved = student.getApprovedSubjects() == null ? Collections.emptyList() : student.getApprovedSubjects();
        List<String> enrolled = student.getEnrolledSubjects() == null ? Collections.emptyList() : student.getEnrolledSubjects();
        dto.setApprovedCourses(approved.size());
        dto.setEnrolledSubjectsCount(enrolled.size());
        dto.setEnrolledSubjectIds(new ArrayList<>(enrolled));


        Double approvedCredits = student.getApprovedCredits();
        if (approvedCredits == null) {
            int acc = 0;
            for (String sid : approved) {
                Optional<Subject> opt = subjectRepository.findBySubjectId(sid);
                if (opt.isPresent()) {
                    Integer c = opt.get().getCredits();
                    if (c != null) acc += c;
                }
            }
            approvedCredits = acc == 0 ? null : (double) acc;
        }

        dto.setApprovedCredits(approvedCredits);
        dto.setPendingCourses(Math.max(0, totalCourses - dto.getApprovedCourses()));

        if (dto.getCreditsPlan() != null && dto.getApprovedCredits() != null) {
            dto.setPendingCredits(Math.max(0.0, dto.getCreditsPlan() - dto.getApprovedCredits()));
        } else {
            dto.setPendingCredits(null);
        }

        int enrolledCount = dto.getEnrolledSubjectsCount() == null ? 0 : dto.getEnrolledSubjectsCount();
        double progress = totalCourses == 0 ? 0.0 : ((double) enrolledCount * 100.0) / totalCourses;


        dto.setProgressPercent(Math.round(progress * 100.0) / 100.0);
        Set<String> done = new HashSet<>();
        done.addAll(approved);
        done.addAll(enrolled);
        List<String> missing = planSubjects.stream()
                .map(Subject::getSubjectId)
                .filter(sid -> !done.contains(sid))
                .collect(Collectors.toList());
        dto.setMissingSubjectIds(missing);

        return dto;
    }
}
