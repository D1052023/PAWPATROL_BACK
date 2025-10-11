package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dtos.ScheduleEntryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import eci.edu.dosw.proyecto.dtos.GroupDTO;
import eci.edu.dosw.proyecto.services.GroupService;

/**
 * Clase controlador para el CRUD de los grupos y sus funcionalidades.
 */
@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupDTO createGroup(@RequestBody GroupDTO groupDTO) {
        return groupService.createGroup(groupDTO);
    }

    @GetMapping
    public List<GroupDTO> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/{id}")
    public GroupDTO getGroupById(@PathVariable String id) {
        return groupService.getGroupById(id);
    }

    @PutMapping("/{id}")
    public GroupDTO updateGroup(@PathVariable String id, @RequestBody GroupDTO groupDTO) {
        return groupService.updateGroup(id, groupDTO);
    }

    @PatchMapping("/{id}")
    public GroupDTO partialUpdateGroup(@PathVariable String id, @RequestBody GroupDTO groupDTO) {
        return groupService.partialUpdateGroup(id, groupDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroup(@PathVariable String id) {
        groupService.deleteGroup(id);
    }

    @PatchMapping("/{id}/capacity")
    public GroupDTO updateCapacity(@PathVariable String id, @RequestParam int newCapacity) {
        return groupService.updateCapacity(id, newCapacity);
    }

    @GetMapping("/{id}/waitlist")
    public List<Integer> getWaitlist(@PathVariable String id) {
        return groupService.getWaitlist(id);
    }

    @GetMapping("/teacher/{teacherId}")
    public List<GroupDTO> getGroupsByTeacher(@PathVariable int teacherId) {
        return groupService.getGroupsByTeacher(teacherId);
    }

    @GetMapping("/{groupId}/MaxCapacity")
    public int getMaxCapacity(@PathVariable String groupId) {
        return groupService.getMaxCapacity(groupId);
    }

    @GetMapping("/{groupId}/CurrentCapacity")
    public int getCurrentCapacity(@PathVariable String groupId) {
        return groupService.getCurrentCapacity(groupId);
    }

    @GetMapping("/{groupId}/WaitingList")
    public List<Integer> getWaitingList(@PathVariable String groupId) {
        return groupService.getWaitlist(groupId);
    }

    @PutMapping("/{groupId}/AssignTeacher/{teacherId}")
    public ResponseEntity<GroupDTO> assignTeacherToGroup(@PathVariable String groupId, @PathVariable int teacherId) {
        return ResponseEntity.ok(groupService.assignTeacherToGroup(groupId, teacherId));
    }

    @PutMapping("/{groupId}/RemoveTeacher")
    public ResponseEntity<GroupDTO> removeTeacherFromGroup(@PathVariable String groupId) {
        return ResponseEntity.ok(groupService.removeTeacherFromGroup(groupId));
    }

    @GetMapping("/subject/{subjectId}")
    public List<GroupDTO> getGroupsBySubject(@PathVariable String subjectId) {
        return groupService.getGroupsBySubject(subjectId);
    }

    @GetMapping("/{groupId}/enrolled")
    public ResponseEntity<Integer> getEnrolledCount(@PathVariable String groupId) {
        return ResponseEntity.ok(groupService.getEnrolledCount(groupId));
    }

    @GetMapping("/{groupId}/schedule")
    public ResponseEntity<List<ScheduleEntryDTO>> getSchedule(@PathVariable String groupId) {
        return ResponseEntity.ok(groupService.getSchedule(groupId));
    }

    @PostMapping("/{groupId}/schedule")
    public ResponseEntity<ScheduleEntryDTO> addScheduleEntry(@PathVariable String groupId,
                                                             @RequestBody ScheduleEntryDTO entry) {
        ScheduleEntryDTO created = groupService.addScheduleEntry(groupId, entry);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{groupId}/schedule")
    public ResponseEntity<List<ScheduleEntryDTO>> updateScheduleGlobal(@PathVariable String groupId,
                                                                       @RequestBody List<ScheduleEntryDTO> entries) {
        return ResponseEntity.ok(groupService.updateScheduleGlobal(groupId, entries));
    }

    @PutMapping("/{groupId}/schedule/day/{day}")
    public ResponseEntity<List<ScheduleEntryDTO>> updateScheduleForDay(@PathVariable String groupId,
                                                                       @PathVariable String day,
                                                                       @RequestBody List<ScheduleEntryDTO> entries) {
        return ResponseEntity.ok(groupService.updateScheduleForDay(groupId, day, entries));
    }

    @DeleteMapping("/{groupId}/schedule")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteScheduleGlobal(@PathVariable String groupId) {
        groupService.deleteScheduleGlobal(groupId);
    }

    @DeleteMapping("/{groupId}/schedule/day/{day}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteScheduleForDay(@PathVariable String groupId, @PathVariable String day) {
        groupService.deleteScheduleForDay(groupId, day);
    }

}
