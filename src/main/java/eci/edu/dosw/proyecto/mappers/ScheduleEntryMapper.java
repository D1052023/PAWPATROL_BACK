package eci.edu.dosw.proyecto.mappers;

import org.mapstruct.Mapper;

import eci.edu.dosw.proyecto.models.ScheduleEntry;
import eci.edu.dosw.proyecto.dtos.ScheduleEntryDTO;

import java.util.List;

/**
 * Interfaz que mapea el DTO y Entity de horario de una materia.
 */
@Mapper(componentModel = "spring")
public interface ScheduleEntryMapper {

    ScheduleEntryDTO toDTO(ScheduleEntry entry);
    ScheduleEntry toModel(ScheduleEntryDTO dto);

    List<ScheduleEntryDTO> toDTOList(List<ScheduleEntry> entries);
    List<ScheduleEntry> toModelList(List<ScheduleEntryDTO> dtos);
}
