package eci.edu.dosw.proyecto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * Clase que maneja los usuario en el sistema SIRHA.
 */
@Entity
@Data
@AllArgsConstructor
public abstract class Usuario {
    @Id
    private String id;
    private String name;
    private String email;
}
