package eci.edu.dosw.proyecto.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import eci.edu.dosw.proyecto.models.Teacher;
import eci.edu.dosw.proyecto.dtos.TeacherDTO;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

    TeacherMapper INSTANCE = Mappers.getMapper(TeacherMapper.class);

    TeacherDTO toDTO(Teacher teacher);

    @Mapping(target = "schedule", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "assignedGroups", ignore = true)
    @Mapping(target = "assignedSubjects", ignore = true)
    Teacher toEntity(TeacherDTO teacherDTO);
}
