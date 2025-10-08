package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.StudentDTO;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.services.ChangeRequestService;
import eci.edu.dosw.proyecto.services.StudentService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Clase controlador para manejar el CRUD de estudaintes y sus funcionalidades.
 */
@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final ChangeRequestService changeRequestService;

    public StudentController(StudentService studentService, ChangeRequestService changeRequestService) {
        this.studentService = studentService;
        this.changeRequestService = changeRequestService;
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


    @GetMapping("/{id}/requests")
    public List<ChangeRequestDTO> getStudentRequests(@PathVariable int id) {
        return studentService.getStudentRequests(id);
    }

    @GetMapping("/{id}/requestsStatus")
    public List<ChangeRequestDTO> getStudentRequestsByStatus(@PathVariable int id, @RequestParam(required = false) RequestStatus status) {
        if (status != null) {
            return studentService.getStudentRequestsByStatus(id, status);
        } else {
            return studentService.getStudentRequests(id);
        }
    }

    @PutMapping("/{requestId}")
    public ChangeRequestDTO updateRequest(@PathVariable Integer studentId,
                                          @PathVariable UUID requestId,
                                          @RequestBody ChangeRequestDTO requestDTO) {
        return changeRequestService.updateChangeRequest(studentId, requestId, requestDTO);
    }

    @DeleteMapping("/{requestId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRequest(@PathVariable Integer studentId, @PathVariable UUID requestId) {
        changeRequestService.deleteChangeRequest(studentId, requestId);
    }
}
