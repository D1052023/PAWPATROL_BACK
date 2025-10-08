package eci.edu.dosw.proyecto.dtos;

import eci.edu.dosw.proyecto.enums.AcademicTrafficLight;
import eci.edu.dosw.proyecto.enums.Career;
import eci.edu.dosw.proyecto.enums.Curriculum;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * Clase DTO que maneja la informacón que va a manejar el estudiante.
 */
@Data
public class StudentDTO {
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    private String email;
    private Career career;
    private int semester;
    private Curriculum curriculum;
    private AcademicTrafficLight academicTrafficLight;
    private List<ScheduleEntryDTO> schedule;
    private List<ChangeRequestDTO> requests;
    private List<String> enrolledSubjects;
}
