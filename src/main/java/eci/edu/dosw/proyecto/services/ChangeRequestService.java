package eci.edu.dosw.proyecto.services;

import eci.edu.dosw.proyecto.dtos.ChangeRequestCreateDTO;
import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.ChangeRequestUpdateDTO;
import eci.edu.dosw.proyecto.dtos.ExceptionalRequestDTO;

import java.util.List;
import java.util.UUID;

/**
 * Interfaz que maneja los métodos que va a imlementar solicitudes de cambio.
 */
public interface ChangeRequestService {

    ChangeRequestDTO createChangeRequest(Integer studentId, ChangeRequestCreateDTO createDto);
    List<ChangeRequestDTO> getAllRequestsByStudent(Integer studentId);
    ChangeRequestDTO getRequestById(Integer studentId, UUID requestId);
    ChangeRequestDTO updateChangeRequest(Integer studentId, UUID requestId, ChangeRequestUpdateDTO updateDto);
    void deleteChangeRequest(Integer studentId, UUID requestId);
    List<ChangeRequestDTO> getExceptionalRequestsByStudent(Integer studentId);
    List<ChangeRequestDTO> getAllExceptionalRequests();
    ChangeRequestDTO requestExceptionalReview(Integer studentId, UUID requestId, String reason);
    List<ChangeRequestDTO> getExceptionalRequestsByDeanery(int deaneryId);
    List<ChangeRequestDTO> getExceptionalRequestsByStudentForDeanery(int deaneryId, Integer studentId);
    ChangeRequestDTO approveExceptionalRequest(int approverId, UUID requestId, boolean approve, String observations);
    ChangeRequestDTO requestExceptional(Integer studentId, UUID requestId, ExceptionalRequestDTO dto);


}

