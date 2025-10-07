package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.services.ChangeRequestService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Clase controlador para el CRUD de las solicitudes de cambio de un estudiante y sus funcionalidades.
 */
@RestController
@RequestMapping("/students/{studentId}/requests")
@RequiredArgsConstructor
public class ChangeRequestController {

    private final ChangeRequestService changeRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChangeRequestDTO createRequest(@PathVariable Integer studentId, @Valid @RequestBody ChangeRequestDTO requestDTO) {
        return changeRequestService.createChangeRequest(studentId, requestDTO);
    }

    @GetMapping
    public List<ChangeRequestDTO> getRequestsByStudent(@PathVariable Integer studentId) {
        return changeRequestService.getAllRequestsByStudent(studentId);
    }

    @GetMapping("/{requestId}")
    public ChangeRequestDTO getRequestById(@PathVariable Integer studentId, @PathVariable UUID requestId) {
        return changeRequestService.getRequestById(studentId, requestId);
    }

    @GetMapping("/status")
    public List<ChangeRequestDTO> getRequestsByStudentAndStatus(@PathVariable Integer studentId, @RequestParam(required = false) RequestStatus status) {
        return changeRequestService.getAllRequestsByStudent(studentId);
    }

}
