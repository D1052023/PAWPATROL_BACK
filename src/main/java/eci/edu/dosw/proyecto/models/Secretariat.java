package eci.edu.dosw.proyecto.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.mongodb.core.mapping.Document;

import eci.edu.dosw.proyecto.enums.Role;

import java.time.LocalDateTime;

/**
 * Clase que representa la información de secretaria académica.
 */
@Document(collection = "secretariat")
@Data
@EqualsAndHashCode(callSuper = true)
public class Secretariat extends User {

    private LocalDateTime requestStartDate;
    private LocalDateTime requestEndDate;

    public Secretariat() {
        super();
        setRole(Role.SECRETARIAT);
    }

    public Secretariat(int id, String name, String email) {
        super(id, name, email, Role.SECRETARIAT);
    }

}
