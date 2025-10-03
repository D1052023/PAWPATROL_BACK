package eci.edu.dosw.proyecto.models;

import eci.edu.dosw.proyecto.enums.AcademicTrafficLight;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa una entrada de horario.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleEntry {
    private String subject;
    private String group;
    private String day;
    private String from;
    private int semester;
    private String classroom;
    private String to;
    private AcademicTrafficLight status;
}
