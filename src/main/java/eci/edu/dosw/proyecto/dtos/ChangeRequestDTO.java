package eci.edu.dosw.proyecto.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import eci.edu.dosw.proyecto.enums.Faculty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * Clase DTO que maneja la información de una solicitud de cambio de un estudiante.
 */
@Data
public class ChangeRequestDTO {

    @Schema(type = "string", format = "uuid")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Faculty faculty;
    private String currentSubject;
    private String currentGroup;
    private String targetSubject;
    private String targetGroup;
    private String studentName;
    private String observations;
    @Schema(type = "string", format = "date-time")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime resolutionDeadline;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean exceptional;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String exceptionalReason;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String exceptionalRequestedBy;
    @Schema(type = "string", format = "date-time")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime exceptionalRequestedAt;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean exceptionalApproved;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String exceptionalApprovedBy;
    @Schema(type = "string", format = "date-time", example = "2025-10-18T12:00:00")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime exceptionalApprovedAt;
    @Schema(type = "string", format = "date-time")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime exceptionalResolutionDeadline;
}