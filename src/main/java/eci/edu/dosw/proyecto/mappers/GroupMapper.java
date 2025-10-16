package eci.edu.dosw.proyecto.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import eci.edu.dosw.proyecto.models.Group;
import eci.edu.dosw.proyecto.dtos.GroupDTO;

/**
 * Interfaz que mapea el DTO y la entidad de Grupo.
 */
@Mapper(componentModel = "spring")
public interface GroupMapper {

    GroupDTO toDTO(Group group);

    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "observers", ignore = true)
    @Mapping(target = "subject.createdAt", ignore = true)
    @Mapping(target = "subject.updatedAt", ignore = true)
    Group toModel(GroupDTO dto);

    List<GroupDTO> toDTOList(List<Group> groups);
    List<Group> toModelList(List<GroupDTO> dtos);
}
