package eci.edu.dosw.proyecto.dtos;

import eci.edu.dosw.proyecto.enums.Role;
import lombok.Data;

/**
 * Clase DTO que representa las credenciales que envia el usuario para registrarseen SIRHA.
 */
@Data
public class RegisterDTO {
    private String email;
    private String password;
    private Role role;
}
