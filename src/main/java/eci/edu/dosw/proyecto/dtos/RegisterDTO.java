package eci.edu.dosw.proyecto.dtos;

import lombok.Data;

/**
 * Clase DTO que representa las credenciales que envia el usuario para registrarseen SIRHA.
 */
@Data
public class RegisterDTO {
    private String email;
    private String password;
}
