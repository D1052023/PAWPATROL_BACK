package eci.edu.dosw.proyecto.dtos;

import eci.edu.dosw.proyecto.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Clase DTO para la información básica de un usuario.
 */
@Data
@AllArgsConstructor
public class UserResponseDTO {
    private String id;
    private String email;
    private Role role;
}
