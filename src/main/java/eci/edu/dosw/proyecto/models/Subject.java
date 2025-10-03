package eci.edu.dosw.proyecto.models;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import eci.edu.dosw.proyecto.enums.Curriculum;
import eci.edu.dosw.proyecto.enums.SubjectStatus;
import eci.edu.dosw.proyecto.enums.SubjectType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que maneja la información de una materia.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "subjects")
public class Subject {
    private String subjectId;
    private String name;
    private int credits;
    private Curriculum curriculum;
    private SubjectType type;
    private SubjectStatus subjectStatus;
    private List<String> prerequisites;
    private String description;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
