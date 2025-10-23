package eci.edu.dosw.proyecto.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO usado para crear una solicitud
 * El estudiante puede solicitar ademas que la solicitud sea excepcional
 */
@Data
public class ChangeRequestCreateDTO {

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
    private Boolean exceptional;
    private String exceptionalReason;
}