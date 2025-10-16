package eci.edu.dosw.proyecto.dtos;

import eci.edu.dosw.proyecto.enums.Faculty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase DTO para la información de un Decan@.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeaneryDTO {
    private int id;
    private String name;
    private String email;
    private Faculty faculty;
}
