package eci.edu.dosw.proyecto.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

import eci.edu.dosw.proyecto.dtos.SubjectDTO;
import eci.edu.dosw.proyecto.services.SubjectService;

/**
 * Clase controlador para manejar el CRUD de materias  y funcionalidades.
 */
@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubjectDTO createSubject(@RequestBody SubjectDTO subjectDTO) {
        return subjectService.createSubject(subjectDTO);
    }

    @GetMapping
    public List<SubjectDTO> getAllSubjects() {
        return subjectService.getAllSubjects();
    }

    @GetMapping("/{id}")
    public SubjectDTO getSubjectById(@PathVariable String id) {
        return subjectService.getSubjectById(id);
    }

    @PutMapping("/{id}")
    public SubjectDTO updateSubject(@PathVariable String id, @RequestBody SubjectDTO subjectDTO) {
        return subjectService.updateSubject(id, subjectDTO);
    }

    @PatchMapping("/{id}")
    public SubjectDTO partialUpdateSubject(@PathVariable String id, @RequestBody SubjectDTO subjectDTO) {
        return subjectService.partialUpdateSubject(id, subjectDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubject(@PathVariable String id) {
        subjectService.deleteSubject(id);
    }
}
