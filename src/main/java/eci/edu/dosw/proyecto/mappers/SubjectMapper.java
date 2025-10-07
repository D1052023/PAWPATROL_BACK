package eci.edu.dosw.proyecto.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import eci.edu.dosw.proyecto.models.Subject;
import eci.edu.dosw.proyecto.dtos.SubjectDTO;

/**
 * Interfaz que mapea el DTO y Entity de materias.
 */
@Mapper(componentModel = "spring")
public interface SubjectMapper {
    
    SubjectDTO toDTO(Subject subject);
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "faculty", ignore = true)
    Subject toModel(SubjectDTO dto);

    List<SubjectDTO> toDTOList(List<Subject> subjects);
    List<Subject> toModelList(List<SubjectDTO> dtos);
}