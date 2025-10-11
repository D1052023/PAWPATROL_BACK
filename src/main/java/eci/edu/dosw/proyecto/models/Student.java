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

    private String name;
    private String email;
    private String documentId;
    private Curriculum curriculum;
    private String planCode;
    private String programName;
    private Career career;
    private int semester;
    private AcademicTrafficLight academicTrafficLight = AcademicTrafficLight.GREEN;
    private List<ChangeRequest> requests = new ArrayList<>();
    private List<ScheduleEntry> schedule = new ArrayList<>();
    private List<String> approvedSubjects;
    private List<String> enrolledSubjects = new ArrayList<>();
    private Double approvedCredits;
    private Double gpa;
    private Double gradeAverage;
    private Integer semesterToTake;
    private Integer lastSemester;
    private String academicSituation;
    private Integer situationCycle;
    private Integer admissionCycle;


    public Student() {
        super();
        setRole(Role.STUDENT);
    }

    public Student(int id, String name, String email) {
        super(id, name, email, Role.STUDENT);
    }
}
