package eci.edu.dosw.proyecto.controller;

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

    @GetMapping("/subject/{subjectId}")
    public List<GroupDTO> getGroupsBySubject(@PathVariable String subjectId) {
        return groupService.getGroupsBySubject(subjectId);
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

}
