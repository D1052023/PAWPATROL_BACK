package eci.edu.dosw.proyecto.dtos;

import eci.edu.dosw.proyecto.enums.RequestStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Clase DTO para la información de estado y observaciones de las solicitudes.
 */
@Data
public class RequestDecisionDTO {
    private RequestStatus status;   
    private String observations;
    private Boolean requestAdditionalInfo;
    private String additionalInfoRequestMessage;
    private LocalDateTime infoDueDate;
}
