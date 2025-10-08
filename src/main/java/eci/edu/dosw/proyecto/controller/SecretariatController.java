package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.RequestDatesDTO;
import eci.edu.dosw.proyecto.dtos.RequestDecisionDTO;
import eci.edu.dosw.proyecto.dtos.SecretariatDTO;
import eci.edu.dosw.proyecto.dtos.StudentDTO;
import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.models.ChangeRequestHistory;
import eci.edu.dosw.proyecto.services.GroupService;
import eci.edu.dosw.proyecto.services.HistoryService;
import eci.edu.dosw.proyecto.services.SecretariatService;
import eci.edu.dosw.proyecto.services.StudentService;

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
    private final HistoryService historyService;
    private final GroupService groupService;
    private final StudentService studentService;

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
    public ResponseEntity<ChangeRequestDTO> respondRequestBySecretariat(@PathVariable UUID requestId, @RequestBody RequestDecisionDTO decision, @RequestParam(required = false) LocalDateTime startDate, @RequestParam(required = false) LocalDateTime endDate) {
        RequestDatesDTO dates = new RequestDatesDTO(startDate, endDate);
        return ResponseEntity.ok(secretariatService.respondRequestBySecretariat(requestId, decision, dates));
    }

    @GetMapping("/requests/faculty/{faculty}/status/{status}")
    public ResponseEntity<List<ChangeRequestDTO>> getRequestsByFacultyAndStatus(@PathVariable Faculty faculty, @PathVariable RequestStatus status) {
        List<ChangeRequestDTO> requests = secretariatService.getRequestsByFacultyAndStatus(faculty, status);
        return ResponseEntity.ok(requests);
    }


    @GetMapping("/{requestId}/history")
    public List<ChangeRequestHistory> getRequestHistory(@PathVariable UUID requestId) {
        return historyService.getHistory(requestId);
    }

    @GetMapping("/groups/{groupId}/MaxCapacity")
    public int getMaxCapacity(@PathVariable String groupId) {
        return groupService.getMaxCapacity(groupId);
    }

    @GetMapping("/groups/{groupId}/CurrentCapacity")
    public int getCurrentCapacity(@PathVariable String groupId) {
        return groupService.getCurrentCapacity(groupId);
    }

    @GetMapping("/groups/{groupId}/waitingList")
    public List<Integer> getWaitingList(@PathVariable String groupId) {
        return groupService.getWaitlist(groupId);
    }

    @GetMapping("/students/{studentId}")
    public StudentDTO getStudentInfo(@PathVariable int studentId) {
        return studentService.getStudentById(studentId);
    }

}
