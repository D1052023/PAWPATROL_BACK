package eci.edu.dosw.proyecto.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para que el estudiante solicite revision excepcional
 */
@Data
public class ExceptionalRequestDTO {
    @NotBlank
    private String reason;
}
