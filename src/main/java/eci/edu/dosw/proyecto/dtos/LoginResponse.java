package eci.edu.dosw.proyecto.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * Clase que maneja la respuesta del login con el token JWT.
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
}
