package eci.edu.dosw.proyecto.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import eci.edu.dosw.proyecto.models.Teacher;
import eci.edu.dosw.proyecto.dtos.TeacherDTO;

/**
 * Interfaz que mapea el DTO y Entity de profesores.
 */
@Mapper(componentModel = "spring")
public interface TeacherMapper { 

    TeacherDTO toDTO(Teacher teacher);

    @Mapping(target = "schedule", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "assignedGroups", ignore = true)
    @Mapping(target = "assignedSubjects", ignore = true)
    Teacher toEntity(TeacherDTO teacherDTO);
}
