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
    @Mapping(target = "schedule", ignore = true)
    @Mapping(target = "requests", ignore = true)
    @Mapping(target = "academicSituation", ignore = true)
    @Mapping(target = "admissionCycle", ignore = true)
    @Mapping(target = "approvedCredits", ignore = true)
    @Mapping(target = "gradeAverage", ignore = true)
    @Mapping(target = "lastSemester", ignore = true)
    @Mapping(target = "semesterToTake", ignore = true)
    @Mapping(target = "situationCycle", ignore = true)
    @Mapping(target = "accumulativeAverage", ignore = true)
    Student toEntity(StudentDTO studentDTO);

    List<StudentDTO> toDTOList(List<Student> students);
    List<Student> toEntityList(List<StudentDTO> studentsDTO);
}