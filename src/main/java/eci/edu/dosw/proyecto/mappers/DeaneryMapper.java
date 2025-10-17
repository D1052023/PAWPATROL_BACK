package eci.edu.dosw.proyecto.mappers;

import eci.edu.dosw.proyecto.dtos.DeaneryDTO;
import eci.edu.dosw.proyecto.models.Deanery;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Interfaz que mappea los DTO y Entity de las decanaturas.
 */
@Mapper(componentModel = "spring")
public interface DeaneryMapper {

    DeaneryMapper INSTANCE = Mappers.getMapper(DeaneryMapper.class);
    DeaneryDTO toDTO(Deanery deanery);

    @Mapping(target = "role", ignore = true)
    Deanery toEntity(DeaneryDTO dto);

    List<DeaneryDTO> toDTOList(List<Deanery> deaneries);
    List<Deanery> toEntityList(List<DeaneryDTO> dtos);
}
