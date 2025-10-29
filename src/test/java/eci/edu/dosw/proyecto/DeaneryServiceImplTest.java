package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.DeaneryDTO;
import eci.edu.dosw.proyecto.dtos.RequestDatesDTO;
import eci.edu.dosw.proyecto.dtos.RequestDecisionDTO;
import eci.edu.dosw.proyecto.enums.AcademicTrafficLight;
import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.mappers.ChangeRequestMapper;
import eci.edu.dosw.proyecto.mappers.DeaneryMapper;
import eci.edu.dosw.proyecto.models.*;
import eci.edu.dosw.proyecto.repositories.ChangeRequestRepository;
import eci.edu.dosw.proyecto.repositories.DeaneryRepository;
import eci.edu.dosw.proyecto.repositories.GroupRepository;
import eci.edu.dosw.proyecto.repositories.StudentRepository;
import eci.edu.dosw.proyecto.services.AlertService;
import eci.edu.dosw.proyecto.services.HistoryService;
import eci.edu.dosw.proyecto.services.impl.DeaneryServiceImpl;

import eci.edu.dosw.proyecto.util.MessageExceptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class DeaneryServiceImplTest {

    @Mock
    DeaneryRepository deaneryRepository;

    @Mock
    ChangeRequestRepository changeRequestRepository;

    @Mock
    StudentRepository studentRepository;

    @Mock
    DeaneryMapper deaneryMapper;

    @Mock
    ChangeRequestMapper changeRequestMapper;

    @Mock
    AlertService alertService;

    @Mock
    GroupRepository groupRepository;

    @Mock
    HistoryService historyService;

    @Mock
    MessageExceptions message;

    @InjectMocks
    DeaneryServiceImpl deaneryService;

    @Test
    void ShouldCreateDeanery() {
        DeaneryDTO in = new DeaneryDTO();
        in.setName("Claudia Patricia Santiago Cely");
        in.setEmail("claudia.santiago@escuelaing.edu.co");

        Deanery entity = new Deanery();
        entity.setName(in.getName());
        entity.setEmail(in.getEmail());

        Deanery saved = new Deanery();
        saved.setId(1);
        saved.setName(in.getName());
        saved.setEmail(in.getEmail());

        DeaneryDTO out = new DeaneryDTO();
        out.setId(1);
        out.setName(in.getName());
        out.setEmail(in.getEmail());

        when(deaneryMapper.toEntity(in)).thenReturn(entity);
        when(deaneryRepository.save(entity)).thenReturn(saved);
        when(deaneryMapper.toDTO(saved)).thenReturn(out);

        DeaneryDTO res = deaneryService.createDeanery(in);
        assertNotNull(res);
        assertEquals(1, res.getId());
        assertEquals("Claudia Patricia Santiago Cely", res.getName());
    }


    @Test
    void ShouldGetAllDeaneries() {
        Deanery d1 = new Deanery(); d1.setId(1); d1.setName("D1");
        Deanery d2 = new Deanery(); d2.setId(2); d2.setName("D2");

        when(deaneryRepository.findAll()).thenReturn(List.of(d1, d2));
        when(deaneryMapper.toDTO(d1)).thenReturn(new DeaneryDTO(){ { setId(1); setName("D1"); } });
        when(deaneryMapper.toDTO(d2)).thenReturn(new DeaneryDTO(){ { setId(2); setName("D2"); } });

        List<DeaneryDTO> out = deaneryService.getAllDeaneries();
        assertEquals(2, out.size());
    }


    @Test
    void shouldCreateDeanery() {
        DeaneryDTO input = new DeaneryDTO();
        input.setName("Claudia Patricia Santiago Cely");
        input.setEmail("claudia.santiago@escuelaing.edu.co");

        Deanery entity = new Deanery();
        entity.setName(input.getName());
        entity.setEmail(input.getEmail());
        Deanery saved = new Deanery();
        saved.setId(1000000451);
        saved.setName(input.getName());
        saved.setEmail(input.getEmail());
        DeaneryDTO expected = new DeaneryDTO();
        expected.setId(1000000451);
        expected.setName(input.getName());
        expected.setEmail(input.getEmail());

        when(deaneryMapper.toEntity(input)).thenReturn(entity);
        when(deaneryRepository.save(entity)).thenReturn(saved);
        when(deaneryMapper.toDTO(saved)).thenReturn(expected);
        DeaneryDTO result = deaneryService.createDeanery(input);

        assertNotNull(result);
        assertEquals(1000000451, result.getId());
        assertEquals("Claudia Patricia Santiago Cely", result.getName());
    }

    @Test
    void shouldGetDeaneryById() {
        Deanery d = new Deanery();
        d.setId(1000000143);
        d.setName("Oswaldo Castillo Navetty");
        d.setEmail("oswaldo.castillo@escuelaing.edu.co");
        d.setFaculty(Faculty.INGENIERIA_DE_SISTEMAS);

        DeaneryDTO dto = new DeaneryDTO();
        dto.setId(1000000143);
        dto.setName(d.getName());
        dto.setEmail(d.getEmail());

        when(message.findDeaneryOrThrow(1000000143)).thenReturn(d);
        when(deaneryMapper.toDTO(d)).thenReturn(dto);
        DeaneryDTO res = deaneryService.getDeaneryById(1000000143);

        assertEquals(1000000143, res.getId());
        assertEquals("Oswaldo Castillo Navetty", res.getName());
    }

    @Test
    void shouldThrowWhenGetDeaneryByIdMissing() {
        when(message.findDeaneryOrThrow(999)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Decan@ no encontrado"));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> deaneryService.getDeaneryById(999));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(Optional.ofNullable(ex.getReason()).orElse("").contains("Decan@ no encontrado"));
    }

    @Test
    void shouldGetDeaneryByFaculty() {
        Deanery d = new Deanery();
        d.setId(1000000451);
        d.setName("Decana Ingenieria de Sistemas Claudia Santiago");
        d.setFaculty(Faculty.INGENIERIA_DE_SISTEMAS);

        DeaneryDTO dto = new DeaneryDTO();
        dto.setId(1000000451);
        dto.setName(d.getName());

        when(message.findDeaneryByFacultyOrThrow(Faculty.INGENIERIA_DE_SISTEMAS)).thenReturn(d);
        when(deaneryMapper.toDTO(d)).thenReturn(dto);
        DeaneryDTO res = deaneryService.getDeaneryByFaculty(Faculty.INGENIERIA_DE_SISTEMAS);

        assertEquals(1000000451, res.getId());
        assertEquals("Decana Ingenieria de Sistemas Claudia Santiago", res.getName());
    }

    @Test
    void shouldGetAllDeaneries() {
        Deanery d1 = new Deanery(); d1.setId(1000000451); d1.setName("Decana Ingenieria de Sistemas Claudia Santiago");
        Deanery d2 = new Deanery(); d2.setId(1000000455); d2.setName("Decana Ingenieria Civil Sonia Jaimes");

        when(deaneryRepository.findAll()).thenReturn(List.of(d1, d2));
        when(deaneryMapper.toDTO(d1)).thenReturn(new DeaneryDTO(){ { setId(1000000451); setName("Decana Ingenieria de Sistemas Claudia Santiago"); } });
        when(deaneryMapper.toDTO(d2)).thenReturn(new DeaneryDTO(){ { setId(1000000455); setName("Decana Ingenieria Civil Sonia Jaimes"); } });
        List<DeaneryDTO> out = deaneryService.getAllDeaneries();

        assertEquals(2, out.size());
    }

    @Test
    void shouldUpdateDeanery() {
        Deanery existing = new Deanery();
        existing.setId(1000000451);
        existing.setName("Oswaldo Castillo Navetty");
        existing.setEmail("oswaldo.castellanos@escuelaing.edu.co");

        DeaneryDTO update = new DeaneryDTO();
        update.setName("Claudia Patricia Santiago Cely");
        update.setEmail("claudia.santiago@escuelaing.edu.co");
        when(message.findDeaneryOrThrow(1000000451)).thenReturn(existing);
        when(deaneryRepository.save(existing)).thenReturn(existing);

        DeaneryDTO mapped = new DeaneryDTO();
        mapped.setId(1000000451);
        mapped.setName("Claudia Patricia Santiago Cely");
        mapped.setEmail("claudia.santiago@escuelaing.edu.co");

        when(deaneryMapper.toDTO(existing)).thenReturn(mapped);
        DeaneryDTO res = deaneryService.updateDeanery(1000000451, update);

        assertNotNull(res);
        assertEquals(1000000451, res.getId());
        assertEquals("Claudia Patricia Santiago Cely", res.getName());
        assertEquals("claudia.santiago@escuelaing.edu.co", res.getEmail());
    }


    @Test
    void shouldDeleteDeanery() {
        Deanery d = new Deanery();
        d.setId(1000000143); d.setName("Oswaldo Castillo Navetty");

        when(message.findDeaneryOrThrow(1000000143)).thenReturn(d);
        deaneryService.deleteDeanery(1000000143);
        
        verify(deaneryRepository, times(1)).deleteById(1000000143);
    }

    @Test
    void shouldRespondRequestAdditionalInfo() {
        int deaneryId = 1000000684;
        UUID reqId = UUID.randomUUID();

        Deanery dean = new Deanery();
        dean.setId(deaneryId);
        dean.setFaculty(Faculty.INGENIERIA_ELECTRICA);

        ChangeRequest req = new ChangeRequest();
        req.setId(reqId);
        req.setStatus(RequestStatus.PENDING);
        req.setFaculty(Faculty.INGENIERIA_ELECTRICA);
        when(message.findDeaneryOrThrow(deaneryId)).thenReturn(dean);
        when(message.findChangeRequestOrThrow(reqId)).thenReturn(req);

        RequestDecisionDTO decision = new RequestDecisionDTO();
        decision.setRequestAdditionalInfo(true);
        decision.setAdditionalInfoRequestMessage("adjuntar certificado");

        RequestDatesDTO dates = new RequestDatesDTO();
        dates.setStartDate(LocalDateTime.now().minusDays(1));
        dates.setEndDate(LocalDateTime.now().plusDays(1));

        ChangeRequestDTO out = new ChangeRequestDTO();
        out.setId(reqId);

        when(changeRequestMapper.toDTO(req)).thenReturn(out);
        ChangeRequestDTO res = deaneryService.respondRequestByDeanery(deaneryId, reqId, decision, dates);

        assertNotNull(res);
        assertEquals(reqId, res.getId());
    }


    @Test
    void shouldApproveRequestAndProcess() {
        int deaneryId = 1000000451;
        UUID reqId = UUID.randomUUID();
        int studentId = 1000100516;

        Deanery dean = new Deanery();
        dean.setId(deaneryId);
        dean.setFaculty(Faculty.INGENIERIA_DE_SISTEMAS);

        ChangeRequest req = new ChangeRequest();
        req.setId(reqId);
        req.setStudentId(studentId);
        req.setCurrentSubject("ODSC-3");
        req.setTargetSubject("ODSC-3");
        req.setCurrentGroup("DOSW-1");
        req.setTargetGroup("TPYC-1");
        req.setFaculty(Faculty.INGENIERIA_DE_SISTEMAS);
        req.setStatus(RequestStatus.PENDING);

        Group current = new Group();
        current.setGroupId("DOSW-1");
        current.setCurrentCapacity(5);
        current.setMaximumCapacity(30);
        current.setSubjectId("MAT101");

        Group target = new Group();
        target.setGroupId("TPYC-1");
        target.setCurrentCapacity(10);
        target.setMaximumCapacity(30);
        target.setSubjectId("MAT101");

        Student student = new Student();
        student.setId(studentId);
        student.setName("Juan Pablo Caballero");
        student.setEmail("juan.ccastellanos@mail.escuelaing.edu.co");
        student.setApprovedSubjects(new ArrayList<>());
        student.setEnrolledSubjects(new ArrayList<>());
        student.setSchedule(new ArrayList<>());

        when(message.findDeaneryOrThrow(deaneryId)).thenReturn(dean);
        when(message.findChangeRequestOrThrow(reqId)).thenReturn(req);
        when(message.findGroupOrThrow("DOSW-1")).thenReturn(current);
        when(message.findGroupOrThrow("TPYC-1")).thenReturn(target);
        when(message.findStudentOrThrow(studentId)).thenReturn(student);
        when(groupRepository.save(any(Group.class))).thenAnswer(i -> i.getArgument(0));
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));
        when(changeRequestMapper.toDTO(req)).thenReturn(new ChangeRequestDTO() {{ setId(reqId); }});
        RequestDecisionDTO decision = new RequestDecisionDTO();
        decision.setStatus(RequestStatus.APPROVED);

        RequestDatesDTO dates = new RequestDatesDTO();
        dates.setStartDate(LocalDateTime.now().minusDays(1));
        dates.setEndDate(LocalDateTime.now().plusDays(1));

        ChangeRequestDTO res = deaneryService.respondRequestByDeanery(deaneryId, reqId, decision, dates);

        assertNotNull(res);
        assertEquals(reqId, res.getId());
        assertEquals(11, target.getCurrentCapacity());
        assertTrue(student.getEnrolledSubjects().contains("ODSC-3"));
    }


    @Test
    void shouldRejectRequest() {
        int deaneryId = 1000000451;
        UUID reqId = UUID.randomUUID();

        Deanery dean = new Deanery(); dean.setId(deaneryId); dean.setFaculty(Faculty.INGENIERIA_DE_SISTEMAS);
        ChangeRequest req = new ChangeRequest(); req.setId(reqId); req.setStatus(RequestStatus.PENDING); req.setFaculty(Faculty.INGENIERIA_DE_SISTEMAS);

        when(message.findDeaneryOrThrow(deaneryId)).thenReturn(dean);
        when(message.findChangeRequestOrThrow(reqId)).thenReturn(req);
        
        RequestDecisionDTO decision = new RequestDecisionDTO();
        decision.setStatus(RequestStatus.REJECTED);
        decision.setObservations("No cumple requisitos");

        when(changeRequestMapper.toDTO(req)).thenReturn(new ChangeRequestDTO(){ { setId(reqId); } });

        RequestDatesDTO dates = new RequestDatesDTO();
        dates.setStartDate(LocalDateTime.now().minusDays(1));
        dates.setEndDate(LocalDateTime.now().plusDays(1));

        ChangeRequestDTO out = deaneryService.respondRequestByDeanery(deaneryId, reqId, decision, dates);

        assertNotNull(out);
        assertEquals(reqId, out.getId());
        assertEquals(RequestStatus.REJECTED, req.getStatus());
    }

    @Test
    void shouldThrowWhenOutsideDates() {
        int deaneryId = 1000000660;
        UUID reqId = UUID.randomUUID();

        Deanery dean = new Deanery();
        dean.setId(deaneryId);
        dean.setFaculty(Faculty.INGENIERIA_MECANICA);

        ChangeRequest req = new ChangeRequest();
        req.setId(reqId);
        req.setStatus(RequestStatus.PENDING);
        req.setFaculty(Faculty.INGENIERIA_MECANICA);

        when(message.findDeaneryOrThrow(deaneryId)).thenReturn(dean);
        when(message.findChangeRequestOrThrow(reqId)).thenReturn(req);

        RequestDecisionDTO decision = new RequestDecisionDTO();
        RequestDatesDTO dates = new RequestDatesDTO();
        dates.setStartDate(LocalDateTime.now().plusDays(1));
        dates.setEndDate(LocalDateTime.now().plusDays(2));

        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "No se pueden gestionar solicitudes fuera del periodo académico habilitado")).when(message).ensureNowWithinDates(any(LocalDateTime.class), eq(dates));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> deaneryService.respondRequestByDeanery(deaneryId, reqId, decision, dates));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void shouldGetRequestsByFacultyAndStatus() {
        ChangeRequest r1 = new ChangeRequest(); r1.setId(UUID.randomUUID()); r1.setFaculty(Faculty.ECONOMIA);
        ChangeRequest r2 = new ChangeRequest(); r2.setId(UUID.randomUUID()); r2.setFaculty(Faculty.ECONOMIA);

        when(changeRequestRepository.findByFacultyAndStatus(Faculty.ECONOMIA, RequestStatus.PENDING)).thenReturn(List.of(r1, r2));
        when(changeRequestMapper.toDTO(r1)).thenReturn(new ChangeRequestDTO() {{ setId(r1.getId()); }});
        when(changeRequestMapper.toDTO(r2)).thenReturn(new ChangeRequestDTO() {{ setId(r2.getId()); }});
        List<ChangeRequestDTO> out = deaneryService.getRequestsByFacultyAndStatus(Faculty.ECONOMIA, RequestStatus.PENDING);

        assertEquals(2, out.size());
    }

    @Test
    void shouldUpdateRequestApprove() {
        int deaneryId = 1000000451;
        UUID reqId = UUID.randomUUID();

        Deanery dean = new Deanery();
        dean.setId(deaneryId);
        dean.setFaculty(Faculty.INGENIERIA_ELECTRICA);

        ChangeRequest req = new ChangeRequest();
        req.setId(reqId);
        req.setStatus(RequestStatus.PENDING);
        req.setFaculty(Faculty.INGENIERIA_ELECTRICA);
        req.setStudentId(3001);
        req.setCurrentGroup("PRI2IS-3");
        req.setTargetGroup("ODSC-3");
        req.setCurrentSubject("S1");
        req.setTargetSubject("S2");

        Group current = new Group();
        current.setGroupId("PRI2IS-3");
        current.setCurrentCapacity(2);
        current.setMaximumCapacity(30);
        current.setSubjectId("S1");

        Group target = new Group();
        target.setGroupId("ODSC-3");
        target.setCurrentCapacity(1);
        target.setMaximumCapacity(30);
        target.setSubjectId("S2");

        Student s = new Student();
        s.setId(3001);
        s.setName("Diego Fernando Chavarro");
        s.setApprovedSubjects(new ArrayList<>());
        s.setEnrolledSubjects(new ArrayList<>());
        s.setSchedule(new ArrayList<>());

        when(message.findDeaneryOrThrow(deaneryId)).thenReturn(dean);
        when(message.findChangeRequestOrThrow(reqId)).thenReturn(req);
        when(message.findGroupOrThrow("PRI2IS-3")).thenReturn(current);
        when(message.findGroupOrThrow("ODSC-3")).thenReturn(target);
        when(message.findStudentOrThrow(3001)).thenReturn(s);
        when(groupRepository.save(any(Group.class))).thenAnswer(i -> i.getArgument(0));
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));
        when(changeRequestMapper.toDTO(req)).thenReturn(new ChangeRequestDTO() {{ setId(reqId); }});
        RequestDecisionDTO decision = new RequestDecisionDTO();
        decision.setStatus(RequestStatus.APPROVED);

        RequestDatesDTO dates = new RequestDatesDTO();
        dates.setStartDate(LocalDateTime.now().minusDays(2));
        dates.setEndDate(LocalDateTime.now().plusDays(2));

        ChangeRequestDTO out = deaneryService.updateRequestAsDeanery(deaneryId, reqId, decision, dates);

        assertNotNull(out);
        assertEquals(reqId, out.getId());
        assertEquals(RequestStatus.APPROVED, req.getStatus());
    }

    @Test
    void shouldDeleteRequestAsDeanery() {
        int deaneryId = 1000100864;
        UUID reqId = UUID.randomUUID();

        Deanery dean = new Deanery(); dean.setId(deaneryId); dean.setFaculty(Faculty.ADMINISTRACION_DE_EMPRESAS);
        ChangeRequest req = new ChangeRequest(); req.setId(reqId); req.setStudentId(4001); req.setStatus(RequestStatus.PENDING); req.setTargetGroup("FEMP-1");

        when(message.findDeaneryOrThrow(deaneryId)).thenReturn(dean);
        when(message.findChangeRequestOrThrow(reqId)).thenReturn(req);
        when(groupRepository.findByGroupId("FEMP-1")).thenReturn(Optional.of(new Group(){ { setGroupId("FEMP-1"); setWaitlist(new ArrayList<>()); } }));

        deaneryService.deleteRequestAsDeanery(deaneryId, reqId);

        verify(message).findDeaneryOrThrow(deaneryId);
        verify(message).findChangeRequestOrThrow(reqId);
        verify(groupRepository).findByGroupId("FEMP-1");

    }

    @Test
    void shouldSearchRequestsVarious() {
        ChangeRequest a = new ChangeRequest();
        a.setId(UUID.randomUUID());
        a.setFaculty(Faculty.INGENIERIA_DE_SISTEMAS);

        when(changeRequestRepository.findByFacultyOrderByPriorityAsc(Faculty.INGENIERIA_DE_SISTEMAS)).thenReturn(List.of(a));
        when(changeRequestMapper.toDTO(a)).thenReturn(new ChangeRequestDTO() {{ setId(a.getId()); }});
        List<ChangeRequestDTO> res1 = deaneryService.searchRequestsByFacultyAndOrPriority(Faculty.INGENIERIA_DE_SISTEMAS, null);

        assertEquals(1, res1.size());
    }

    @Test
    void shouldGetRequestsByFacultyAndPriority() {
        Faculty faculty = Faculty.INGENIERIA_DE_SISTEMAS;
        int priority = 2;

        ChangeRequest r = new ChangeRequest();
        r.setId(UUID.randomUUID());
        r.setFaculty(faculty);

        when(changeRequestRepository.findByFacultyAndPriorityOrderByPriorityAsc(faculty, priority)).thenReturn(List.of(r));
        when(changeRequestMapper.toDTO(r)).thenReturn(new ChangeRequestDTO() {{ setId(r.getId()); }});
        List<ChangeRequestDTO> out = deaneryService.getRequestsByFacultyAndPriority(faculty, priority);

        assertEquals(1, out.size());
        verify(changeRequestRepository).findByFacultyAndPriorityOrderByPriorityAsc(faculty, priority);
    }

    @Test
    void shouldGetAllRequestsOrderedAndByPriority() {
        ChangeRequest a = new ChangeRequest(); a.setId(UUID.randomUUID()); a.setPriority(1);
        ChangeRequest b = new ChangeRequest(); b.setId(UUID.randomUUID()); b.setPriority(2);

        when(changeRequestRepository.findAllByOrderByPriorityAsc()).thenReturn(List.of(a, b));
        when(changeRequestRepository.findByPriorityOrderByPriorityAsc(2)).thenReturn(List.of(b));
        when(changeRequestMapper.toDTO(a)).thenReturn(new ChangeRequestDTO() {{ setId(a.getId()); }});
        when(changeRequestMapper.toDTO(b)).thenReturn(new ChangeRequestDTO() {{ setId(b.getId()); }});
        List<ChangeRequestDTO> allOrdered = deaneryService.getAllRequestsOrderedByPriority();
        List<ChangeRequestDTO> byPriority = deaneryService.getAllRequestsByPriority(2);

        assertEquals(2, allOrdered.size());
        assertEquals(1, byPriority.size());
    }

    @Test
    void shouldSearchRequestsVariousBranches() {
        Faculty f = Faculty.INGENIERIA_DE_SISTEMAS;

        when(changeRequestRepository.findByFacultyAndPriorityOrderByPriorityAsc(eq(f), eq(5))).thenReturn(List.of(new ChangeRequest()));
        when(changeRequestMapper.toDTO(any(ChangeRequest.class))).thenReturn(new ChangeRequestDTO());
        List<ChangeRequestDTO> r1 = deaneryService.searchRequestsByFacultyAndOrPriority(f, 5);

        assertNotNull(r1);
        assertEquals(1, r1.size(), "Se esperaba 1 resultado para faculty + priority");

        when(changeRequestRepository.findByFacultyOrderByPriorityAsc(eq(f))).thenReturn(List.of(new ChangeRequest()));
        List<ChangeRequestDTO> r2 = deaneryService.searchRequestsByFacultyAndOrPriority(f, null);

        assertNotNull(r2);
        assertEquals(1, r2.size(), "Se esperaba 1 resultado para faculty only");

        when(changeRequestRepository.findByPriorityOrderByPriorityAsc(eq(7))).thenReturn(List.of(new ChangeRequest()));
        List<ChangeRequestDTO> r3 = deaneryService.searchRequestsByFacultyAndOrPriority(null, 7);

        assertNotNull(r3);
        assertEquals(1, r3.size(), "Se esperaba 1 resultado para priority only");

        when(changeRequestRepository.findAllByOrderByPriorityAsc()).thenReturn(List.of(new ChangeRequest()));
        List<ChangeRequestDTO> r4 = deaneryService.searchRequestsByFacultyAndOrPriority(null, null);

        assertNotNull(r4);
        assertEquals(1, r4.size(), "Se esperaba 1 resultado para sin filtros");
    }


    @Test
    void shouldProcessApprovedRequest() {
        UUID reqId = UUID.randomUUID();

        ChangeRequest req = new ChangeRequest();
        req.setId(reqId);
        req.setStudentId(1000100282);
        req.setCurrentGroup("DOPO-1");
        req.setTargetGroup("DOPO-2");
        req.setCurrentSubject("POOB");
        req.setTargetSubject("DOPO");

        Group current = new Group();
        current.setGroupId("DOPO-1");
        current.setWaitlist(new ArrayList<>(List.of(1000100282)));

        Group target = new Group();
        target.setGroupId("DOPO-2");
        target.setCurrentCapacity(2);
        target.setMaximumCapacity(30);
        target.setSubjectId("DOPO");
        
        ScheduleEntry targetSE = new ScheduleEntry("DOPO", "DOPO-2", "LUN", "08:00", 1, "Edicifio C C4-202", "10:00", AcademicTrafficLight.GREEN);
        target.setSchedule(new ArrayList<>(List.of(targetSE)));

        Student student = new Student();
        student.setId(1000100282);
        student.setSchedule(new ArrayList<>());
        student.setEnrolledSubjects(new ArrayList<>());

        when(message.findGroupOrThrow("DOPO-1")).thenReturn(current);
        when(message.findGroupOrThrow("DOPO-2")).thenReturn(target);
        when(message.findStudentOrThrow(1000100282)).thenReturn(student);
        when(groupRepository.save(any(Group.class))).thenAnswer(i -> i.getArgument(0));
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));
        deaneryService.processApprovedRequest(req, new RequestDecisionDTO() {{ setStatus(RequestStatus.APPROVED); }}, 99);

        boolean found = student.getSchedule().stream().anyMatch(se -> "DOPO".equals(se.getSubject()) && "DOPO-2".equals(se.getGroup()));

        assertFalse(current.getWaitlist().contains(1000100282));
        assertEquals(3, target.getCurrentCapacity());
        assertTrue(student.getEnrolledSubjects().contains("DOPO"));
        assertTrue(found);
    }

    @Test
    void shouldDeleteRequestAsDeaneryRemoveGruopWaitList() {
        int deaneryId = 1000000451;
        UUID reqId = UUID.randomUUID();

        ChangeRequest req = new ChangeRequest();
        req.setId(reqId);
        req.setStudentId(1000100667);
        req.setTargetGroup("FUEC-1");

        when(message.findDeaneryOrThrow(deaneryId)).thenReturn(new Deanery() {{ setId(deaneryId); setFaculty(Faculty.ECONOMIA); }});
        when(message.findChangeRequestOrThrow(reqId)).thenReturn(req);

        Group g = new Group();
        g.setGroupId("FUEC-1");
        g.setWaitlist(new ArrayList<>(List.of(1000100516, 1000100575, 1000100667)));
        Optional<Group> opt = Optional.of(g);

        when(groupRepository.findByGroupId("FUEC-1")).thenReturn(opt);
        deaneryService.deleteRequestAsDeanery(deaneryId, reqId);

        assertFalse(g.getWaitlist().contains(1000100667));
    }

    @Test
    void shouldProcessApprovedRequestRemoveExistingSchedule() {
        UUID reqId = UUID.randomUUID();

        ChangeRequest req = new ChangeRequest();
        req.setId(reqId);
        req.setStudentId(1000100282);
        req.setCurrentGroup("TPYC-1");
        req.setTargetGroup("TPYC-3");
        req.setCurrentSubject("TPRO");
        req.setTargetSubject("TPYC");

        Group current = new Group();
        current.setGroupId("TPYC-1");
        current.setWaitlist(new ArrayList<>(List.of(1000100282)));

        Group target = new Group();
        target.setGroupId("TPYC-3");
        target.setCurrentCapacity(2);
        target.setMaximumCapacity(30);
        target.setSubjectId("TPYC");

        ScheduleEntry targetSE = new ScheduleEntry("TPYC", "TPYC-3", "LUNES", "08:00", 1, "Edicifio C C3-205", "10:00", AcademicTrafficLight.GREEN);
        target.setSchedule(new ArrayList<>(List.of(targetSE)));

        Student student = new Student();
        student.setId(1000100282);

        ScheduleEntry existingCurrent = new ScheduleEntry("TPRO", "TPYC-1", "MARTES", "10:00", 1, "D -D-202", "12:00", AcademicTrafficLight.GREEN);
        student.setSchedule(new ArrayList<>(List.of(existingCurrent)));
        student.setEnrolledSubjects(new ArrayList<>(List.of("TPRO")));
        student.setApprovedSubjects(new ArrayList<>());

        when(message.findGroupOrThrow("TPYC-1")).thenReturn(current);
        when(message.findGroupOrThrow("TPYC-3")).thenReturn(target);
        when(message.findStudentOrThrow(1000100282)).thenReturn(student);
        when(groupRepository.save(any(Group.class))).thenAnswer(i -> i.getArgument(0));
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));
        deaneryService.processApprovedRequest(req, new RequestDecisionDTO() {{ setStatus(RequestStatus.APPROVED); }}, 1000000451);

        boolean foundNew = student.getSchedule().stream().anyMatch(se -> "TPYC".equals(se.getSubject()) && "TPYC-3".equals(se.getGroup()));

        assertFalse(current.getWaitlist().contains(1000100282));
        assertEquals(3, target.getCurrentCapacity());
        assertFalse(student.getEnrolledSubjects().contains("TPRO"));
        assertTrue(student.getEnrolledSubjects().contains("TPYC"));
        assertTrue(foundNew);
    }

    @Test
    void shouldNotAddDuplicateScheduleEntryHasSameEntry() {
        UUID reqId = UUID.randomUUID();

        ChangeRequest req = new ChangeRequest();
        req.setId(reqId);
        req.setStudentId(1000100279);
        req.setCurrentGroup("DOSW-1");
        req.setTargetGroup("DOSW-2");
        req.setCurrentSubject("CVDS");
        req.setTargetSubject("DOSW");

        Group current = new Group();
        current.setGroupId("DOSW-1");
        current.setWaitlist(new ArrayList<>());

        Group target = new Group();
        target.setGroupId("DOSW-2");
        target.setCurrentCapacity(5);
        target.setMaximumCapacity(30);
        target.setSubjectId("DOSW");

        ScheduleEntry targetSE = new ScheduleEntry("DOSW", "DOSW-2", "MARTES", "14:00", 5, "Edificio D D-305", "16:00", AcademicTrafficLight.GREEN);
        target.setSchedule(new ArrayList<>(List.of(targetSE)));

        Student student = new Student();
        student.setId(1000100279);

        ScheduleEntry already = new ScheduleEntry("DOSW", "DOSW-2", "MARTES", "14:00", 5, "Edificio H H-301", "16:00", AcademicTrafficLight.GREEN);
        student.setSchedule(new ArrayList<>(List.of(already)));
        student.setEnrolledSubjects(new ArrayList<>());

        when(message.findGroupOrThrow("DOSW-1")).thenReturn(current);
        when(message.findGroupOrThrow("DOSW-2")).thenReturn(target);
        when(message.findStudentOrThrow(1000100279)).thenReturn(student);
        when(groupRepository.save(any(Group.class))).thenAnswer(i -> i.getArgument(0));
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));
        deaneryService.processApprovedRequest(req, new RequestDecisionDTO() {{ setStatus(RequestStatus.APPROVED); }}, 1000000451);

        long count = student.getSchedule().stream().filter(se -> "DOSW".equals(se.getSubject()) && "DOSW-2".equals(se.getGroup())).count();

        assertEquals(1, count);
    }

    @Test
    void shouldAddBasicScheduleEntry_whenTargetGroupHasNoSchedule_andGroupSubjectBlank() {
        UUID reqId = UUID.randomUUID();

        ChangeRequest req = new ChangeRequest();
        req.setId(reqId);
        req.setStudentId(1000100667);
        req.setCurrentGroup("ODSC-4");
        req.setTargetGroup("ODSC-3");
        req.setCurrentSubject("ODSC");
        req.setTargetSubject("ODSC");

        Group current = new Group();
        current.setGroupId("ODSC-4");
        current.setWaitlist(new ArrayList<>());

        Group target = new Group();
        target.setGroupId("ODSC-3");
        target.setCurrentCapacity(1);
        target.setMaximumCapacity(40);
        target.setSubjectId("");
        target.setSchedule(null);

        Student student = new Student();
        student.setId(1000100667);
        student.setSchedule(new ArrayList<>());
        student.setEnrolledSubjects(new ArrayList<>());

        when(message.findGroupOrThrow("ODSC-4")).thenReturn(current);
        when(message.findGroupOrThrow("ODSC-3")).thenReturn(target);
        when(message.findStudentOrThrow(1000100667)).thenReturn(student);
        when(groupRepository.save(any(Group.class))).thenAnswer(i -> i.getArgument(0));
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));
        deaneryService.processApprovedRequest(req, new RequestDecisionDTO() {{ setStatus(RequestStatus.APPROVED); }}, 1000000451);

        boolean basicFound = student.getSchedule().stream().anyMatch(se -> "ODSC".equals(se.getSubject()) && "ODSC-3".equals(se.getGroup()));

        assertTrue(basicFound);
        assertTrue(student.getEnrolledSubjects().contains("ODSC"));
    }

    @Test
    void shouldRespondAdditionalInfoByDeanery() {
        int deaneryId = 1000000451;
        UUID reqId = UUID.randomUUID();

        Deanery dean = new Deanery(); dean.setId(deaneryId); dean.setFaculty(Faculty.INGENIERIA_DE_SISTEMAS);
        ChangeRequest req = new ChangeRequest(); req.setId(reqId);
        req.setStatus(RequestStatus.PENDING);
        req.setFaculty(Faculty.INGENIERIA_DE_SISTEMAS);

        when(message.findDeaneryOrThrow(deaneryId)).thenReturn(dean);
        when(message.findChangeRequestOrThrow(reqId)).thenReturn(req);
        when(changeRequestMapper.toDTO(req)).thenReturn(new ChangeRequestDTO() {{ setId(reqId); }});

        RequestDecisionDTO decision = new RequestDecisionDTO();
        decision.setRequestAdditionalInfo(true);
        decision.setAdditionalInfoRequestMessage("Adjunta certificado");

        RequestDatesDTO dates = new RequestDatesDTO();
        dates.setStartDate(LocalDateTime.now().minusDays(1));
        dates.setEndDate(LocalDateTime.now().plusDays(1));

        ChangeRequestDTO out = deaneryService.respondRequestByDeanery(deaneryId, reqId, decision, dates);

        assertNotNull(out);
        assertEquals(reqId, out.getId());
        assertEquals(RequestStatus.REQUEST_ADDITIONAL_INFO, req.getStatus());
    }


    @Test
    void shouldProcessApprovedRequestAddsBasicScheduleWhenNoGroupSchedule() {
        UUID reqId = UUID.randomUUID();
        int studentId = 1000100575;

        ChangeRequest req = new ChangeRequest();
        req.setId(reqId);
        req.setStudentId(studentId);
        req.setCurrentSubject("DOSW");
        req.setTargetSubject("ODSC");
        req.setCurrentGroup("DOSW-1");
        req.setTargetGroup("G-2");
        req.setStatus(RequestStatus.PENDING);

        Group current = new Group();
        current.setGroupId("DOSW-1");
        current.setWaitlist(new ArrayList<>(List.of(studentId)));

        Group target = new Group();
        target.setGroupId("G-2");
        target.setCurrentCapacity(1);
        target.setMaximumCapacity(30);
        target.setSubjectId("ODSC");
        target.setSchedule(new ArrayList<>());

        Student student = new Student();
        student.setId(studentId);
        student.setSchedule(new ArrayList<>());
        student.setEnrolledSubjects(new ArrayList<>());

        when(message.findGroupOrThrow("DOSW-1")).thenReturn(current);
        when(message.findGroupOrThrow("G-2")).thenReturn(target);
        when(message.findStudentOrThrow(studentId)).thenReturn(student);
        when(groupRepository.save(any(Group.class))).thenAnswer(i -> i.getArgument(0));
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));
        deaneryService.processApprovedRequest(req, new RequestDecisionDTO() {{ setStatus(RequestStatus.APPROVED); }}, 999);

        boolean found = student.getSchedule().stream().anyMatch(se -> "ODSC".equals(se.getSubject()) && "G-2".equals(se.getGroup()));

        assertEquals(2, target.getCurrentCapacity());
        assertFalse(current.getWaitlist().contains(studentId));
        assertTrue(student.getEnrolledSubjects().contains("ODSC"));
        assertTrue(found);


    }


    @Test
    void shouldUpdateRequestAsDeaneryWhenStatusNull() {
        int deaneryId = 1000000143;
        UUID reqId = UUID.randomUUID();

        Deanery dean = new Deanery(); dean.setId(deaneryId); dean.setFaculty(Faculty.ECONOMIA);
        ChangeRequest req = new ChangeRequest(); req.setId(reqId);
        req.setStatus(RequestStatus.PENDING);
        req.setFaculty(Faculty.ECONOMIA);

        when(message.findDeaneryOrThrow(deaneryId)).thenReturn(dean);
        when(message.findChangeRequestOrThrow(reqId)).thenReturn(req);
        when(changeRequestMapper.toDTO(req)).thenReturn(new ChangeRequestDTO() {{ setId(reqId); }});
        when(changeRequestRepository.save(any(ChangeRequest.class))).thenAnswer(i -> i.getArgument(0));

        RequestDecisionDTO decision = new RequestDecisionDTO();
        ChangeRequestDTO out = deaneryService.updateRequestAsDeanery(deaneryId, reqId, decision, null);

        assertNotNull(out);
        assertEquals(reqId, out.getId());
    }

    @Test
    void shouldReturnNoteWhenStatusAndObservationArePresent() throws Exception {
        RequestDecisionDTO decision = new RequestDecisionDTO();
        decision.setStatus(RequestStatus.APPROVED);
        decision.setObservations("Todo correcto");

        Method method = DeaneryServiceImpl.class.getDeclaredMethod("decisionToNote", RequestDecisionDTO.class);
        method.setAccessible(true);
        String result = (String) method.invoke(deaneryService, decision);

        assertEquals("Decision: APPROVED. Todo correcto", result);
    }

    @Test
    void shouldReturnNoteWhenStatusAndObservationAreNull() throws Exception {
        RequestDecisionDTO decision = new RequestDecisionDTO();
        decision.setStatus(null);
        decision.setObservations(null);

        Method method = DeaneryServiceImpl.class.getDeclaredMethod("decisionToNote", RequestDecisionDTO.class);
        method.setAccessible(true);
        String result = (String) method.invoke(deaneryService, decision);

        assertEquals("Decision: UNKNOWN. ", result);
    }


}