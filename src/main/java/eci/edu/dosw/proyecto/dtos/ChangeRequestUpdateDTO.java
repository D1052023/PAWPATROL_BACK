package eci.edu.dosw.proyecto.dtos;

import lombok.Data;

/**
 * DTO para actualizaciones hechas por el estudiante sobre su solicitud
 */
@Data
public class ChangeRequestUpdateDTO {
    private String targetSubject;
    private String targetGroup;
    private String observations;
}
