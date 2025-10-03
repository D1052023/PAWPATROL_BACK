package eci.edu.dosw.proyecto.services.impl;

import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.ScheduleEntryDTO;
import eci.edu.dosw.proyecto.dtos.StudentDTO;
import eci.edu.dosw.proyecto.enums.AcademicTrafficLight;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.mappers.ChangeRequestMapper;
import eci.edu.dosw.proyecto.mappers.ScheduleEntryMapper;
import eci.edu.dosw.proyecto.mappers.StudentMapper;
import eci.edu.dosw.proyecto.models.Student;
import eci.edu.dosw.proyecto.repositories.ChangeRequestRepository;
import eci.edu.dosw.proyecto.repositories.StudentRepository;
import eci.edu.dosw.proyecto.services.StudentService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Clase servicio que implementa la interfaz y maneja la lógica del estudiante.
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final ScheduleEntryMapper scheduleEntryMapper;
    private final ChangeRequestRepository changeRequestRepository;
    private final ChangeRequestMapper changeRequestMapper;


    @Override
    public List<StudentDTO> getAllStudents() {
        return studentMapper.toDTOList(studentRepository.findAll());
    }

    @Override
    public StudentDTO getStudentById(Integer id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estudiante no encontrado con id: " + id));
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
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estudiante no encontrado con id: " + id));

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
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estudiante no encontrado con id: " + id));
        studentRepository.delete(student);
    }

    @Override
    public StudentDTO partialUpdateStudent(Integer id, StudentDTO dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estudiante no encontrado con id: " + id));

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
        Student student = studentRepository.findByEmail(email);
        if (student == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado con email: " + email);
        }
        return student;
    }

    private AcademicTrafficLight calculateTrafficLight(List<ScheduleEntryDTO> schedule) {
        boolean hasRed = schedule.stream()
                                .anyMatch(s -> s.getStatus() == AcademicTrafficLight.RED);
        if (hasRed) return AcademicTrafficLight.RED;
        boolean hasBlue = schedule.stream()
                                .anyMatch(s -> s.getStatus() == AcademicTrafficLight.BLUE);
        if (hasBlue) return AcademicTrafficLight.BLUE;
        return AcademicTrafficLight.GREEN;
    }

    @Override
    public StudentDTO getStudentSchedule(int studentId, int semester) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado"));

        List<ScheduleEntryDTO> schedule = student.getSchedule().stream()
        .filter(s -> s.getSemester() == semester)
        .map(scheduleEntryMapper::toDTO) 
        .toList();


        AcademicTrafficLight trafficLight = calculateTrafficLight(schedule);

        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setCareer(student.getCareer());
        dto.setSemester(semester);
        dto.setCurriculum(student.getCurriculum());
        dto.setSchedule(schedule);
        dto.setAcademicTrafficLight(trafficLight);

        return dto;
    }

    @Override
    public List<ChangeRequestDTO> getStudentRequests(int studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estudiante no encontrado con id: " + studentId));
        List<ChangeRequestDTO> requests = changeRequestRepository.findByStudentId(studentId)
                .stream()
                .map(changeRequestMapper::toDTO)
                .toList();

        return requests;
    }

    @Override
    public List<ScheduleEntryDTO> getStudentScheduleByTrafficLight(int studentId, int semester, AcademicTrafficLight light) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado"));

        return student.getSchedule().stream()
                .filter(s -> s.getSemester() == semester) 
                .map(scheduleEntryMapper::toDTO)
                .filter(dto -> dto.getStatus() == light) 
                .toList();
    }

    @Override
    public List<ChangeRequestDTO> getStudentRequestsByStatus(int studentId, RequestStatus status) {
        studentRepository.findById(studentId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Estudiante no encontrado con id: " + studentId));

        return changeRequestRepository.findByStudentId(studentId).stream()
                                    .filter(request -> request.getStatus() == status)
                                    .map(changeRequestMapper::toDTO)
                                    .toList();
    }


}
