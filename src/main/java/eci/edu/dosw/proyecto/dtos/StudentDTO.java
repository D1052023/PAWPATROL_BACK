package eci.edu.dosw.proyecto.dtos;

import eci.edu.dosw.proyecto.enums.AcademicTrafficLight;
import eci.edu.dosw.proyecto.enums.Career;
import eci.edu.dosw.proyecto.enums.Curriculum;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private List<ScheduleEntryDTO> schedule;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private List<ChangeRequestDTO> requests;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private List<String> enrolledSubjects;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private List<String> approvedSubjects;

}