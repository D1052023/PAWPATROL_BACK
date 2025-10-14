package eci.edu.dosw.proyecto.models;

import org.springframework.data.mongodb.core.mapping.Document;

import eci.edu.dosw.proyecto.enums.Role;
import lombok.Data;

/**
 * Clase que maneja los usuarios autenticados.
 */
@Document(collection = "users")
@Data
public class AuthUser {
    private String id;
    private String email;
    private String passwordHash;
    private Role role;
}