package eci.edu.dosw.proyecto.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

import eci.edu.dosw.proyecto.dtos.ReassignmentStatsDTO;
import eci.edu.dosw.proyecto.services.ReassignmentReportService;


/**
 * Clase controlador para los reportes.
 */
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportsController {

    private final ReassignmentReportService reportService;

    @Operation(summary = "Estadísticas por asignatura")
    @GetMapping("/subjects")
    public ResponseEntity<List<ReassignmentStatsDTO>> bySubject() {
        return ResponseEntity.ok(reportService.statsBySubject());
    }

    @Operation(summary = "Estadísticas por grupo")
    @GetMapping("/groups")
    public ResponseEntity<List<ReassignmentStatsDTO>> byGroup() {
        return ResponseEntity.ok(reportService.statsByGroup());
    }

    @Operation(summary = "Estadísticas por decanería")
    @GetMapping("/deaneries")
    public ResponseEntity<List<ReassignmentStatsDTO>> byDeanery() {
        return ResponseEntity.ok(reportService.statsByDeanery());
    }

    @Operation(summary = "Estadísticas globales")
    @GetMapping("/global")
    public ResponseEntity<ReassignmentStatsDTO> global() {
        return ResponseEntity.ok(reportService.statsGlobal());
    }
}
