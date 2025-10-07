package eci.edu.dosw.proyecto.models;

import org.springframework.data.mongodb.core.mapping.Document;

import eci.edu.dosw.proyecto.enums.AcademicTrafficLight;
import eci.edu.dosw.proyecto.enums.Career;
import eci.edu.dosw.proyecto.enums.Curriculum;
import eci.edu.dosw.proyecto.enums.Role;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que extiende de usuario que maneja la información de los estudiantes.
 */
@Document(collection = "students")
@Data
@EqualsAndHashCode(callSuper = true)
public class Student extends User {

    private Career career;
    private int semester;
    private Curriculum curriculum;
    private AcademicTrafficLight academicTrafficLight = AcademicTrafficLight.GREEN;
    private List<ChangeRequest> requests = new ArrayList<>();


    public Student() {
        super();
        setRole(Role.STUDENT);
    }

    public Student(int id, String name, String email) {
        super(id, name, email, Role.STUDENT);
    }
}
