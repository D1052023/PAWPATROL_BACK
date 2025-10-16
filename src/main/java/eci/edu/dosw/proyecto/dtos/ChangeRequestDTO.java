package eci.edu.dosw.proyecto.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import eci.edu.dosw.proyecto.enums.Faculty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private Faculty faculty;
    @NotNull
    private LocalDateTime resolutionDeadline;
    private boolean exceptional;
    private String exceptionalReason;
    private String exceptionalRequestedBy;
    private LocalDateTime exceptionalRequestedAt;
    private Boolean exceptionalApproved;
    private String exceptionalApprovedBy;
    private LocalDateTime exceptionalApprovedAt;
    private LocalDateTime exceptionalResolutionDeadline;

}
