package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.ScheduleEntryDTO;
import eci.edu.dosw.proyecto.dtos.StudentDTO;
import eci.edu.dosw.proyecto.enums.AcademicTrafficLight;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.services.StudentService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Clase controlador para manejar el CRUD de estudaintes y sus funcionalidades.
 */
@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<StudentDTO> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public StudentDTO getStudentById(@PathVariable Integer id) {
        return studentService.getStudentById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentDTO createStudent(@Valid @RequestBody StudentDTO studentDTO) {
        return studentService.createStudent(studentDTO);
    }

    @PutMapping("/{id}")
    public StudentDTO updateStudent(@PathVariable Integer id, @Valid @RequestBody StudentDTO updatedStudentDTO) {
        return studentService.updateStudent(id, updatedStudentDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable Integer id) {
        studentService.deleteStudent(id);
    }

    @PatchMapping("/{id}")
    public StudentDTO partialUpdateStudent(@PathVariable Integer id, @RequestBody StudentDTO studentDTO) {
        return studentService.partialUpdateStudent(id, studentDTO);
    }

    @GetMapping("/{id}/schedule")
    public StudentDTO getStudentSchedule(@PathVariable Integer id, @RequestParam int semester) {
        return studentService.getStudentSchedule(id, semester);
    }

    @GetMapping("/{id}/requests")
    public List<ChangeRequestDTO> getStudentRequests(@PathVariable int id) {
        return studentService.getStudentRequests(id);
    }

    @GetMapping("/{id}/scheduleTrafficLight")
    public List<ScheduleEntryDTO> getStudentScheduleByTrafficLight(@PathVariable int id, @RequestParam int semester, @RequestParam AcademicTrafficLight light) {
        return studentService.getStudentScheduleByTrafficLight(id, semester, light);
    }

    @GetMapping("/{id}/requestsStatus")
    public List<ChangeRequestDTO> getStudentRequestsByStatus(@PathVariable int id, @RequestParam(required = false) RequestStatus status) {
        if (status != null) {
            return studentService.getStudentRequestsByStatus(id, status);
        } else {
            return studentService.getStudentRequests(id);
        }
    }    
}
