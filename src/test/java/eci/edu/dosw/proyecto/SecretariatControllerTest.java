package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.controller.SecretariatController;
import eci.edu.dosw.proyecto.dtos.*;
import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.models.ChangeRequestHistory;
import eci.edu.dosw.proyecto.services.GroupService;
import eci.edu.dosw.proyecto.services.HistoryService;
import eci.edu.dosw.proyecto.services.SecretariatService;
import eci.edu.dosw.proyecto.services.StudentService;
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
class SecretariatControllerTest {

    @Mock
    private SecretariatService secretariatService;

    @Mock
    private HistoryService historyService;

    @Mock
    private GroupService groupService;

    @Mock
    private StudentService studentService;

    @InjectMocks
    private SecretariatController controller;

    @Test
    void shouldCreateSecretariat() {
        SecretariatDTO in = new SecretariatDTO();
        in.setName("Patricia Salazar Perdomo");
        in.setEmail("patricia.salazar@escuelaing.edu.co");

        SecretariatDTO out = new SecretariatDTO();
        out.setId(1);
        out.setName("Patricia Salazar Perdomo");
        out.setEmail("patricia.salazar@escuelaing.edu.co");

        when(secretariatService.createSecretariat(in)).thenReturn(out);
        SecretariatDTO res = controller.createSecretariat(in);

        assertNotNull(res);
        assertEquals(1, res.getId());
        assertEquals("Patricia Salazar Perdomo", res.getName());
    }

    @Test
    void shouldGetSecretariat() {
        SecretariatDTO dto = new SecretariatDTO();
        dto.setId(5);
        dto.setName("Patricia Salazar Perdomo");

        when(secretariatService.getSecretariatById(5)).thenReturn(dto);
        SecretariatDTO res = controller.getSecretariat(5);

        assertEquals("Patricia Salazar Perdomo", res.getName());
    }

    @Test
    void shouldGetAllSecretariats() {
        SecretariatDTO a = new SecretariatDTO(); a.setId(1);
        SecretariatDTO b = new SecretariatDTO(); b.setId(2);

        when(secretariatService.getAllSecretariats()).thenReturn(List.of(a, b));
        List<SecretariatDTO> res = controller.getAllSecretariats();

        assertEquals(2, res.size());
    }

    @Test
    void shouldUpdateSecretariat() {
        SecretariatDTO in = new SecretariatDTO();
        in.setName("Updated");

        SecretariatDTO out = new SecretariatDTO();
        out.setId(7);
        
        out.setName("Updated");
        when(secretariatService.updateSecretariat(1000000398, in)).thenReturn(out);

        SecretariatDTO res = controller.updateSecretariat(1000000398, in);
        assertEquals("Updated", res.getName());
    }

    @Test
    void shouldDeleteSecretariat() {
        doNothing().when(secretariatService).deleteSecretariat(1000000398);
        controller.deleteSecretariat(1000000398);

        assertTrue(true);
    }

    @Test
    void shouldUpdateRequestDates() {
        RequestDatesDTO dto = new RequestDatesDTO(LocalDateTime.of(2025,1,1,9,0), LocalDateTime.of(2025,1,10,18,0));

        doNothing().when(secretariatService).updateRequestDates(1000000398, dto.getRequestStartDate(), dto.getRequestEndDate());
        controller.updateRequestDates(1000000398, dto);

        verify(secretariatService).updateRequestDates(1000000398, dto.getRequestStartDate(), dto.getRequestEndDate());
    }

    @Test
    void shouldRespondWithParams() {
        UUID reqId = UUID.randomUUID();

        RequestDecisionDTO decision = new RequestDecisionDTO();
        decision.setStatus(RequestStatus.REJECTED);
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        ChangeRequestDTO out = new ChangeRequestDTO();
        out.setId(reqId);

        when(secretariatService.respondRequestBySecretariat(eq(reqId), eq(decision), any())).thenReturn(out);
        ChangeRequestDTO res = controller.respondRequestBySecretariat(reqId, decision, start, end);

        assertEquals(reqId, res.getId());
    }

    @Test
    void shouldRespondWithBody() {
        UUID reqId = UUID.randomUUID();

        RequestDecisionDTO decision = new RequestDecisionDTO();
        decision.setStatus(RequestStatus.APPROVED);

        RequestDatesDTO dates = new RequestDatesDTO(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        RespondRequestInfo body = new RespondRequestInfo();
        body.setDecision(decision);
        body.setDates(dates);

        ChangeRequestDTO out = new ChangeRequestDTO();
        out.setId(reqId);

        when(secretariatService.respondRequestBySecretariat(eq(reqId), eq(decision), eq(dates))).thenReturn(out);
        ChangeRequestDTO res = controller.respondRequestBySecretariat(reqId, body);

        assertEquals(reqId, res.getId());
    }

    @Test
    void shouldGetRequestsByFacultyAndStatus() {
        Faculty faculty = Faculty.INGENIERIA_DE_SISTEMAS;
        RequestStatus status = RequestStatus.PENDING;

        ChangeRequestDTO dto = new ChangeRequestDTO();

        when(secretariatService.getRequestsByFacultyAndStatus(faculty, status)).thenReturn(List.of(dto));
        List<ChangeRequestDTO> res = controller.getRequestsByFacultyAndStatus(faculty, status);

        assertEquals(1, res.size());
    }

    @Test
    void shouldGetRequestsOrderedAndSearchAndByPriority() {
        Faculty faculty = Faculty.ECONOMIA;
        ChangeRequestDTO dto = new ChangeRequestDTO();

        when(secretariatService.getRequestsByFacultyOrderedByPriority(faculty)).thenReturn(List.of(dto));
        when(secretariatService.searchRequestsByFacultyAndOrPriority(faculty, null)).thenReturn(List.of(dto));
        when(secretariatService.getAllRequestsOrderedByPriority()).thenReturn(List.of(dto));
        when(secretariatService.getAllRequestsByPriority(2)).thenReturn(List.of(dto));

        assertEquals(1, controller.getRequestsByFacultyOrderedByPriority(faculty).size());
        assertEquals(1, controller.searchRequests(faculty, null).size());
        assertEquals(1, controller.getAllOrderedByPriority().size());
        assertEquals(1, controller.getAllByPriority(2).size());
    }

    @Test
    void shouldGetHistoryAndGroupAndStudent() {
        UUID reqId = UUID.randomUUID();
        ChangeRequestHistory h = new ChangeRequestHistory();

        when(historyService.getHistory(reqId)).thenReturn(List.of(h));

        assertEquals(1, controller.getRequestHistory(reqId).size());

        when(groupService.getMaxCapacity("EGI4-4")).thenReturn(40);
        when(groupService.getCurrentCapacity("EGI4-4")).thenReturn(10);
        when(groupService.getWaitlist("EGI4-4")).thenReturn(List.of(1001, 1002));

        assertEquals(40, controller.getMaxCapacity("EGI4-4"));
        assertEquals(10, controller.getCurrentCapacity("EGI4-4"));
        assertEquals(List.of(1001, 1002), controller.getWaitingList("EGI4-4"));

        StudentDTO stu = new StudentDTO(); stu.setId(1000100575); stu.setName("Alumno");

        when(studentService.getStudentById(1000100575)).thenReturn(stu);
        StudentDTO res = controller.getStudentInfo(1000100575);

        assertEquals("Alumno", res.getName());
    }

    @Test
    void shouldUpdateAndDeleteRequestAsSecretariat() {
        UUID reqId = UUID.randomUUID();

        RequestDecisionDTO decision = new RequestDecisionDTO();
        decision.setStatus(RequestStatus.APPROVED);
        RequestDatesDTO dates = new RequestDatesDTO(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        ChangeRequestDTO out = new ChangeRequestDTO(); out.setId(reqId);

        when(secretariatService.updateRequestAsSecretariat(reqId, decision, dates)).thenReturn(out);
        ChangeRequestDTO res = controller.updateRequestAsSecretariat(reqId, decision, dates.getRequestStartDate(), dates.getRequestEndDate());
        controller.deleteRequestAsSecretariat(reqId);
        
        assertEquals(reqId, res.getId());

    }
}
