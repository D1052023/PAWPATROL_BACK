package eci.edu.dosw.proyecto.dtos;

import eci.edu.dosw.proyecto.enums.AcademicTrafficLight;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleEntryDTO {
    @NotBlank
    private String subject;
    @NotBlank
    private String group;
    private int semester;
    @NotBlank
    private String day;
    @NotBlank
    private String from;
    @NotBlank
    private String to;
    private String classroom;
    private AcademicTrafficLight status;
}
