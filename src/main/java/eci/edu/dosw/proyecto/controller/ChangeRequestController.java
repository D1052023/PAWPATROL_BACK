package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.StudentDTO;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.models.ChangeRequestHistory;
import eci.edu.dosw.proyecto.services.ChangeRequestService;
import eci.edu.dosw.proyecto.services.HistoryService;
import eci.edu.dosw.proyecto.services.StudentService;
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
    private final StudentService studentService;
    private final HistoryService historyService;

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

    @GetMapping("/{id}/schedule/current")
    public StudentDTO getCurrentSchedule(@PathVariable Integer id, @RequestParam(required = false) Integer semester) {
        if (semester == null) {
            StudentDTO student = studentService.getStudentById(id);
            semester = student.getSemester();
        }
        return studentService.getStudentSchedule(id, semester);
    }

    @GetMapping("/{id}/schedule/previous")
    public StudentDTO getPreviousSchedule(@PathVariable Integer id, @RequestParam int semester) {
        return studentService.getStudentSchedule(id, semester);
    }


    @GetMapping("/status")
    public List<ChangeRequestDTO> getRequestsByStudentAndStatus(@PathVariable Integer studentId, @RequestParam(required = false) RequestStatus status) {
        return changeRequestService.getAllRequestsByStudent(studentId);
    }

    @GetMapping("/{requestId}/history")
    public List<ChangeRequestHistory> getRequestHistory(@PathVariable Integer studentId, @PathVariable UUID requestId) {
        return historyService.getHistory(requestId);
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
