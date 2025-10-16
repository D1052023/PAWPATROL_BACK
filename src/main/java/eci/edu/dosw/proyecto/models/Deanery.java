package eci.edu.dosw.proyecto.models;

import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.enums.Role;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * Clase que representa a un usuario tipo Decanato (Deanery) en MongoDB.
 */
@Document(collection = "deanerys")
@Data
@EqualsAndHashCode(callSuper = true)
public class Deanery extends User {
    private Faculty faculty;

    public Deanery() {
        super();
        setRole(Role.DEANERY);
    }

    public Deanery(int id, String name, String email) {
        super(id, name, email, Role.DEANERY); 
    }
}
