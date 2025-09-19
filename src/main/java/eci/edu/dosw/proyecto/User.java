package eci.edu.dosw.proyecto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Clase que maneja los usuario en el sistema SIRHA.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users") 
public class User {
    @Id
    private String id;
    private String name;
    private String email;
}
