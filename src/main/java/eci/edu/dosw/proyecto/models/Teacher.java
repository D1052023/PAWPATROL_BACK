package eci.edu.dosw.proyecto.models;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.enums.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection = "professors")
@Data
@EqualsAndHashCode(callSuper = true)
public class Teacher extends User{
    private Faculty faculty;
    private List<String> assignedSubjects;
    private List<String> assignedGroups;
    private List<String> schedule;

    public Teacher() {
        super();
        setRole(Role.TEACHER);
    }

    public Teacher(int id, String name, String email){
        super(id, name, email, Role.TEACHER);
    }

}
