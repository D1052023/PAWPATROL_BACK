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
}
