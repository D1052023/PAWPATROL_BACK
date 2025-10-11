package eci.edu.dosw.proyecto.mappers;

import eci.edu.dosw.proyecto.dtos.AcademicPlanDTO;
import eci.edu.dosw.proyecto.models.Student;
import org.mapstruct.Mapper;

/**
 * Mapper del plan de avance de un estudiante
 */
@Mapper(componentModel = "spring")
public interface AcademicPlanMapper {
    AcademicPlanDTO toDto(Student student);
}
