package eci.edu.dosw.proyecto.dtos;

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
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
