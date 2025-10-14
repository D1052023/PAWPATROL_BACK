package eci.edu.dosw.proyecto.mappers;

import eci.edu.dosw.proyecto.dtos.AcademicPlanDTO;
import eci.edu.dosw.proyecto.models.Student;
import org.mapstruct.Mapper;

/**
 * Interfaz que mapea el DTO del plan académico.
 */
@Mapper(componentModel = "spring")
public interface AcademicPlanMapper {
    AcademicPlanDTO toDto(Student student);
}
