package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.dtos.ReassignmentStatsDTO;
import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.models.ChangeRequest;
import eci.edu.dosw.proyecto.repositories.ChangeRequestRepository;
import eci.edu.dosw.proyecto.services.impl.ReassignmentReportServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReassignmentReportServiceImplTest {

    @Mock
    private ChangeRequestRepository changeRequestRepository;

    @InjectMocks
    private ReassignmentReportServiceImpl service;

    @Test
    void shouldComputeStatsBySubjectAndGlobal() {

        LocalDateTime now = LocalDateTime.now();
        ChangeRequest s1a = new ChangeRequest();
        s1a.setId(UUID.randomUUID());
        s1a.setTargetSubject("S1");
        s1a.setStatus(RequestStatus.APPROVED);
        s1a.setExceptional(false);
        s1a.setCreatedAt(now.minusMinutes(60));
        s1a.setUpdatedAt(now);
        ChangeRequest s1b = new ChangeRequest();
        s1b.setId(UUID.randomUUID());
        s1b.setTargetSubject("S1");
        s1b.setStatus(RequestStatus.REJECTED);
        s1b.setExceptional(true);
        s1b.setCreatedAt(now.minusMinutes(120));
        s1b.setUpdatedAt(now);
        ChangeRequest s2 = new ChangeRequest();
        s2.setId(UUID.randomUUID());
        s2.setTargetSubject("S2");
        s2.setStatus(RequestStatus.PENDING);
        s2.setExceptional(false);
        s2.setCreatedAt(now.minusMinutes(30));
        s2.setUpdatedAt(null);
        List<ChangeRequest> all = List.of(s1a, s1b, s2);
        when(changeRequestRepository.findAll()).thenReturn(all);
        List<ReassignmentStatsDTO> bySubject = service.statsBySubject();

        assertNotNull(bySubject);
        assertEquals(2, bySubject.size());
        ReassignmentStatsDTO expectedS1 = new ReassignmentStatsDTO("S1","S1", 2L, 1L, 1L, 0L, 1L, 1.5);
        ReassignmentStatsDTO expectedS2 = new ReassignmentStatsDTO("S2","S2", 1L, 0L, 0L, 1L, 0L, null);
        assertEquals(expectedS1, bySubject.get(0));
        assertEquals(expectedS2, bySubject.get(1));
        ReassignmentStatsDTO global = service.statsGlobal();
        ReassignmentStatsDTO expectedGlobal = new ReassignmentStatsDTO("GLOBAL","GLOBAL", 3L, 1L, 1L, 1L, 1L, 1.5);
        assertEquals(expectedGlobal, global);

    }

    @Test
    void shouldComputeStatsByGroupAndDeaneryAndHandleUnknowns() {

        LocalDateTime now = LocalDateTime.now();
        ChangeRequest a = new ChangeRequest();
        a.setId(UUID.randomUUID());
        a.setTargetGroup("G-A");
        a.setTargetSubject("SUB-A");
        a.setFaculty(Faculty.INGENIERIA_DE_SISTEMAS);
        a.setStatus(RequestStatus.APPROVED);
        a.setCreatedAt(now.minusMinutes(30));
        a.setUpdatedAt(now);
        a.setExceptional(false);
        ChangeRequest b = new ChangeRequest();
        b.setId(UUID.randomUUID());
        b.setTargetGroup(null);
        b.setTargetSubject(null);
        b.setFaculty(null);
        b.setStatus(RequestStatus.REJECTED);
        b.setCreatedAt(now.minusMinutes(90));
        b.setUpdatedAt(now);
        b.setExceptional(true);
        ChangeRequest c = new ChangeRequest();
        c.setId(UUID.randomUUID());
        c.setTargetGroup("G-A");
        c.setTargetSubject("SUB-A");
        c.setFaculty(Faculty.INGENIERIA_DE_SISTEMAS);
        c.setStatus(RequestStatus.PENDING);
        c.setCreatedAt(now.minusMinutes(10));
        c.setUpdatedAt(null);
        c.setExceptional(false);
        List<ChangeRequest> all = List.of(a, b, c);
        when(changeRequestRepository.findAll()).thenReturn(all);
        List<ReassignmentStatsDTO> byGroup = service.statsByGroup();
        assertEquals(2, byGroup.size());
        ReassignmentStatsDTO expectedGA = new ReassignmentStatsDTO("G-A","G-A", 2L, 1L, 0L, 1L, 0L, 0.5); // a:30min -> 0.5h avg
        ReassignmentStatsDTO expectedUnknown = new ReassignmentStatsDTO("UNKNOWN","UNKNOWN", 1L, 0L, 1L, 0L, 1L, 1.5); // b:90min -> 1.5h
        assertEquals(expectedGA, byGroup.get(0));
        assertEquals(expectedUnknown, byGroup.get(1));
        List<ReassignmentStatsDTO> byDeanery = service.statsByDeanery();
        ReassignmentStatsDTO expectedFaculty = new ReassignmentStatsDTO(Faculty.INGENIERIA_DE_SISTEMAS.name(), Faculty.INGENIERIA_DE_SISTEMAS.name(), 2L, 1L, 0L, 1L, 0L, 0.5);
        ReassignmentStatsDTO expectedFacultyUnknown = new ReassignmentStatsDTO("UNKNOWN","UNKNOWN", 1L, 0L, 1L, 0L, 1L, 1.5);

        assertEquals(expectedFaculty, byDeanery.get(0));
        assertEquals(expectedFacultyUnknown, byDeanery.get(1));
    }
}
