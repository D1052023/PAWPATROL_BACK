package eci.edu.dosw.proyecto.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import eci.edu.dosw.proyecto.dtos.ScheduleEntryDTO;
import eci.edu.dosw.proyecto.dtos.StudentDTO;
import eci.edu.dosw.proyecto.dtos.GroupDTO;
import eci.edu.dosw.proyecto.services.GroupService;

/**
 * Clase controlador para gestionar el CRUD de los grupos de las materias.
 */
@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un nuevo grupo")
    public GroupDTO createGroup(@RequestBody GroupDTO groupDTO) {
        return groupService.createGroup(groupDTO);
    }

    @GetMapping
    @Operation(summary = "Listar todos los grupos")
    public List<GroupDTO> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener grupo por ID")
    public GroupDTO getGroupById(@Parameter(description = "ID del grupo a consultar") @PathVariable String id) {
        return groupService.getGroupById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar grupo completamente")
    public GroupDTO updateGroup(@Parameter(description = "ID del grupo a actualizar") @PathVariable String id, @RequestBody GroupDTO groupDTO) {
        return groupService.updateGroup(id, groupDTO);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar parcialmente un grupo")
    public GroupDTO partialUpdateGroup(@Parameter(description = "ID del grupo a actualizar parcialmente") @PathVariable String id, @RequestBody GroupDTO groupDTO) {
        return groupService.partialUpdateGroup(id, groupDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar grupo")
    public void deleteGroup(@Parameter(description = "ID del grupo a eliminar") @PathVariable String id) {
        groupService.deleteGroup(id);
    }

    @PatchMapping("/{id}/capacity")
    @Operation(summary = "Actualizar capacidad máxima")
    public GroupDTO updateCapacity(@Parameter(description = "ID del grupo") @PathVariable String id, @RequestParam int newCapacity) {
        return groupService.updateCapacity(id, newCapacity);
    }

    @GetMapping("/{groupId}/MaxCapacity")
    @Operation(summary = "Consultar capacidad máxima")
    public int getMaxCapacity(@Parameter(description = "ID del grupo") @PathVariable String groupId) {
        return groupService.getMaxCapacity(groupId);
    }

    @GetMapping("/{groupId}/CurrentCapacity")
    @Operation(summary = "Consultar capacidad actual")
    public int getCurrentCapacity(@Parameter(description = "ID del grupo") @PathVariable String groupId) {
        return groupService.getCurrentCapacity(groupId);
    }

    @GetMapping("/{groupId}/enrolled")
    @Operation(summary = "Obtener número de estudiantes inscritos")
    public Integer getEnrolledCount(@Parameter(description = "ID del grupo") @PathVariable String groupId) {
        return groupService.getEnrolledCount(groupId);
    }

    @PutMapping("/{groupId}/AssignTeacher/{teacherId}")
    @Operation(summary = "Asignar profesor a grupo")
    public GroupDTO assignTeacherToGroup(@Parameter(description = "ID del grupo") @PathVariable String groupId, @Parameter(description = "ID del profesor") @PathVariable int teacherId) {
        return groupService.assignTeacherToGroup(groupId, teacherId);
    }

    @PutMapping("/{groupId}/RemoveTeacher")
    @Operation(summary = "Remover profesor de grupo")
    public GroupDTO removeTeacherFromGroup(@Parameter(description = "ID del grupo") @PathVariable String groupId) {
        return groupService.removeTeacherFromGroup(groupId);
    }

    @GetMapping("/teacher/{teacherId}")
    @Operation(summary = "Obtener grupos por profesor")
    public List<GroupDTO> getGroupsByTeacher(@Parameter(description = "ID del profesor") @PathVariable int teacherId) {
        return groupService.getGroupsByTeacher(teacherId);
    }

    @GetMapping("/subject/{subjectId}")
    @Operation(summary = "Obtener grupos por asignatura")
    public List<GroupDTO> getGroupsBySubject(@Parameter(description = "ID de la asignatura") @PathVariable String subjectId) {
        return groupService.getGroupsBySubject(subjectId);
    }

    @PostMapping("/{groupId}/students/{studentId}")
    @Operation(summary = "Asignar estudiante a grupo")
    public GroupDTO assignStudentToGroup(@PathVariable String groupId, @PathVariable int studentId) {
        return groupService.assignStudentToGroup(groupId, studentId);
    }

    @DeleteMapping("/{groupId}/students/{studentId}")
    @Operation(summary = "Remover estudiante de grupo")
    public void removeStudentFromGroup(@PathVariable String groupId, @PathVariable int studentId) {
        groupService.removeStudentFromGroup(groupId, studentId);
    }

    @GetMapping("/{groupId}/waitlist")
    @Operation(summary = "Obtener lista de espera")
    public List<Integer> getWaitlist(@Parameter(description = "ID del grupo") @PathVariable String groupId) {
        return groupService.getWaitlist(groupId);
    }

    @GetMapping("/{groupId}/waitlist/details")
    @Operation(summary = "Obtener lista de espera detallada")
    public List<StudentDTO> getWaitlistDetails(@Parameter(description = "ID del grupo") @PathVariable String groupId) {
        return groupService.getWaitlistDetails(groupId);
    }

    @GetMapping("/{groupId}/schedule")
    @Operation(summary = "Obtener horario de grupo")
    public List<ScheduleEntryDTO> getSchedule(@Parameter(description = "ID del grupo") @PathVariable String groupId) {
        return groupService.getSchedule(groupId);
    }

    @PostMapping("/{groupId}/Addschedule")
    @Operation(summary = "Agregar horario")
    public ScheduleEntryDTO addScheduleEntry(@Parameter(description = "ID del grupo") @PathVariable String groupId, @RequestBody ScheduleEntryDTO entry) {
        return groupService.addScheduleEntry(groupId, entry);
    }

    @PutMapping("/{groupId}/schedule")
    @Operation(summary = "Actualizar horario")
    public List<ScheduleEntryDTO> updateScheduleGlobal(@Parameter(description = "ID del grupo") @PathVariable String groupId, @RequestBody List<ScheduleEntryDTO> entries) {
        return groupService.updateScheduleGlobal(groupId, entries);
    }

    @PutMapping("/{groupId}/schedule/day/{day}")
    @Operation(summary = "Actualizar horario por día")
    public List<ScheduleEntryDTO> updateScheduleForDay(@Parameter(description = "ID del grupo") @PathVariable String groupId,
                                                       @Parameter(description = "Día específico a actualizar") @PathVariable String day, @RequestBody List<ScheduleEntryDTO> entries) {
        return groupService.updateScheduleForDay(groupId, day, entries);
    }

    @DeleteMapping("/{groupId}/schedule")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar horario completo")
    public void deleteScheduleGlobal(@Parameter(description = "ID del grupo") @PathVariable String groupId) {
        groupService.deleteScheduleGlobal(groupId);
    }

    @DeleteMapping("/{groupId}/schedule/day/{day}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar horario por día")
    public void deleteScheduleForDay(@Parameter(description = "ID del grupo") @PathVariable String groupId, @Parameter(description = "Día específico") @PathVariable String day) {
        groupService.deleteScheduleForDay(groupId, day);
    }

}