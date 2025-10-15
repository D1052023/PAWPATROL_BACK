package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.controller.ReportsController;
import eci.edu.dosw.proyecto.dtos.ReassignmentStatsDTO;
import eci.edu.dosw.proyecto.services.ReassignmentReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReportsControllerTest {

    @Mock
    ReassignmentReportService reportService;

    @InjectMocks
    ReportsController controller;

    @Test
    void shouldReturnBySubject() {
        ReassignmentStatsDTO dto = new ReassignmentStatsDTO("DOSW", "DOSW", 1, 0, 0, 1, 0, null);
        when(reportService.statsBySubject()).thenReturn(List.of(dto));
        List<ReassignmentStatsDTO> res = controller.bySubject();

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals("DOSW", res.get(0).getKey());
    }

    @Test
    void shouldReturnByGroup() {
        ReassignmentStatsDTO dto = new ReassignmentStatsDTO("G-1", "G-1", 2, 1, 0, 1, 0, 1.25);
        when(reportService.statsByGroup()).thenReturn(List.of(dto));
        List<ReassignmentStatsDTO> res = controller.byGroup();

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals("G-1", res.get(0).getKey());
        assertEquals(2, res.get(0).getTotalRequests());

    }

    @Test
    void shouldReturnByDeanery() {
        ReassignmentStatsDTO dto = new ReassignmentStatsDTO("ING", "ING", 3, 2, 0, 1, 0, 0.5);
        when(reportService.statsByDeanery()).thenReturn(List.of(dto));
        List<ReassignmentStatsDTO> res = controller.byDeanery();

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals("ING", res.get(0).getKey());

    }

    @Test
    void shouldReturnGlobal() {
        ReassignmentStatsDTO dto = new ReassignmentStatsDTO("GLOBAL", "GLOBAL", 10, 6, 2, 2, 1, 2.0);
        when(reportService.statsGlobal()).thenReturn(dto);
        ReassignmentStatsDTO res = controller.global();

        assertNotNull(res);
        assertEquals("GLOBAL", res.getKey());
        assertEquals(10, res.getTotalRequests());

    }
}
