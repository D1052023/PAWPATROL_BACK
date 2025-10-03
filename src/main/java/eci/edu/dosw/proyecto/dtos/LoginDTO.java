package eci.edu.dosw.proyecto.dtos;

import lombok.Data;

/**
 * Clase DTO que representa las credenciales que envia el usuario para iniciar
 * sesión en SIRHA
 */
@Data
public class LoginDTO {
    private String email;
    private String password;
}
