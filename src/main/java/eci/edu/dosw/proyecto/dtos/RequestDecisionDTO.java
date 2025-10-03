package eci.edu.dosw.proyecto.dtos;

import eci.edu.dosw.proyecto.enums.RequestStatus;
import lombok.Data;

/**
 * Clase DTO para la información de estado y observaciones de las solicitudes.
 */
@Data
public class RequestDecisionDTO {
    private RequestStatus status;   
    private String observations;    
}
