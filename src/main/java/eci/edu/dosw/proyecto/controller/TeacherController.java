package eci.edu.dosw.proyecto.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import eci.edu.dosw.proyecto.dtos.TeacherDTO;
import eci.edu.dosw.proyecto.services.TeacherService;

import lombok.RequiredArgsConstructor;

/**
 * Clase controlador para el CRUD de los profesores y sus funcionalidades.
 */
@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    public List<TeacherDTO> getAllTeachers() {
        return teacherService.getAllTeachers();
    }

    @GetMapping("/{id}")
    public TeacherDTO getTeacherById(@PathVariable Integer id) {
        return teacherService.getTeacherById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeacherDTO createTeacher(@RequestBody TeacherDTO teacherDTO) {
        return teacherService.createTeacher(teacherDTO);
    }

    @PutMapping("/{id}")
    public TeacherDTO updateTeacher(@PathVariable Integer id, @RequestBody TeacherDTO teacherDTO) {
        return teacherService.updateTeacher(id, teacherDTO);
    }

    @PatchMapping("/{id}")
    public TeacherDTO partialUpdateTeacher(@PathVariable Integer id, @RequestBody TeacherDTO teacherDTO) {
        return teacherService.partialUpdateTeacher(id, teacherDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeacher(@PathVariable Integer id) {
        teacherService.deleteTeacher(id);
    }

    @GetMapping("/email/{email}")
    public TeacherDTO getTeacherByEmail(@PathVariable String email) {
        return teacherService.getTeacherByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Profesor no encontrado con email: " + email));
    }

}
