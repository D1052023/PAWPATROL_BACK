package eci.edu.dosw.proyecto.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import lombok.RequiredArgsConstructor;

import eci.edu.dosw.proyecto.dtos.TeacherDTO;
import eci.edu.dosw.proyecto.services.TeacherService;

/**
 * Clase controlador para el CRUD de los profesores y sus funcionalidades.
 */
@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @Operation(summary = "Crear un nuevo profesor")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeacherDTO createTeacher(@Parameter(description = "Datos del profesor a crear") @RequestBody TeacherDTO teacherDTO) {
        return teacherService.createTeacher(teacherDTO);
    }

    @Operation(summary = "Listar todos los profesores")
    @GetMapping
    public List<TeacherDTO> getAllTeachers() {
        return teacherService.getAllTeachers();
    }

    @Operation(summary = "Obtener profesor por ID")
    @GetMapping("/{id}")
    public TeacherDTO getTeacherById(@Parameter(description = "ID del profesor") @PathVariable Integer id) {
        return teacherService.getTeacherById(id);
    }

    @Operation(summary = "Actualizar profesor completamente")
    @PutMapping("/{id}")
    public TeacherDTO updateTeacher(@Parameter(description = "ID del profesor a actualizar") @PathVariable Integer id, @Parameter(description = "Datos completos del profesor") @RequestBody TeacherDTO teacherDTO) {
        return teacherService.updateTeacher(id, teacherDTO);
    }

    @Operation(summary = "Actualizar parcialmente un profesor")
    @PatchMapping("/{id}")
    public TeacherDTO partialUpdateTeacher(@Parameter(description = "ID del profesor a actualizar parcialmente") @PathVariable Integer id, @Parameter(description = "Campos a actualizar") @RequestBody TeacherDTO teacherDTO) {
        return teacherService.partialUpdateTeacher(id, teacherDTO);
    }

    @Operation(summary = "Eliminar profesor")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeacher(@Parameter(description = "ID del profesor a eliminar") @PathVariable Integer id) {
        teacherService.deleteTeacher(id);
    }

    @Operation(summary = "Obtener profesor por email")
    @GetMapping("/email/{email}")
    public TeacherDTO getTeacherByEmail(@Parameter(description = "Email del profesor") @PathVariable String email) {
        return teacherService.getTeacherByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Profesor no encontrado con email: " + email));
    }
}