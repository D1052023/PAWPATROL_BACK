package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dtos.*;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.models.ChangeRequestHistory;
import eci.edu.dosw.proyecto.services.ChangeRequestService;
import eci.edu.dosw.proyecto.services.HistoryService;
import eci.edu.dosw.proyecto.services.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * Clase controlador para gestionar el CRUD de las solicitudes de cambio de los estudiantes.
 */
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ChangeRequestController {

    private final ChangeRequestService changeRequestService;
    private final StudentService studentService;
    private final HistoryService historyService;

    @Operation(summary = "Crear una nueva solicitud de cambio")
    @PostMapping("/students/{studentId}")
    public ChangeRequestDTO createRequest(@PathVariable Integer studentId, @Valid @RequestBody ChangeRequestCreateDTO createDto) {
        return changeRequestService.createChangeRequest(studentId, createDto);
    }

    @Operation(summary = "Obtener todas las solicitudes de un estudiante")
    @GetMapping("/students/{studentId}")
    public List<ChangeRequestDTO> getRequestsByStudent(@PathVariable Integer studentId) {
        return changeRequestService.getAllRequestsByStudent(studentId);
    }

    @Operation(summary = "Obtener solicitudes excepcionales de un estudiante")
    @GetMapping("/students/{studentId}/exceptionalRequest")
    public List<ChangeRequestDTO> getExceptionalByStudent(@PathVariable Integer studentId) {
        return changeRequestService.getExceptionalRequestsByStudent(studentId);
    }

    @Operation(summary = "Filtrar solicitudes por estado")
    @GetMapping("/students/{studentId}/status")
    public List<ChangeRequestDTO> getRequestsByStudentAndStatus(@PathVariable Integer studentId, @RequestParam(required = false) RequestStatus status) {
        if (status == null) {
            return changeRequestService.getAllRequestsByStudent(studentId);
        } else {
            return studentService.getStudentRequestsByStatus(studentId, status);
        }
    }

    @Operation(summary = "Obtener horario actual del estudiante")
    @GetMapping("/students/{studentId}/schedule/current")
    public StudentDTO getCurrentSchedule(@Parameter(description = "ID del estudiante") @PathVariable Integer studentId, @Parameter(description = "semestre actual")@RequestParam(required = false) Integer semester) {
        if (semester == null) {
            StudentDTO student = studentService.getStudentById(studentId);
            semester = student.getSemester();
        }
        return studentService.getStudentSchedule(studentId, semester);
    }

    @Operation(summary = "Obtener horario anterior del estudiante")
    @GetMapping("/students/{studentId}/schedule/previous")
    public StudentDTO getPreviousSchedule(@Parameter(description = "ID del estudiante") @PathVariable Integer studentId, @Parameter(description = "semestre actual") @RequestParam int semester) {
        return studentService.getStudentSchedule(studentId, semester);
    }

    @Operation(summary = "Solicitar revisión excepcional")
    @PostMapping("/{requestId}/students/{studentId}/requestExceptional")
    public ChangeRequestDTO requestExceptional(@Parameter(description = "ID del estudiante") @PathVariable Integer studentId, @Parameter(description = "id de la solicitud") @PathVariable UUID requestId, @Parameter(description = "Razón de la solicitud") @RequestParam(required = false) String reason) {
        return changeRequestService.requestExceptionalReview(studentId, requestId, reason);
    }

    @Operation(summary = "Consultar una solicitud específica")
    @GetMapping("/{requestId}/students/{studentId}")
    public ChangeRequestDTO getRequestById(@Parameter(description = "ID del estudiante") @PathVariable Integer studentId, @Parameter(description = "id de la solicitud") @PathVariable UUID requestId) {
        return changeRequestService.getRequestById(studentId, requestId);
    }

    @Operation(summary = "Consultar historial de una solicitud")
    @GetMapping("/{requestId}/students/{studentId}/history")
    public List<ChangeRequestHistory> getRequestHistory(@Parameter(description = "ID del estudiante") @PathVariable Integer studentId, @Parameter(description = "id de la solicitud") @PathVariable UUID requestId) {
        return historyService.getHistory(requestId);
    }

    @Operation(summary = "Actualizar una solicitud de cambio")
    @PutMapping("/{requestId}/students/{studentId}")
    public ChangeRequestDTO updateRequest(@PathVariable Integer studentId, @PathVariable UUID requestId, @Valid @RequestBody ChangeRequestUpdateDTO updateDto) {
        return changeRequestService.updateChangeRequest(studentId, requestId, updateDto);
    }
    @Operation(summary = "Eliminar una solicitud de cambio")
    @DeleteMapping("/{requestId}/students/{studentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRequest(@Parameter(description = "ID del estudiante") @PathVariable Integer studentId, @Parameter(description = "id de la solicitud") @PathVariable UUID requestId) {
        changeRequestService.deleteChangeRequest(studentId, requestId);
    }
}