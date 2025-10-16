package eci.edu.dosw.proyecto.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

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

    @Operation(summary = "Crear una nueva materia")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubjectDTO createSubject(@Parameter(description = "Datos de la materia a crear") @RequestBody SubjectDTO subjectDTO) {
        return subjectService.createSubject(subjectDTO);
    }

    @Operation(summary = "Listar todas las materias")
    @GetMapping
    public List<SubjectDTO> getAllSubjects() {
        return subjectService.getAllSubjects();
    }

    @Operation(summary = "Obtener materia por ID")
    @GetMapping("/{id}")
    public SubjectDTO getSubjectById(@Parameter(description = "ID de la materia") @PathVariable String id) {
        return subjectService.getSubjectById(id);
    }

    @Operation(summary = "Actualizar materia completamente")
    @PutMapping("/{id}")
    public SubjectDTO updateSubject(@Parameter(description = "ID de la materia a actualizar") @PathVariable String id,
            @Parameter(description = "Datos completos de la materia") @RequestBody SubjectDTO subjectDTO) {
        return subjectService.updateSubject(id, subjectDTO);
    }

    @Operation(summary = "Actualizar parcialmente una materia")
    @PatchMapping("/{id}")
    public SubjectDTO partialUpdateSubject(@Parameter(description = "ID de la materia a actualizar parcialmente") @PathVariable String id, 
            @Parameter(description = "Campos a actualizar") @RequestBody SubjectDTO subjectDTO) {
        return subjectService.partialUpdateSubject(id, subjectDTO);
    }

    @Operation(summary = "Eliminar materia")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubject(@Parameter(description = "ID de la materia a eliminar") @PathVariable String id) {
        subjectService.deleteSubject(id);
    }

    @Operation(summary = "Obtener materias de un profesor")
    @GetMapping("/teacher/{teacherId}")
    public List<SubjectDTO> getSubjectsByTeacher(@Parameter(description = "ID del profesor") @PathVariable int teacherId) {
        return subjectService.getSubjectsByTeacher(teacherId);
    }

    @Operation(summary = "Actualizar capacidad máxima de una materia")
    @PatchMapping("/{subjectId}/capacity")
    public ResponseEntity<SubjectDTO> updateSubjectCapacity(@Parameter(description = "ID de la materia") @PathVariable String subjectId,
            @Parameter(description = "Nueva capacidad máxima") @RequestParam int newCapacity) {
        SubjectDTO dto = new SubjectDTO();
        dto.setMaximumCapacity(newCapacity);
        return ResponseEntity.ok(subjectService.partialUpdateSubject(subjectId, dto));
    }

    @Operation(summary = "Asignar estudiante a una materia")
    @PostMapping("/{subjectId}/assign/{studentId}")
    public ResponseEntity<SubjectDTO> assignStudentToSubject(@Parameter(description = "ID de la materia") @PathVariable String subjectId, @Parameter(description = "ID del estudiante") @PathVariable int studentId) {
        return ResponseEntity.ok(subjectService.assignStudentToSubject(subjectId, studentId));
    }

    @Operation(summary = "Remover estudiante de una materia")
    @DeleteMapping("/{subjectId}/remove/{studentId}")
    public ResponseEntity<SubjectDTO> removeStudentFromSubject(@Parameter(description = "ID de la materia") @PathVariable String subjectId, @Parameter(description = "ID del estudiante") @PathVariable int studentId) {
        return ResponseEntity.ok(subjectService.removeStudentFromSubject(subjectId, studentId));
    }
}