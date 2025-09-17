package eci.edu.dosw.proyecto;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * Clase que es un DTO, la cual envia información de manera estructurada desde el backend hacia el frontend.
 * 
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
}
