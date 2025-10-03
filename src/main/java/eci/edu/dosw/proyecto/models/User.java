package eci.edu.dosw.proyecto.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import eci.edu.dosw.proyecto.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que maneja la información de los usuarios de 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class User {
    @Id
    private int id;
    private String name;
    @Indexed(unique = true)
    private String email;
    private Role role;
}
