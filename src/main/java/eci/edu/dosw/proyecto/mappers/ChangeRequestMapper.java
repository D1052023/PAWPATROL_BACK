package eci.edu.dosw.proyecto.mappers;

import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.models.ChangeRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Interfaz que mapea el DTO y Entity de la solicitud de cambio.
 */
@Mapper(componentModel = "spring")
public interface ChangeRequestMapper {

    ChangeRequestDTO toDTO(ChangeRequest changeRequest);

    @Mapping(target = "processedBy", ignore = true)
    @Mapping(target = "processedById", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "faculty", ignore = true)
    @Mapping(target = "priority", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    ChangeRequest toEntity(ChangeRequestDTO changeRequestDTO);

    List<ChangeRequestDTO> toDTOList(List<ChangeRequest> changeRequests);
    List<ChangeRequest> toEntityList(List<ChangeRequestDTO> changeRequestDTOs);
}
