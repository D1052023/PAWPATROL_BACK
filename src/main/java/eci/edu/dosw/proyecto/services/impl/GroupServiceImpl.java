package eci.edu.dosw.proyecto.services.impl;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

import eci.edu.dosw.proyecto.services.GroupService;
import eci.edu.dosw.proyecto.repositories.GroupRepository;
import eci.edu.dosw.proyecto.repositories.SubjectRepository;
import eci.edu.dosw.proyecto.dtos.GroupDTO;
import eci.edu.dosw.proyecto.models.Group;
import eci.edu.dosw.proyecto.models.Subject;
import eci.edu.dosw.proyecto.mappers.GroupMapper;
import eci.edu.dosw.proyecto.mappers.ScheduleEntryMapper;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final SubjectRepository subjectRepository;
    private final GroupMapper groupMapper;
    private final ScheduleEntryMapper scheduleEntryMapper;

    @Override
    public GroupDTO createGroup(GroupDTO dto) {
        Group group = groupMapper.toModel(dto);

        if (group.getSubjectId() != null) {
            Subject subject = subjectRepository.findBySubjectId(group.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Materia no encontrada"));
            if (subject.getCurriculum() != group.getCurriculum()) {
                throw new RuntimeException("La materia de ese pensum no corresponde a la del pensum del grupo");
            }
        }

        group = groupRepository.save(group);
        return groupMapper.toDTO(group);
    }

    @Override
    public List<GroupDTO> getAllGroups() {
        return groupRepository.findAll()
                .stream()
                .map(groupMapper::toDTO)
                .toList();
    }

    @Override
    public GroupDTO getGroupById(String groupId) {
        Group group = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        return groupMapper.toDTO(group);
    }

    @Override
    public GroupDTO updateGroup(String groupId, GroupDTO dto) {
        Group existing = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getTeacher() != 0) existing.setTeacher(dto.getTeacher());
        if (dto.getMaximumCapacity() != 0) existing.setMaximumCapacity(dto.getMaximumCapacity());
        if (dto.getCurrentCapacity() != 0) existing.setCurrentCapacity(dto.getCurrentCapacity());
        if (dto.getSchedule() != null) existing.setSchedule(scheduleEntryMapper.toModelList(dto.getSchedule()));
        if (dto.getSubjectId() != null) existing.setSubjectId(dto.getSubjectId());
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
    public GroupDTO updateCapacity(String groupId, int newMaximumCapacity) {
        Group existing = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        existing.setMaximumCapacity(newMaximumCapacity);
        existing = groupRepository.save(existing);
        return groupMapper.toDTO(existing);
    }

    @Override
    public List<Integer> getWaitlist(String groupId) {
        Group group = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        return group.getWaitlist();
    }

    @Override
    public GroupDTO partialUpdateGroup(String groupId, GroupDTO dto) {
        Group existing = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getTeacher() != 0) existing.setTeacher(dto.getTeacher());
        if (dto.getMaximumCapacity() != 0) existing.setMaximumCapacity(dto.getMaximumCapacity());
        if (dto.getCurrentCapacity() != 0) existing.setCurrentCapacity(dto.getCurrentCapacity());
        if (dto.getSchedule() != null) existing.setSchedule(scheduleEntryMapper.toModelList(dto.getSchedule()));
        if (dto.getSubjectId() != null) existing.setSubjectId(dto.getSubjectId());
        if (dto.getCurriculum() != null) existing.setCurriculum(dto.getCurriculum());
        if (dto.getGroupStatus() != null) existing.setGroupStatus(dto.getGroupStatus());

        existing = groupRepository.save(existing);
        return groupMapper.toDTO(existing);
    }

    @Override
    public List<GroupDTO> getGroupsByTeacher(int teacherId) {
        return groupRepository.findByTeacher(teacherId)
                .stream()
                .map(groupMapper::toDTO)
                .toList();
    }

    @Override
    public List<GroupDTO> getGroupsBySubject(String subjectId) {
        return groupRepository.findBySubjectId(subjectId)
                .stream()
                .map(groupMapper::toDTO)
                .toList();
    }
}
