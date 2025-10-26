package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.controller.DeaneryController;
import eci.edu.dosw.proyecto.dtos.*;
import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.models.ChangeRequestHistory;
import eci.edu.dosw.proyecto.services.*;
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
class DeaneryControllerTest {

    @Mock
    private DeaneryService deaneryService;

    @Mock
    private HistoryService historyService;

    @Mock
    private GroupService groupService;

    @Mock
    private StudentService studentService;

    @Mock
    private ChangeRequestService changeRequestService;

    @InjectMocks
    private DeaneryController controller;

    @Test
    void ShouldCreateDeanery() {
        DeaneryDTO dto = new DeaneryDTO();
        dto.setId(1);
        dto.setName("Decano Ingeniería");

        when(deaneryService.createDeanery(dto)).thenReturn(dto);

        DeaneryDTO response = controller.createDeanery(dto);

        assertEquals(dto, response);
    }

    @Test
    void ShouldGetDeaneryById() {
        DeaneryDTO dto = new DeaneryDTO();
        dto.setId(5);
        dto.setName("Decano de Economía");

        when(deaneryService.getDeaneryById(5)).thenReturn(dto);

        DeaneryDTO response = controller.getDeaneryById(5);

        assertEquals(dto, response);
    }

    @Test
    void ShouldGetDeaneryByFaculty() {
        Faculty faculty = Faculty.INGENIERIA_DE_SISTEMAS;
        DeaneryDTO dto = new DeaneryDTO();
        dto.setId(2);
        dto.setName("Decano Ing");

        when(deaneryService.getDeaneryByFaculty(faculty)).thenReturn(dto);

        DeaneryDTO result = controller.getDeaneryByFaculty(faculty);

        assertNotNull(result);
        assertEquals(dto, result);
    }

    @Test
    void ShouldGetAllDeaneries() {
        DeaneryDTO d1 = new DeaneryDTO();
        d1.setId(1);
        DeaneryDTO d2 = new DeaneryDTO();
        d2.setId(2);

        when(deaneryService.getAllDeaneries()).thenReturn(List.of(d1, d2));

        List<DeaneryDTO> response = controller.getAllDeaneries();
        assertEquals(2, response.size());
    }

    @Test
    void ShouldUpdateDeanery() {
        DeaneryDTO dto = new DeaneryDTO();
        dto.setId(3);
        dto.setName("Nuevo nombre");

        when(deaneryService.updateDeanery(3, dto)).thenReturn(dto);

        DeaneryDTO response = controller.updateDeanery(3, dto);
        assertEquals("Nuevo nombre", response.getName());
    }

    @Test
    void ShouldDeleteDeanery() {
        doNothing().when(deaneryService).deleteDeanery(7);

        controller.deleteDeanery(7);
        verify(deaneryService).deleteDeanery(7);
    }

    @Test
    void ShouldRespondRequestByDeanery() {
        int deaneryId = 10;
        UUID requestId = UUID.randomUUID();
        RequestDecisionDTO decision = new RequestDecisionDTO();
        RequestDatesDTO dates = new RequestDatesDTO();
        RespondRequestInfo body = new RespondRequestInfo(decision, dates);
        ChangeRequestDTO returned = new ChangeRequestDTO();
        when(deaneryService.respondRequestByDeanery(deaneryId, requestId, decision, dates)).thenReturn(returned);
        ChangeRequestDTO response = controller.respondRequestByDeanery(deaneryId, requestId, body);

        assertSame(returned, response);
    }


    @Test
    void ShouldGetRequestsByFacultyAndStatus() {
        Faculty faculty = Faculty.INGENIERIA_DE_SISTEMAS;
        RequestStatus status = RequestStatus.PENDING;
        ChangeRequestDTO dto = new ChangeRequestDTO();

        when(deaneryService.getRequestsByFacultyAndStatus(faculty, status)).thenReturn(List.of(dto));

        List<ChangeRequestDTO> response = controller.getRequestsByFacultyAndStatus(faculty, status);

        assertEquals(1, response.size());
    }

    @Test
    void shouldGetRequestHistory() {
        UUID requestId = UUID.randomUUID();
        ChangeRequestHistory h1 = new ChangeRequestHistory();
        ChangeRequestHistory h2 = new ChangeRequestHistory();
        when(historyService.getHistory(requestId)).thenReturn(List.of(h1, h2));
        List<ChangeRequestHistory> result = controller.getRequestHistory(requestId);

        assertEquals(2, result.size());
    }

    @Test
    void shouldGetGroupCapacitiesAndWaitlist() {
        String groupId = "DOSW-1";
        when(groupService.getMaxCapacity(groupId)).thenReturn(40);
        when(groupService.getCurrentCapacity(groupId)).thenReturn(12);
        when(groupService.getWaitlist(groupId)).thenReturn(List.of(1001, 1002));

        assertEquals(40, controller.getMaxCapacity(groupId));
        assertEquals(12, controller.getCurrentCapacity(groupId));
        assertEquals(List.of(1001, 1002), controller.getWaitingList(groupId));
    }


    @Test
    void shouldGetStudentInfo() {
        int studentId = 1000100279;
        StudentDTO dto = new StudentDTO();
        dto.setId(studentId);
        dto.setName("Oscar Sanchez Porras");
        when(studentService.getStudentById(studentId)).thenReturn(dto);
        StudentDTO res = controller.getStudentInfo(studentId);

        assertEquals("Oscar Sanchez Porras", res.getName());
    }

    @Test
    void shouldUpdateRequestAsDeanery() {
        int deaneryId = 1000000451;
        UUID reqId = UUID.randomUUID();
        RequestDecisionDTO decision = new RequestDecisionDTO();
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        RequestDatesDTO expectedDates = new RequestDatesDTO(start, end);
        ChangeRequestDTO returned = new ChangeRequestDTO();
        when(deaneryService.updateRequestAsDeanery(deaneryId, reqId, decision, expectedDates)).thenReturn(returned);
        ChangeRequestDTO res = controller.updateRequestAsDeanery(deaneryId, reqId, decision, start, end);

        assertEquals(returned, res);
    }

    @Test
    void shouldDeleteRequestAsDeanery() {
        int deaneryId = 1000000143;
        UUID reqId = UUID.randomUUID();

        controller.deleteRequestAsDeanery(deaneryId, reqId);
        verify(deaneryService, times(1)).deleteRequestAsDeanery(deaneryId, reqId);
    }

    @Test
    void shouldRespondRequestByDeaneryWithBody() {
        int deaneryId = 1000000451;
        UUID reqId = UUID.randomUUID();

        RequestDecisionDTO decision = new RequestDecisionDTO();
        RequestDatesDTO dates = new RequestDatesDTO();
        RespondRequestInfo body = new RespondRequestInfo();
        body.setDecision(decision);
        body.setDates(dates);

        ChangeRequestDTO returned = new ChangeRequestDTO();

        when(deaneryService.respondRequestByDeanery(deaneryId, reqId, decision, dates)).thenReturn(returned);
        ChangeRequestDTO response = controller.respondRequestByDeaneryInfo(deaneryId, reqId, body);

        assertEquals(returned, response);
    }

    @Test
    void shouldGetRequestsByFacultyAndPriority() {
        Faculty faculty = Faculty.INGENIERIA_DE_SISTEMAS;
        int priority = 1;

        ChangeRequestDTO dto = new ChangeRequestDTO();

        when(deaneryService.getRequestsByFacultyAndPriority(faculty, priority)).thenReturn(List.of(dto));
        List<ChangeRequestDTO> response = controller.getRequestsByFacultyAndPriority(faculty, priority);

        assertEquals(1, response.size());
    }

    @Test
    void shouldApproveExceptional() {
        int deaneryId = 1000000451;
        UUID reqId = UUID.randomUUID();

        ChangeRequestDTO dto = new ChangeRequestDTO();

        when(changeRequestService.approveExceptionalRequest(deaneryId, reqId, true, "ok"))
                .thenReturn(dto);

        ChangeRequestDTO res = controller.approveExceptional(deaneryId, reqId, true, "ok");

        assertNotNull(res);
        assertEquals(dto, res);
    }

    @Test
    void shouldSearchRequestsByFacultyAndPriority() {
        Faculty faculty = Faculty.INGENIERIA_DE_SISTEMAS;
        Integer priority = 2;

        ChangeRequestDTO dto = new ChangeRequestDTO();

        when(deaneryService.searchRequestsByFacultyAndOrPriority(faculty, priority)).thenReturn(List.of(dto));

        List<ChangeRequestDTO> res = controller.searchRequests(faculty, priority);

        assertNotNull(res);
        assertEquals(1, res.size());
    }

    @Test
    void shouldGetExceptionalRequestsForStudentByDeanery() {
        int deaneryId = 1000000143;
        Integer studentId = 1000100575;

        ChangeRequestDTO dto = new ChangeRequestDTO();
        when(changeRequestService.getExceptionalRequestsByStudentForDeanery(deaneryId, studentId))
                .thenReturn(List.of(dto));

        List<ChangeRequestDTO> res = controller.getExceptionalRequestsForStudentByDeanery(deaneryId, studentId);

        assertNotNull(res);
        assertEquals(1, res.size());
    }

    @Test
    void shouldGetRequestsByFacultyOrderedByPriority() {
        Faculty faculty = Faculty.INGENIERIA_DE_SISTEMAS;
        ChangeRequestDTO dto = new ChangeRequestDTO();

        when(deaneryService.getRequestsByFacultyOrderedByPriority(faculty)).thenReturn(List.of(dto));

        List<ChangeRequestDTO> res = controller.getRequestsByFacultyOrderedByPriority(faculty);

        assertNotNull(res);
        assertEquals(1, res.size());
    }

    @Test
    void shouldGetAllByPriority() {
        int priority = 3;
        ChangeRequestDTO dto = new ChangeRequestDTO();

        when(deaneryService.getAllRequestsByPriority(priority)).thenReturn(List.of(dto));

        List<ChangeRequestDTO> res = controller.getAllByPriority(priority);

        assertNotNull(res);
        assertEquals(1, res.size());
    }

    @Test
    void shouldGetExceptionalByDeanery() {
        int deaneryId = 1000000143;
        ChangeRequestDTO dto = new ChangeRequestDTO();

        when(changeRequestService.getExceptionalRequestsByDeanery(deaneryId)).thenReturn(List.of(dto));

        List<ChangeRequestDTO> res = controller.getExceptionalByDeanery(deaneryId);

        assertNotNull(res);
        assertEquals(1, res.size());
    }

    @Test
    void shouldGetAllOrderedByPriority() {
        ChangeRequestDTO dto = new ChangeRequestDTO();

        when(deaneryService.getAllRequestsOrderedByPriority()).thenReturn(List.of(dto));

        List<ChangeRequestDTO> res = controller.getAllOrderedByPriority();

        assertNotNull(res);
        assertEquals(1, res.size());
    }

    @Test
    void shouldGetAllExceptionalRequests() {
        ChangeRequestDTO dto = new ChangeRequestDTO();

        when(changeRequestService.getAllExceptionalRequests()).thenReturn(List.of(dto));

        List<ChangeRequestDTO> res = controller.getAllExceptionalRequests();

        assertNotNull(res);
        assertEquals(1, res.size());
    }

}