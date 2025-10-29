package eci.edu.dosw.proyecto.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import eci.edu.dosw.proyecto.dtos.*;
import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.models.ChangeRequestHistory;
import eci.edu.dosw.proyecto.services.GroupService;
import eci.edu.dosw.proyecto.services.HistoryService;
import eci.edu.dosw.proyecto.services.SecretariatService;
import eci.edu.dosw.proyecto.services.StudentService;

/**
 * Clase controlador para el CRUD de la secretaria académica y sus funcionalidades.
 */

@RestController
@RequestMapping("/secretariat")
@RequiredArgsConstructor
public class SecretariatController {

    private final SecretariatService secretariatService;
    private final HistoryService historyService;
    private final GroupService groupService;
    private final StudentService studentService;

    @Operation(summary = "Crear secretaría académica")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SecretariatDTO createSecretariat(@RequestBody SecretariatDTO dto) {
        return secretariatService.createSecretariat(dto);
    }

    @Operation(summary = "Obtener secretaría académica por ID")
    @GetMapping("/{id}")
    public SecretariatDTO getSecretariat(@Parameter(description = "ID de la secretaría") @PathVariable int id) {
        return secretariatService.getSecretariatById(id);
    }

    @Operation(summary = "Listar todas las secretarías académicas")
    @GetMapping
    public List<SecretariatDTO> getAllSecretariats() {
        return secretariatService.getAllSecretariats();
    }

    @Operation(summary = "Actualizar secretaría académica")
    @PutMapping("/{id}")
    public SecretariatDTO updateSecretariat(@Parameter(description = "ID de la secretaría") @PathVariable int id, @RequestBody SecretariatDTO dto) {
        return secretariatService.updateSecretariat(id, dto);
    }

    @Operation(summary = "Eliminar secretaría académica")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSecretariat(@Parameter(description = "ID de la secretaría") @PathVariable int id) {
        secretariatService.deleteSecretariat(id);
    }

    @Operation(summary = "Actualizar fechas de solicitud")
    @PutMapping("/{id}/request-dates")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRequestDates(@Parameter(description = "ID de la secretaría") @PathVariable int id, @RequestBody RequestDatesDTO dto) {
        secretariatService.updateRequestDates(id, dto.getStartDate(), dto.getEndDate());
    }

    @Operation(summary = "Responder solicitud")
    @PostMapping("/requests/{requestId}/respond")
    public ChangeRequestDTO respondRequestBySecretariat(@Parameter(description = "ID de la solicitud") @PathVariable UUID requestId, @RequestBody RequestDecisionDTO decision,
            @RequestParam(required = false) LocalDateTime startDate, @RequestParam(required = false) LocalDateTime endDate) {
        RequestDatesDTO dates = new RequestDatesDTO(startDate, endDate);
        return secretariatService.respondRequestBySecretariat(requestId, decision, dates);
    }

    @Operation(summary = "Responder solicitud (info consolidada)")
    @PostMapping("/requests/{requestId}/respondInfo")
    public ChangeRequestDTO respondRequestBySecretariat(@Parameter(description = "ID de la solicitud") @PathVariable UUID requestId, @RequestBody RespondRequestInfo body) {
        return secretariatService.respondRequestBySecretariat(requestId, body.getDecision(), body.getDates());
    }

    @Operation(summary = "Obtener solicitudes por facultad y estado")
    @GetMapping("/requests/faculty/{faculty}/status/{status}")
    public List<ChangeRequestDTO> getRequestsByFacultyAndStatus(@Parameter(description = "Facultad") @PathVariable Faculty faculty,
            @Parameter(description = "Estado de la solicitud") @PathVariable RequestStatus status) {
        return secretariatService.getRequestsByFacultyAndStatus(faculty, status);
    }

    @Operation(summary = "Obtener solicitudes por facultad ordenadas por prioridad")
    @GetMapping("/requests/faculty/{faculty}/priority")
    public List<ChangeRequestDTO> getRequestsByFacultyOrderedByPriority(@Parameter(description = "Facultad") @PathVariable Faculty faculty) {
        return secretariatService.getRequestsByFacultyOrderedByPriority(faculty);
    }

    @Operation(summary = "Buscar solicitudes por filtros")
    @GetMapping("/requests/search")
    public List<ChangeRequestDTO> searchRequests(@RequestParam(required = false) Faculty faculty, @RequestParam(required = false) Integer priority) {
        return secretariatService.searchRequestsByFacultyAndOrPriority(faculty, priority);
    }

    @Operation(summary = "Listar todas las solicitudes ordenadas por prioridad")
    @GetMapping("/requests/priority")
    public List<ChangeRequestDTO> getAllOrderedByPriority() {
        return secretariatService.getAllRequestsOrderedByPriority();
    }

    @Operation(summary = "Listar solicitudes por prioridad específica")
    @GetMapping("/requests/priority/{priority}")
    public List<ChangeRequestDTO> getAllByPriority(@Parameter(description = "Prioridad") @PathVariable int priority) {
        return secretariatService.getAllRequestsByPriority(priority);
    }

    @Operation(summary = "Consultar historial de solicitud")
    @GetMapping("/{requestId}/history")
    public List<ChangeRequestHistory> getRequestHistory(@Parameter(description = "ID de la solicitud") @PathVariable UUID requestId) {
        return historyService.getHistory(requestId);
    }

    @Operation(summary = "Consultar capacidad máxima de grupo")
    @GetMapping("/groups/{groupId}/MaxCapacity")
    public int getMaxCapacity(@Parameter(description = "ID del grupo") @PathVariable String groupId) {
        return groupService.getMaxCapacity(groupId);
    }

    @Operation(summary = "Consultar capacidad actual de grupo")
    @GetMapping("/groups/{groupId}/CurrentCapacity")
    public int getCurrentCapacity(@Parameter(description = "ID del grupo") @PathVariable String groupId) {
        return groupService.getCurrentCapacity(groupId);
    }

    @Operation(summary = "Consultar lista de espera de grupo")
    @GetMapping("/groups/{groupId}/waitingList")
    public List<Integer> getWaitingList(@Parameter(description = "ID del grupo") @PathVariable String groupId) {
        return groupService.getWaitlist(groupId);
    }

    @Operation(summary = "Consultar información de estudiante")
    @GetMapping("/students/{studentId}")
    public StudentDTO getStudentInfo(@Parameter(description = "ID del estudiante") @PathVariable int studentId) {
        return studentService.getStudentById(studentId);
    }

    @Operation(summary = "Actualizar solicitud como secretaría")
    @PutMapping("/requests/{requestId}")
    public ChangeRequestDTO updateRequestAsSecretariat(@Parameter(description = "ID de la solicitud") @PathVariable UUID requestId,@RequestBody RequestDecisionDTO decision,
            @RequestParam(required = false) LocalDateTime startDate, @RequestParam(required = false) LocalDateTime endDate) {
        RequestDatesDTO dates = new RequestDatesDTO(startDate, endDate);
        return secretariatService.updateRequestAsSecretariat(requestId, decision, dates);
    }

    @Operation(summary = "Eliminar solicitud como secretaría")
    @DeleteMapping("/requests/{requestId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRequestAsSecretariat(@Parameter(description = "ID de la solicitud") @PathVariable UUID requestId) {
        secretariatService.deleteRequestAsSecretariat(requestId);
    }
}