package eci.edu.dosw.proyecto.services.impl;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Query;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import eci.edu.dosw.proyecto.services.AlertService;
import eci.edu.dosw.proyecto.services.GroupService;
import eci.edu.dosw.proyecto.repositories.GroupRepository;
import eci.edu.dosw.proyecto.dtos.GroupDTO;
import eci.edu.dosw.proyecto.mappers.GroupMapper;
import eci.edu.dosw.proyecto.mappers.ScheduleEntryMapper;
import eci.edu.dosw.proyecto.dtos.ScheduleEntryDTO;
import eci.edu.dosw.proyecto.dtos.StudentDTO;
import eci.edu.dosw.proyecto.models.*;
import eci.edu.dosw.proyecto.repositories.StudentRepository;
import eci.edu.dosw.proyecto.services.HistoryService;
import eci.edu.dosw.proyecto.services.StudentService;
import eci.edu.dosw.proyecto.util.MessageExceptions;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    private final MongoTemplate mongoTemplate;
    private final AlertService alertService;
    private final GroupMapper groupMapper;
    private final ScheduleEntryMapper scheduleEntryMapper;
    private final MessageExceptions message;
    private final StudentRepository studentRepository;
    private final StudentService studentService;
    private final HistoryService historyService;

    @Override
    public GroupDTO createGroup(GroupDTO dto) {
        Group group = groupMapper.toModel(dto);

        message.ensureSubjectIdProvided(group.getSubjectId());
        Subject subject = message.findSubjectOrThrow(group.getSubjectId());
        message.ensureSubjectCurriculumMatchesGroup(subject, group);
        message.ensureSubjectHasTotalCapacity(subject);

        int totalCuposGrupos = groupRepository.findBySubjectId(subject.getSubjectId())
                .stream()
                .mapToInt(Group::getMaximumCapacity)
                .sum();

        int newGroupCapacity = group.getMaximumCapacity() == null ? 0 : group.getMaximumCapacity();
        message.ensureTotalGroupCapacityNotExceeded(totalCuposGrupos, newGroupCapacity, subject.getMaximumCapacity());

        group.attach(alertService);

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
        Group group = message.findGroupOrThrow(groupId);
        return groupMapper.toDTO(group);
    }

    @Override
    public GroupDTO updateGroup(String groupId, GroupDTO dto) {
        Group existing = message.findGroupOrThrow(groupId);

        if (dto.getMaximumCapacity() != 0) {
            Subject subject = message.findSubjectOrThrow(existing.getSubjectId());
            final String currentGroupId = existing.getGroupId();

            int totalCuposGrupos = groupRepository.findBySubjectId(subject.getSubjectId())
                    .stream()
                    .filter(g -> !g.getGroupId().equals(currentGroupId))
                    .mapToInt(Group::getMaximumCapacity)
                    .sum();

            int newCapacity = dto.getMaximumCapacity();
            message.ensureTotalGroupCapacityNotExceeded(totalCuposGrupos, newCapacity, subject.getMaximumCapacity());
            existing.setMaximumCapacity(dto.getMaximumCapacity());
        }

        if (dto.getCurrentCapacity() != 0) existing.setCurrentCapacity(dto.getCurrentCapacity());
        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getSchedule() != null) existing.setSchedule(scheduleEntryMapper.toModelList(dto.getSchedule()));
        if (dto.getCurriculum() != null) existing.setCurriculum(dto.getCurriculum());

        existing.attach(alertService);

        existing = groupRepository.save(existing);
        return groupMapper.toDTO(existing);
    }

    @Override
    public void deleteGroup(String groupId) {
        Group group = message.findGroupOrThrow(groupId);
        groupRepository.delete(group);
    }

    @Override
    public GroupDTO updateCapacity(String groupId, int newMaximumCapacity) {
        Group existing = message.findGroupOrThrow(groupId);
        existing.setMaximumCapacity(newMaximumCapacity);
        existing = groupRepository.save(existing);
        return groupMapper.toDTO(existing);
    }

    @Override
    public List<Integer> getWaitlist(String groupId) {
        Group group = message.findGroupOrThrow(groupId);
        return group.getWaitlist();
    }

    @Override
    public List<StudentDTO> getWaitlistDetails(String groupId) {
        Group group = message.findGroupOrThrow(groupId);
        List<Integer> waitlist = group.getWaitlist();
        if (waitlist == null || waitlist.isEmpty()) return List.of();

        List<StudentDTO> details = new ArrayList<>(waitlist.size());
        for (Integer studentId : waitlist) {
            StudentDTO sdto = studentService.getStudentById(studentId);
            details.add(sdto);
        }
        return details;
    }

    @Override
    public GroupDTO partialUpdateGroup(String groupId, GroupDTO dto) {
        Group existing = message.findGroupOrThrow(groupId);

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getMaximumCapacity() != 0) existing.setMaximumCapacity(dto.getMaximumCapacity());
        if (dto.getCurrentCapacity() != 0) existing.setCurrentCapacity(dto.getCurrentCapacity());
        if (dto.getSchedule() != null) existing.setSchedule(scheduleEntryMapper.toModelList(dto.getSchedule()));
        if (dto.getSubjectId() != null) existing.setSubjectId(dto.getSubjectId());
        if (dto.getCurriculum() != null) existing.setCurriculum(dto.getCurriculum());
        if (dto.getGroupStatus() != null) existing.setGroupStatus(dto.getGroupStatus());

        existing.attach(alertService);

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

    @Override
    public int getMaxCapacity(String groupId) {
        Group group = message.findGroupOrThrow(groupId);
        return group.getMaximumCapacity();
    }

    @Override
    public int getCurrentCapacity(String groupId) {
        Group group = message.findGroupOrThrow(groupId);
        return group.getCurrentCapacity();
    }

    @Override
    public GroupDTO assignTeacherToGroup(String groupId, int teacherId) {
        Group group = message.findGroupOrThrow(groupId);

        message.ensureTeacherExistsOrThrow(teacherId);
        message.ensureGroupHasNoTeacherAssigned(group);
        group.setTeacher(teacherId);
        groupRepository.save(group);
        return groupMapper.toDTO(group);
    }

    @Override
    public GroupDTO removeTeacherFromGroup(String groupId) {
        Group group = message.findGroupOrThrow(groupId);

        message.ensureGroupHasTeacherAssigned(group);
        group.setTeacher(0);
        groupRepository.save(group);
        return groupMapper.toDTO(group);
    }

    @Override
    public int getEnrolledCount(String groupId) {
        Group g = message.findGroupOrThrow(groupId);
        return Math.max(0, g.getCurrentCapacity());
    }

    @Override
    public ScheduleEntryDTO addScheduleEntry(String groupId, ScheduleEntryDTO entryDto) {
        Group g = message.findGroupOrThrow(groupId);
        if (g.getSchedule() == null) g.setSchedule(new ArrayList<>());
        ScheduleEntry e = scheduleEntryMapper.toModel(entryDto);
        e.setGroup(groupId);
        g.getSchedule().add(e);

        groupRepository.save(g);

        return scheduleEntryMapper.toDTO(e);
    }

    @Override
    public List<ScheduleEntryDTO> getSchedule(String groupId) {
        Group g = message.findGroupOrThrow(groupId);
        if (g.getSchedule() == null) return List.of();
        return scheduleEntryMapper.toDTOList(g.getSchedule());
    }

    @Override
    public List<ScheduleEntryDTO> updateScheduleGlobal(String groupId, List<ScheduleEntryDTO> entries) {
        Group g = message.findGroupOrThrow(groupId);

        List<ScheduleEntry> newSchedule = (entries == null) ? new ArrayList<>() : scheduleEntryMapper.toModelList(entries);
        newSchedule.forEach(e -> e.setGroup(groupId));
        g.setSchedule(newSchedule);
        groupRepository.save(g);
        return scheduleEntryMapper.toDTOList(g.getSchedule());
    }

    @Override
    public List<ScheduleEntryDTO> updateScheduleForDay(String groupId, String day, List<ScheduleEntryDTO> entries) {
        Group g = message.findGroupOrThrow(groupId);
        if (g.getSchedule() == null) g.setSchedule(new ArrayList<>());
        g.getSchedule().removeIf(se -> se.getDay() != null && se.getDay().equalsIgnoreCase(day));

        if (entries != null && !entries.isEmpty()) {
            List<ScheduleEntry> mapped = scheduleEntryMapper.toModelList(entries);
            mapped.forEach(e -> e.setGroup(groupId));
            g.getSchedule().addAll(mapped);
        }

        groupRepository.save(g);
        return scheduleEntryMapper.toDTOList(g.getSchedule());
    }

    @Override
    public void deleteScheduleGlobal(String groupId) {
        Group g = message.findGroupOrThrow(groupId);
        g.setSchedule(new ArrayList<>());
        groupRepository.save(g);
    }

    @Override
    public void deleteScheduleForDay(String groupId, String day) {
        Group g = message.findGroupOrThrow(groupId);
        if (g.getSchedule() != null) {
            g.getSchedule().removeIf(se -> se.getDay() != null && se.getDay().equalsIgnoreCase(day));
            groupRepository.save(g);
        }
    }


    @Override
    public GroupDTO assignStudentToGroup(String groupId, int studentId) {

        Student student = message.findStudentOrThrow(studentId);
        Group group = message.findGroupOrThrow(groupId);
        message.ensureCurriculumMatchesStudentGroup(student, group);
        message.ensureStudentNotInGroup(student, groupId);
        message.ensureNoScheduleConflict(student, group);
        message.ensureGroupHasAvailableCapacity(group);

        Query q = Query.query(Criteria.where("groupId").is(groupId)
                .and("currentCapacity").lt(group.getMaximumCapacity()));
        Update u = new Update()
                .inc("currentCapacity", 1)
                .pull("waitlist", studentId);

        Group updated = mongoTemplate.findAndModify(
                q, u, FindAndModifyOptions.options().returnNew(true), Group.class
        );

        Objects.requireNonNull(updated, "No se puede inscribir: el grupo está lleno o se actualizó simultáneamente");

        if (student.getSchedule() == null) student.setSchedule(new ArrayList<>());
        String subjectId = updated.getSubjectId();
        if (updated.getSchedule() != null && !updated.getSchedule().isEmpty()) {
            for (ScheduleEntry se : updated.getSchedule()) {
                ScheduleEntry newEntry = new ScheduleEntry(
                        subjectId,
                        updated.getGroupId(),
                        se.getDay(),
                        se.getFrom(),
                        se.getSemester(),
                        se.getClassroom(),
                        se.getTo(),
                        se.getStatus()
                );

                boolean exists = student.getSchedule().stream().anyMatch(existing ->
                        existing.getSubject() != null && existing.getGroup() != null &&
                                existing.getSubject().equals(newEntry.getSubject()) &&
                                existing.getGroup().equals(newEntry.getGroup()) &&
                                existing.getDay().equals(newEntry.getDay()) &&
                                existing.getFrom().equals(newEntry.getFrom()) &&
                                existing.getTo().equals(newEntry.getTo())
                );

                if (!exists) student.getSchedule().add(newEntry);
            }
        } else {
            ScheduleEntry basic = new ScheduleEntry();
            basic.setSubject(subjectId);
            basic.setGroup(updated.getGroupId());
            student.getSchedule().add(basic);
        }

        if (student.getEnrolledSubjects() == null) student.setEnrolledSubjects(new ArrayList<>());
        if (subjectId != null && !student.getEnrolledSubjects().contains(subjectId)) {
            student.getEnrolledSubjects().add(subjectId);
        }

        studentRepository.save(student);
        historyService.addHistoryEvent(UUID.randomUUID(), "SYSTEM", "STUDENT_ASSIGNED",
                "Estudiante " + studentId + " inscrito en grupo " + groupId, "SYSTEM");

        double load = (updated.getCurrentCapacity() * 100.0) / Math.max(1, updated.getMaximumCapacity());
        if (load >= 90.0) {
            alertService.update(updated);
        }

        return groupMapper.toDTO(updated);
    }

    @Override
    public GroupDTO removeStudentFromGroup(String groupId, int studentId) {
        Student student = message.findStudentOrThrow(studentId);
        Group group = message.findGroupOrThrow(groupId);
        message.ensureStudentIsInGroup(student, groupId);
        message.ensureGroupHasCapacityGreaterThanZero(group);

        Query q = Query.query(Criteria.where("groupId").is(groupId)
                .and("currentCapacity").gt(0));
        Update u = new Update()
                .inc("currentCapacity", -1)
                .pull("waitlist", studentId);

        Group updated = mongoTemplate.findAndModify(
                q, u, FindAndModifyOptions.options().returnNew(true), Group.class
        );

        message.ensureAtomicUpdateSucceeded(updated,
                "No se pudo retirar: capacidad ya en 0 o modificación concurrente");

        if (student.getSchedule() != null) {
            student.getSchedule().removeIf(se ->
                    se.getGroup() != null && se.getGroup().equals(groupId));
        }

        if (student.getEnrolledSubjects() != null) {
            boolean stillHasSubject = student.getSchedule() != null && student.getSchedule().stream()
                    .anyMatch(se -> se.getSubject() != null && se.getSubject().equals(group.getSubjectId()));
            if (!stillHasSubject) {
                student.getEnrolledSubjects().removeIf(sid -> sid.equals(group.getSubjectId()));
            }
        }

        studentRepository.save(student);
        historyService.addHistoryEvent(UUID.randomUUID(), "SYSTEM", "STUDENT_REMOVED",
                "Estudiante " + studentId + " retirado de grupo " + groupId, "SYSTEM");

        return groupMapper.toDTO(updated);
    }

}
