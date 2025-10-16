package eci.edu.dosw.proyecto.mappers;

import eci.edu.dosw.proyecto.dtos.SecretariatDTO;
import eci.edu.dosw.proyecto.models.Secretariat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Interfaz que mapea el DTO y Entity de la secretaria académica.
 */
@Mapper(componentModel = "spring")
public interface SecretariatMapper {

    @Mapping(target = "role", expression = "java(eci.edu.dosw.proyecto.enums.Role.SECRETARIAT)")
    Secretariat toEntity(SecretariatDTO dto);
    SecretariatDTO toDTO(Secretariat sec);
}
