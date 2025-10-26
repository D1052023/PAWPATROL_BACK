package eci.edu.dosw.proyecto.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Clase DTO para actualizar las fechas de creación de solicitudes.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDatesDTO {
    @Schema(type = "string", format = "date-time", example = "2025-10-18T00:00:00")
    private LocalDateTime requestStartDate;
    @Schema(type = "string", format = "date-time", example = "2025-10-25T23:59:59")
    private LocalDateTime requestEndDate;
}