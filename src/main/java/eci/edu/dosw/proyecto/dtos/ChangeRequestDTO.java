package eci.edu.dosw.proyecto.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import eci.edu.dosw.proyecto.enums.Faculty;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;


/**
 * Clase DTO que maneja la información de una solicitud de cambio de un estudiante.
 */
@Data
public class ChangeRequestDTO {
    @Schema(type = "string", format = "uuid", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
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
    @Schema(type = "string", format = "date-time", example = "2025-10-18T12:00:00")
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
