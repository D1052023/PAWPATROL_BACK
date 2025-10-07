package eci.edu.dosw.proyecto.mappers;

import eci.edu.dosw.proyecto.dtos.StudentDTO;
import eci.edu.dosw.proyecto.models.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Interfaz que mappea los DTO y Entity de los estudiantes.
 */
@Mapper(componentModel = "spring")
public interface StudentMapper {

    StudentDTO toDTO(Student student);

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "requests", ignore = true)
    Student toEntity(StudentDTO studentDTO);

    List<StudentDTO> toDTOList(List<Student> students);
    List<Student> toEntityList(List<StudentDTO> studentsDTO);
}
