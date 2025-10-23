package eci.edu.dosw.proyecto.mappers;

import eci.edu.dosw.proyecto.dtos.DeaneryDTO;
import eci.edu.dosw.proyecto.models.Deanery;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Interfaz que mappea los DTO y Entity de las decanaturas.
 */
@Mapper(componentModel = "spring")
public interface DeaneryMapper {

    DeaneryDTO toDTO(Deanery deanery);

    @Mapping(target = "role", ignore = true)
    Deanery toEntity(DeaneryDTO dto);

    List<DeaneryDTO> toDTOList(List<Deanery> deaneries);
    List<Deanery> toEntityList(List<DeaneryDTO> dtos);
}