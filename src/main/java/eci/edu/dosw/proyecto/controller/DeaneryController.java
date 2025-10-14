package eci.edu.dosw.proyecto.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import eci.edu.dosw.proyecto.dtos.*;
import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.models.ChangeRequestHistory;
import eci.edu.dosw.proyecto.services.*;

/**
 * Clase controlador para gestionar el CRUD de los decanos/as y sus funcionalidades.
 */
@RestController
@RequestMapping("/deaneries")
@RequiredArgsConstructor
public class DeaneryController {

    private final DeaneryService deaneryService;
    private final HistoryService historyService;
    private final GroupService groupService;
    private final StudentService studentService;
    private final ChangeRequestService changeRequestService;

    @Operation(summary = "Crear decano/a")
    @PostMapping
    public ResponseEntity<DeaneryDTO> createDeanery(@RequestBody DeaneryDTO deaneryDTO) {
        return new ResponseEntity<>(deaneryService.createDeanery(deaneryDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todos los decanos/as")
    @GetMapping
    public ResponseEntity<List<DeaneryDTO>> getAllDeaneries() {
        return ResponseEntity.ok(deaneryService.getAllDeaneries());
    }

    @Operation(summary = "Obtener decano/a por ID")
    @GetMapping("/{deaneryId}")
    public ResponseEntity<DeaneryDTO> getDeaneryById(@Parameter(description = "ID del decano o decana") @PathVariable int deaneryId) {
        return ResponseEntity.ok(deaneryService.getDeaneryById(deaneryId));
    }

    @Operation(summary = "Obtener decano/a por facultad")
    @GetMapping("/faculty/{faculty}")
    public DeaneryDTO getDeaneryByFaculty(@Parameter(description = "Facultad a consultar") @PathVariable Faculty faculty) {
        return deaneryService.getDeaneryByFaculty(faculty);
    }

    @Operation(summary = "Actualizar decano/a")
    @PutMapping("/{deaneryId}")
    public ResponseEntity<DeaneryDTO> updateDeanery(@Parameter(description = "ID del decano o decana") @PathVariable int deaneryId, @RequestBody DeaneryDTO deaneryDTO) {
        return ResponseEntity.ok(deaneryService.updateDeanery(deaneryId, deaneryDTO));
    }

    @Operation(summary = "Eliminar decano/a")
    @DeleteMapping("/{deaneryId}")
    public ResponseEntity<Void> deleteDeanery(@Parameter(description = "ID del decano o decana") @PathVariable int deaneryId) {
        deaneryService.deleteDeanery(deaneryId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Responder solicitud")
    @PostMapping("/{deaneryId}/requests/{requestId}/respond")
    public ResponseEntity<ChangeRequestDTO> respondRequestByDeanery(@Parameter(description = "ID del decano o decana") @PathVariable int deaneryId,
            @Parameter(description = "ID de la solicitud a responder") @PathVariable UUID requestId, @RequestBody RequestDecisionDTO decision, @RequestBody RequestDatesDTO dates) {
        return ResponseEntity.ok(deaneryService.respondRequestByDeanery(deaneryId, requestId, decision, dates));
    }

    @Operation(summary = "Responder solicitud (info consolidada)")
    @PostMapping("/{deaneryId}/requests/{requestId}/respondInfo")
    public ResponseEntity<ChangeRequestDTO> respondRequestByDeanery(@Parameter(description = "ID del decano o decana") @PathVariable int deaneryId,
            @Parameter(description = "ID de la solicitud a responder") @PathVariable UUID requestId, @RequestBody RespondRequestInfo body) {
        return ResponseEntity.ok(deaneryService.respondRequestByDeanery(deaneryId, requestId, body.getDecision(), body.getDates()));
    }

    @Operation(summary = "Actualizar solicitud como decano/a")
    @PutMapping("/{deaneryId}/requests/{requestId}")
    public ChangeRequestDTO updateRequestAsDeanery(@Parameter(description = "ID del decano o decana") @PathVariable int deaneryId,
            @Parameter(description = "ID de la solicitud a actualizar") @PathVariable UUID requestId, @RequestBody RequestDecisionDTO decision, @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        RequestDatesDTO dates = new RequestDatesDTO(startDate, endDate);
        return deaneryService.updateRequestAsDeanery(deaneryId, requestId, decision, dates);
    }

    @Operation(summary = "Eliminar solicitud como decano/a")
    @DeleteMapping("/{deaneryId}/requests/{requestId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRequestAsDeanery(@Parameter(description = "ID del decano o decana") @PathVariable int deaneryId, @Parameter(description = "ID de la solicitud a eliminar") @PathVariable UUID requestId) {
        deaneryService.deleteRequestAsDeanery(deaneryId, requestId);
    }

    @Operation(summary = "Solicitudes por facultad y estado")
    @GetMapping("/requests/faculty/{faculty}/status/{status}")
    public ResponseEntity<List<ChangeRequestDTO>> getRequestsByFacultyAndStatus(@Parameter(description = "Facultad a filtrar") @PathVariable Faculty faculty,
            @Parameter(description = "Estado de la solicitud") @PathVariable RequestStatus status) {
        return ResponseEntity.ok(deaneryService.getRequestsByFacultyAndStatus(faculty, status));
    }

    @Operation(summary = "Solicitudes por facultad y prioridad")
    @GetMapping("/requests/faculty/{faculty}/priority/{priority}")
    public ResponseEntity<List<ChangeRequestDTO>> getRequestsByFacultyAndPriority(@Parameter(description = "Facultad a filtrar") @PathVariable Faculty faculty,
            @Parameter(description = "Nivel de prioridad") @PathVariable int priority) {
        return ResponseEntity.ok(deaneryService.getRequestsByFacultyAndPriority(faculty, priority));
    }

    @Operation(summary = "Solicitudes de facultad ordenadas por prioridad")
    @GetMapping("/requests/faculty/{faculty}/priority")
    public ResponseEntity<List<ChangeRequestDTO>> getRequestsByFacultyOrderedByPriority(@Parameter(description = "Facultad a consultar") @PathVariable Faculty faculty) {
        return ResponseEntity.ok(deaneryService.getRequestsByFacultyOrderedByPriority(faculty));
    }

    @Operation(summary = "Buscar solicitudes por filtros")
    @GetMapping("/requests/search")
    public ResponseEntity<List<ChangeRequestDTO>> searchRequests(@RequestParam(required = false) Faculty faculty, @RequestParam(required = false) Integer priority) {
        return ResponseEntity.ok(deaneryService.searchRequestsByFacultyAndOrPriority(faculty, priority));
    }

    @Operation(summary = "Listar todas las solicitudes ordenadas por prioridad")
    @GetMapping("/requests/priority")
    public ResponseEntity<List<ChangeRequestDTO>> getAllOrderedByPriority() {
        return ResponseEntity.ok(deaneryService.getAllRequestsOrderedByPriority());
    }

    @Operation(summary = "Listar solicitudes por prioridad específica")
    @GetMapping("/requests/priority/{priority}")
    public ResponseEntity<List<ChangeRequestDTO>> getAllByPriority(@PathVariable int priority) {
        return ResponseEntity.ok(deaneryService.getAllRequestsByPriority(priority));
    }

    @Operation(summary = "Solicitudes excepcionales de un decano/a")
    @GetMapping("/{deaneryId}/requests/exceptional")
    public ResponseEntity<List<ChangeRequestDTO>> getExceptionalByDeanery(@Parameter(description = "ID del decano o decana") @PathVariable int deaneryId) {
        return ResponseEntity.ok(changeRequestService.getExceptionalRequestsByDeanery(deaneryId));
    }

    @Operation(summary = "Aprobar o rechazar solicitud excepcional")
    @PostMapping("/{deaneryId}/requests/{requestId}")
    public ResponseEntity<ChangeRequestDTO> approveExceptional(@Parameter(description = "ID del decano o decana") @PathVariable int deaneryId,
            @Parameter(description = "ID de la solicitud excepcional") @PathVariable UUID requestId, @RequestParam boolean approve, @RequestParam(required = false) String observations) {
        return ResponseEntity.ok(changeRequestService.approveExceptionalRequest(deaneryId, requestId, approve, observations));
    }

    @Operation(summary = "Solicitudes excepcionales de un estudiante")
    @GetMapping("/{deaneryId}/students/{studentId}/requests/exceptional")
    public ResponseEntity<List<ChangeRequestDTO>> getExceptionalRequestsForStudentByDeanery(@Parameter(description = "ID del decano o decana") @PathVariable int deaneryId,
            @Parameter(description = "ID del estudiante") @PathVariable Integer studentId) {
        return ResponseEntity.ok(changeRequestService.getExceptionalRequestsByStudentForDeanery(deaneryId, studentId));
    }

    @Operation(summary = "Listar todas las solicitudes excepcionales")
    @GetMapping("/requests/exceptional")
    public ResponseEntity<List<ChangeRequestDTO>> getAllExceptionalRequests() {
        return ResponseEntity.ok(changeRequestService.getAllExceptionalRequests());
    }

    @Operation(summary = "Consultar información de estudiante")
    @GetMapping("/students/{studentId}")
    public StudentDTO getStudentInfo(@Parameter(description = "ID del estudiante") @PathVariable int studentId) {
        return studentService.getStudentById(studentId);
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

    @Operation(summary = "Ver historial de solicitud")
    @GetMapping("/requests/{requestId}/history")
    public List<ChangeRequestHistory> getRequestHistory(@Parameter(description = "ID de la solicitud") @PathVariable UUID requestId) {
        return historyService.getHistory(requestId);
    }

}
