package eci.edu.dosw.proyecto;

import lombok.Data;

/**
 * Clase que es es un DTO, funciona cuando el usuario intenta iniciar sesión.
 */
@Data
public class Login {
    private String username;
    private String password;
}
