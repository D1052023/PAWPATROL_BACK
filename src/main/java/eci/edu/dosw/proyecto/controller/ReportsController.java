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
    public List<ReassignmentStatsDTO> bySubject() {
        return reportService.statsBySubject();
    }

    @Operation(summary = "Estadísticas por grupo")
    @GetMapping("/groups")
    public List<ReassignmentStatsDTO> byGroup() {
        return reportService.statsByGroup();
    }

    @Operation(summary = "Estadísticas por decanería")
    @GetMapping("/deaneries")
    public List<ReassignmentStatsDTO> byDeanery() {
        return reportService.statsByDeanery();
    }

    @Operation(summary = "Estadísticas globales")
    @GetMapping("/global")
    public ReassignmentStatsDTO global() {
        return reportService.statsGlobal();
    }
}
