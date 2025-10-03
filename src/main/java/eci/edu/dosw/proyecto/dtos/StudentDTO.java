package eci.edu.dosw.proyecto.dtos;

import java.util.List;

import eci.edu.dosw.proyecto.enums.AcademicTrafficLight;
import eci.edu.dosw.proyecto.enums.Curriculum;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

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
    @NotBlank
    private String career;
    private int semester;
    private Curriculum curriculum;
    private AcademicTrafficLight academicTrafficLight;
    private List<ScheduleEntryDTO> schedule;
}
