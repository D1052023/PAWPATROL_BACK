package eci.edu.dosw.proyecto.services;

import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;

import java.util.List;
import java.util.UUID;

/**
 * Interfaz que maneja los metodos que va a imlementar solicitudes de cambio.
 */
public interface ChangeRequestService {

    ChangeRequestDTO createChangeRequest(Integer studentId, ChangeRequestDTO requestDTO);
    List<ChangeRequestDTO> getAllRequestsByStudent(Integer studentId);
    ChangeRequestDTO getRequestById(Integer studentId, UUID requestId);
    ChangeRequestDTO updateChangeRequest(Integer studentId, UUID requestId, ChangeRequestDTO dto);
    void deleteChangeRequest(Integer studentId, UUID requestId);
    List<ChangeRequestDTO> getExceptionalRequestsByStudent(Integer studentId);
    List<ChangeRequestDTO> getAllExceptionalRequests();
    ChangeRequestDTO requestExceptionalReview(Integer studentId, UUID requestId, String reason);
    List<ChangeRequestDTO> getExceptionalRequestsByDeanery(int deaneryId);
    List<ChangeRequestDTO> getExceptionalRequestsByStudentForDeanery(int deaneryId, Integer studentId);
    ChangeRequestDTO approveExceptionalRequest(int approverId, UUID requestId, boolean approve, String observations);

}
