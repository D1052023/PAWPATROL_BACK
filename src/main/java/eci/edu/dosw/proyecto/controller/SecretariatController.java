package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.RequestDatesDTO;
import eci.edu.dosw.proyecto.dtos.RequestDecisionDTO;
import eci.edu.dosw.proyecto.dtos.SecretariatDTO;
import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.services.SecretariatService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Clase controlador para el CRUD de la secretaria académica y sus funcionalidades.
 */

@RestController
@RequestMapping("/secretariat")
@RequiredArgsConstructor
public class SecretariatController {

    private final SecretariatService secretariatService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SecretariatDTO createSecretariat(@RequestBody SecretariatDTO dto) {
        return secretariatService.createSecretariat(dto);
    }

    @GetMapping("/{id}")
    public SecretariatDTO getSecretariat(@PathVariable int id) {
        return secretariatService.getSecretariatById(id);
    }

    @GetMapping
    public List<SecretariatDTO> getAllSecretariats() {
        return secretariatService.getAllSecretariats();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SecretariatDTO updateSecretariat(@PathVariable int id, @RequestBody SecretariatDTO dto) {
        return secretariatService.updateSecretariat(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSecretariat(@PathVariable int id) {
        secretariatService.deleteSecretariat(id);
    }

    @PutMapping("/{id}/request-dates")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRequestDates(@PathVariable int id, @RequestBody RequestDatesDTO dto) {
        secretariatService.updateRequestDates(id, dto.getStartDate(), dto.getEndDate());
    }

    @PostMapping("/requests/{requestId}/respond")
    public ResponseEntity<ChangeRequestDTO> respondRequestBySecretariat(@PathVariable UUID requestId, @RequestBody RequestDecisionDTO decision,
            @RequestParam(required = false) LocalDateTime startDate, @RequestParam(required = false) LocalDateTime endDate) {
        RequestDatesDTO dates = new RequestDatesDTO(startDate, endDate);
        return ResponseEntity.ok(secretariatService.respondRequestBySecretariat(requestId, decision, dates));
    }

    @GetMapping("/requests/faculty/{faculty}/status/{status}")
    public ResponseEntity<List<ChangeRequestDTO>> getRequestsByFacultyAndStatus(@PathVariable Faculty faculty, @PathVariable RequestStatus status) {
        List<ChangeRequestDTO> requests = secretariatService.getRequestsByFacultyAndStatus(faculty, status);
        return ResponseEntity.ok(requests);
    }


}
