package eci.edu.dosw.proyecto.dtos;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * Clase DTO que maneja la información de una solicitud de cambio de un estudiante.
 */
@Data
public class ChangeRequestDTO {
    private UUID id;
    @NotBlank
    private String currentSubject;
    @NotBlank
    private String currentGroup;
    @NotBlank
    private String targetSubject;
    @NotBlank
    private String targetGroup;
    @NotBlank
    private String studentName;
    private String observations;

}
