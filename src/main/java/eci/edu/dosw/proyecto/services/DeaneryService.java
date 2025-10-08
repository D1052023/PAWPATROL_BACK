package eci.edu.dosw.proyecto.services;

import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.DeaneryDTO;
import eci.edu.dosw.proyecto.dtos.RequestDatesDTO;
import eci.edu.dosw.proyecto.dtos.RequestDecisionDTO;
import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.enums.RequestStatus;

import java.util.List;
import java.util.UUID;

/**
 * Interfaz que maneja los métodos que se van a implementar en decanatura.
 */
public interface DeaneryService {
    DeaneryDTO createDeanery(DeaneryDTO deaneryDTO);
    DeaneryDTO getDeaneryById(int id);
    DeaneryDTO getDeaneryByFaculty(Faculty faculty);
    List<DeaneryDTO> getAllDeaneries();
    DeaneryDTO updateDeanery(int id, DeaneryDTO deaneryDTO);
    void deleteDeanery(int id);
    ChangeRequestDTO respondRequestByDeanery(int deaneryId, UUID requestId, RequestDecisionDTO decision, RequestDatesDTO dates);
    List<ChangeRequestDTO> getRequestsByFacultyAndStatus(Faculty faculty, RequestStatus status);
    ChangeRequestDTO updateRequestAsDeanery(int deaneryId, UUID requestId, RequestDecisionDTO decision, RequestDatesDTO dates);
    void deleteRequestAsDeanery(int deaneryId, UUID requestId);
}
