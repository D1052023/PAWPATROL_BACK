package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.DeaneryDTO;
import eci.edu.dosw.proyecto.dtos.RequestDatesDTO;
import eci.edu.dosw.proyecto.dtos.RequestDecisionDTO;
import eci.edu.dosw.proyecto.dtos.StudentDTO;
import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.models.ChangeRequestHistory;
import eci.edu.dosw.proyecto.services.DeaneryService;
import eci.edu.dosw.proyecto.services.GroupService;
import eci.edu.dosw.proyecto.services.HistoryService;
import eci.edu.dosw.proyecto.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Clase controlador para el CRUD de los decan@s y sus funcionalidades.
 */
@RestController
@RequestMapping("/api/deaneries")
@RequiredArgsConstructor
public class DeaneryController {

    private final DeaneryService deaneryService;
    private final HistoryService historyService;
    private final GroupService groupService;
    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<DeaneryDTO> createDeanery(@RequestBody DeaneryDTO deaneryDTO) {
        return new ResponseEntity<>(deaneryService.createDeanery(deaneryDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeaneryDTO> getDeaneryById(@PathVariable int id) {
        return ResponseEntity.ok(deaneryService.getDeaneryById(id));
    }

    @GetMapping("/faculty/{faculty}")
    public DeaneryDTO getDeaneryByFaculty(@PathVariable Faculty faculty) {
        return deaneryService.getDeaneryByFaculty(faculty);
    }


    @GetMapping
    public ResponseEntity<List<DeaneryDTO>> getAllDeaneries() {
        return ResponseEntity.ok(deaneryService.getAllDeaneries());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeaneryDTO> updateDeanery(@PathVariable int id, @RequestBody DeaneryDTO deaneryDTO) {
        return ResponseEntity.ok(deaneryService.updateDeanery(id, deaneryDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeanery(@PathVariable int id) {
        deaneryService.deleteDeanery(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{deaneryId}/requests/{requestId}/respond")
    public ResponseEntity<ChangeRequestDTO> respondRequestByDeanery(@PathVariable int deaneryId, @PathVariable UUID requestId,
            @RequestBody RequestDecisionDTO decision, @RequestBody RequestDatesDTO dates) {
        return ResponseEntity.ok(
                deaneryService.respondRequestByDeanery(deaneryId, requestId, decision, dates)
        );
    }

    @GetMapping("/requests/faculty/{faculty}/status/{status}")
    public ResponseEntity<List<ChangeRequestDTO>> getRequestsByFacultyAndStatus(
            @PathVariable Faculty faculty,
            @PathVariable RequestStatus status
    ) {
        List<ChangeRequestDTO> requests = deaneryService.getRequestsByFacultyAndStatus(faculty, status);
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
