package eci.edu.dosw.proyecto.dtos;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Clase DTO para la información de la secretaria académica.
 */
@Data
public class SecretariatDTO {
    private int id;
    private String name;
    private String email;
    private LocalDateTime requestStartDate;
    private LocalDateTime requestEndDate;
}
