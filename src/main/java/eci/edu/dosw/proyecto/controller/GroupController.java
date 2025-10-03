package eci.edu.dosw.proyecto.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
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
        return groupService.updateGroup(id, groupDTO);
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
    public List<Integer> getWaitlist(@PathVariable String groupId) {
        return groupService.getWaitlist(groupId);
    }
}
