package eci.edu.dosw.proyecto.mappers;

import eci.edu.dosw.proyecto.dtos.AcademicPlanDTO;
import eci.edu.dosw.proyecto.models.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper del plan de avance de un estudiante
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AcademicPlanMapper {

    @Mapping(source = "id", target = "studentId")
    @Mapping(source = "name", target = "studentName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "curriculum", target = "curriculum")
    AcademicPlanDTO toDto(Student student);
}