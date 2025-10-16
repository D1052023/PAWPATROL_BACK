package eci.edu.dosw.proyecto.services;

import eci.edu.dosw.proyecto.dtos.ReassignmentStatsDTO;

import java.util.List;

public interface ReassignmentReportService {
    List<ReassignmentStatsDTO> statsBySubject();
    List<ReassignmentStatsDTO> statsByGroup();
    List<ReassignmentStatsDTO> statsByDeanery();
    ReassignmentStatsDTO statsGlobal();
}
