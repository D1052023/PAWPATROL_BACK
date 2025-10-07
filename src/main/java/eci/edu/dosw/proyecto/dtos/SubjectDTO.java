package eci.edu.dosw.proyecto.dtos;

import java.util.List;

import eci.edu.dosw.proyecto.enums.Curriculum;
import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.enums.SubjectStatus;
import eci.edu.dosw.proyecto.enums.SubjectType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase DTO que maneja la información de las materias.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectDTO {
    private String subjectId;
    private String name;
    private int credits;
    private Curriculum curriculum;
    private SubjectType type;
    private SubjectStatus subjectStatus;
    private List<String> prerequisites;

    private int teacherId;
    private Faculty faculty;

    private String description;

}
