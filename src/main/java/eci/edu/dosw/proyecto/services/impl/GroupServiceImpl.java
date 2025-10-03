package eci.edu.dosw.proyecto.services.impl;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;

import eci.edu.dosw.proyecto.services.GroupService;
import eci.edu.dosw.proyecto.repositories.GroupRepository;
import eci.edu.dosw.proyecto.dtos.GroupDTO;
import eci.edu.dosw.proyecto.models.Group;
import eci.edu.dosw.proyecto.mappers.GroupMapper;
import eci.edu.dosw.proyecto.mappers.ScheduleEntryMapper;
import eci.edu.dosw.proyecto.mappers.SubjectMapper;

/**
 * Clase servicio que implementa la interfaz y maneja la lógica del grupo.
 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;
    private final ScheduleEntryMapper scheduleEntryMapper;
    private final SubjectMapper subjectMapper;

    @Override
    public GroupDTO createGroup(GroupDTO dto) {
        Group group = groupMapper.toModel(dto);

        if (group.getSubject() != null) {
            if (group.getSubject().getCurriculum() != group.getCurriculum()) {
                throw new RuntimeException("La materia de ese pensum no corresponde a la del pensum del grupo");
            }
        }

        group = groupRepository.save(group);
        return groupMapper.toDTO(group);
    }

    @Override
    public List<GroupDTO> getAllGroups() {
        return groupMapper.toDTOList(groupRepository.findAll());
    }

    @Override
    public GroupDTO getGroupById(String groupId) {
        return groupRepository.findByGroupId(groupId)
                .map(groupMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
    }

    @Override
    public GroupDTO updateGroup(String groupId, GroupDTO dto) {
        Group existing = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getTeacher() != null) existing.setTeacher(dto.getTeacher());
        if (dto.getMaximumCapacity() != 0) existing.setMaximumCapacity(dto.getMaximumCapacity());
        if (dto.getCurrentCapacity() != 0) existing.setCurrentCapacity(dto.getCurrentCapacity());
        if (dto.getSchedule() != null) existing.setSchedule(scheduleEntryMapper.toModelList(dto.getSchedule()));

        if (dto.getSubject() != null) {
            if (dto.getSubject().getCurriculum() != existing.getCurriculum()) {
                throw new RuntimeException("La materia de ese pensum no corresponde a la del pensum del grupo");
            }
            existing.setSubject(subjectMapper.toModel(dto.getSubject()));
        }

        if (dto.getCurriculum() != null) existing.setCurriculum(dto.getCurriculum());

        existing = groupRepository.save(existing);
        return groupMapper.toDTO(existing);
    }

    @Override
    public void deleteGroup(String groupId) {
        Group group = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        groupRepository.delete(group);
    }


    @Override
    public GroupDTO updateCapacity(String groupId, int newCurrentCapacity) {
        Group existing = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        existing.setCurrentCapacity(newCurrentCapacity);
        existing = groupRepository.save(existing);
        return groupMapper.toDTO(existing);
    }

    @Override
    public List<Integer> getWaitlist(String groupId) {
        Group group = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        return group.getWaitlist();
    }
}
