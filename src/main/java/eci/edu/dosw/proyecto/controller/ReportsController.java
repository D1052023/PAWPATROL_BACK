package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dtos.ReassignmentStatsDTO;
import eci.edu.dosw.proyecto.services.ReassignmentReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports/reassignments")
@RequiredArgsConstructor
public class ReportsController {

    private final ReassignmentReportService reportService;

    @GetMapping("/subjects")
    public ResponseEntity<List<ReassignmentStatsDTO>> bySubject() {
        return ResponseEntity.ok(reportService.statsBySubject());
    }

    @GetMapping("/groups")
    public ResponseEntity<List<ReassignmentStatsDTO>> byGroup() {
        return ResponseEntity.ok(reportService.statsByGroup());
    }

    @GetMapping("/deaneries")
    public ResponseEntity<List<ReassignmentStatsDTO>> byDeanery() {
        return ResponseEntity.ok(reportService.statsByDeanery());
    }

    @GetMapping("/global")
    public ResponseEntity<ReassignmentStatsDTO> global() {
        return ResponseEntity.ok(reportService.statsGlobal());
    }
}
