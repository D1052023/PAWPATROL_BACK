package eci.edu.dosw.proyecto.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import eci.edu.dosw.proyecto.dtos.AcademicPlanDTO;
import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.StudentDTO;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.services.StudentService;

/**
 * Clase controlador para manejar el CRUD de estudiantes y sus funcionalidades.
 */
@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "Crear un nuevo estudiante")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentDTO createStudent(@Parameter(description = "Datos del estudiante a crear") @Valid @RequestBody StudentDTO studentDTO) {
        return studentService.createStudent(studentDTO);
    }

    @Operation(summary = "Listar todos los estudiantes")
    @GetMapping
    public List<StudentDTO> getAllStudents() {
        return studentService.getAllStudents();
    }

    @Operation(summary = "Obtener estudiante por ID")
    @GetMapping("/{id}")
    public StudentDTO getStudentById(@Parameter(description = "ID del estudiante") @PathVariable Integer id) {
        return studentService.getStudentById(id);
    }

    @Operation(summary = "Actualizar estudiante completamente")
    @PutMapping("/{id}")
    public StudentDTO updateStudent(@Parameter(description = "ID del estudiante a actualizar") @PathVariable Integer id,
            @Parameter(description = "Datos actualizados del estudiante") @Valid @RequestBody StudentDTO updatedStudentDTO) {
        return studentService.updateStudent(id, updatedStudentDTO);
    }

    @Operation(summary = "Actualizar parcialmente estudiante")
    @PatchMapping("/{id}")
    public StudentDTO partialUpdateStudent(@Parameter(description = "ID del estudiante a actualizar parcialmente") @PathVariable Integer id,
            @Parameter(description = "Datos a actualizar") @RequestBody StudentDTO studentDTO) {
        return studentService.partialUpdateStudent(id, studentDTO);
    }
    @Operation(summary = "Obtener todas las solicitudes de un estudiante")
    @GetMapping("/{id}/requests")
    public List<ChangeRequestDTO> getStudentRequests(@Parameter(description = "ID del estudiante") @PathVariable int id) {
        return studentService.getStudentRequests(id);
    }

    @Operation(summary = "Obtener solicitudes de un estudiante filtradas por estado")
    @GetMapping("/{id}/requestsStatus")
    public List<ChangeRequestDTO> getStudentRequestsByStatus(@Parameter(description = "ID del estudiante") @PathVariable int id,
            @Parameter(description = "Estado de las solicitudes") @RequestParam(required = false) RequestStatus status) {
        if (status != null) {
            return studentService.getStudentRequestsByStatus(id, status);
        } else {
            return studentService.getStudentRequests(id);
        }
    }

    @Operation(summary = "Eliminar estudiante")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@Parameter(description = "ID del estudiante a eliminar") @PathVariable Integer id) {
        studentService.deleteStudent(id);
    }

    @Operation(summary = "Obtener plan académico de un estudiante")
    @GetMapping("/{studentId}/academic-plan")
    public AcademicPlanDTO getAcademicPlan(@Parameter(description = "ID del estudiante") @PathVariable Integer studentId) {
        return studentService.getAcademicPlan(studentId);
    }
}
