package eci.edu.dosw.proyecto.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.RequestDatesDTO;
import eci.edu.dosw.proyecto.dtos.RequestDecisionDTO;
import eci.edu.dosw.proyecto.dtos.SecretariatDTO;
import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.enums.RequestStatus;

/**
 * Interfaz que maneja los métodos que se van a implementar en secretaria académica.
 */
public interface SecretariatService {

    SecretariatDTO createSecretariat(SecretariatDTO dto);
    SecretariatDTO getSecretariatById(int id);
    List<SecretariatDTO> getAllSecretariats(); 
    SecretariatDTO updateSecretariat(int id, SecretariatDTO dto);
    void deleteSecretariat(int id); 
    void updateRequestDates(int id, LocalDateTime startDate, LocalDateTime endDate);
    ChangeRequestDTO respondRequestBySecretariat(UUID requestId, RequestDecisionDTO decision, RequestDatesDTO requestDates);
    List<ChangeRequestDTO> getRequestsByFacultyAndStatus(Faculty faculty, RequestStatus status);
    ChangeRequestDTO updateRequestAsSecretariat(UUID requestId, RequestDecisionDTO decision, RequestDatesDTO requestDates);
    void deleteRequestAsSecretariat(UUID requestId);
}