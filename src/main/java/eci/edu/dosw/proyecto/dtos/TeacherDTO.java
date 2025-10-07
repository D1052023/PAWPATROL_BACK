package eci.edu.dosw.proyecto.dtos;

import eci.edu.dosw.proyecto.enums.Faculty;
import lombok.Data;

@Data
public class TeacherDTO {
    private int id;
    private String name;
    private String email;
    private Faculty faculty;
}
