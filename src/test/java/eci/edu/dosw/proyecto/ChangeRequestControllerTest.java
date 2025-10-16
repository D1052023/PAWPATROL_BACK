package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.controller.ChangeRequestController;
import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.StudentDTO;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.models.ChangeRequestHistory;
import eci.edu.dosw.proyecto.services.ChangeRequestService;
import eci.edu.dosw.proyecto.services.HistoryService;
import eci.edu.dosw.proyecto.services.StudentService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;


@ExtendWith(MockitoExtension.class)
class ChangeRequestControllerTest {

    @Mock
    private ChangeRequestService changeRequestService;

    @Mock
    private StudentService studentService;

    @Mock
    private HistoryService historyService;

    @InjectMocks
    private ChangeRequestController controller;

    @Test
    void shouldCreateRequest() {
        Integer studentId = 1000100575;
        ChangeRequestDTO requestDTO = new ChangeRequestDTO();
        requestDTO.setId(UUID.randomUUID());
        when(changeRequestService.createChangeRequest(studentId, requestDTO)).thenReturn(requestDTO);
        ChangeRequestDTO res = controller.createRequest(studentId, requestDTO);

        assertNotNull(res);
        assertEquals(requestDTO.getId(), res.getId());
    }

    @Test
    void shouldGetRequestsByStudent() {
        Integer studentId = 1000100516;
        ChangeRequestDTO c1 = new ChangeRequestDTO(); c1.setId(UUID.randomUUID());
        ChangeRequestDTO c2 = new ChangeRequestDTO(); c2.setId(UUID.randomUUID());
        when(changeRequestService.getAllRequestsByStudent(studentId)).thenReturn(List.of(c1, c2));
        List<ChangeRequestDTO> res = controller.getRequestsByStudent(studentId);

        assertEquals(2, res.size());
    }

    @Test
    void shouldGetRequestById() {
        Integer studentId = 1000100282;
        UUID requestId = UUID.randomUUID();
        ChangeRequestDTO dto = new ChangeRequestDTO();
        dto.setId(requestId);
        when(changeRequestService.getRequestById(studentId, requestId)).thenReturn(dto);
        ChangeRequestDTO res = controller.getRequestById(studentId, requestId);

        assertNotNull(res);
        assertEquals(requestId, res.getId());
    }

    @Test
    void shouldGetCurrentScheduleWhenSemesterNull() {
        Integer id = 1000100282;
        StudentDTO student = new StudentDTO();
        student.setId(id);
        student.setSemester(3);
        StudentDTO scheduleDto = new StudentDTO();
        scheduleDto.setId(id);
        when(studentService.getStudentById(id)).thenReturn(student);
        when(studentService.getStudentSchedule(id, 3)).thenReturn(scheduleDto);
        StudentDTO res = controller.getCurrentSchedule(id, null);

        assertNotNull(res);
        assertEquals(id, res.getId());
    }

    @Test
    void shouldGetPreviousSchedule() {
        Integer id = 1000100667;
        int semester = 2;
        StudentDTO expected = new StudentDTO();
        expected.setId(id);
        when(studentService.getStudentSchedule(id, semester)).thenReturn(expected);
        StudentDTO res = controller.getPreviousSchedule(id, semester);

        assertEquals(expected, res);
    }

    @Test
    void shouldGetRequestsByStudentAndStatusWhenStatusNull() {
        Integer studentId = 1000100667;
        ChangeRequestDTO c = new ChangeRequestDTO(); c.setId(UUID.randomUUID());
        when(changeRequestService.getAllRequestsByStudent(studentId)).thenReturn(List.of(c));
        List<ChangeRequestDTO> res = controller.getRequestsByStudentAndStatus(studentId, null);

        assertEquals(1, res.size());
    }

    @Test
    void shouldGetRequestsByStudentAndStatusWhenProvided() {
        Integer studentId = 1000100279;
        RequestStatus status = RequestStatus.PENDING;
        ChangeRequestDTO c = new ChangeRequestDTO(); c.setId(UUID.randomUUID());
        when(studentService.getStudentRequestsByStatus(studentId, status)).thenReturn(List.of(c));
        List<ChangeRequestDTO> res = controller.getRequestsByStudentAndStatus(studentId, status);

        assertEquals(1, res.size());
    }

    @Test
    void shouldGetRequestHistory() {
        UUID requestId = UUID.randomUUID();
        ChangeRequestHistory h1 = new ChangeRequestHistory();
        ChangeRequestHistory h2 = new ChangeRequestHistory();
        when(historyService.getHistory(requestId)).thenReturn(List.of(h1, h2));
        List<ChangeRequestHistory> res = controller.getRequestHistory(12345, requestId);

        assertEquals(2, res.size());
    }

    @Test
    void shouldUpdateRequest() {
        Integer studentId = 1000100279;
        UUID requestId = UUID.randomUUID();
        ChangeRequestDTO in = new ChangeRequestDTO();
        in.setId(requestId);
        ChangeRequestDTO out = new ChangeRequestDTO();
        out.setId(requestId);
        when(changeRequestService.updateChangeRequest(studentId, requestId, in)).thenReturn(out);
        ChangeRequestDTO res = controller.updateRequest(studentId, requestId, in);

        assertEquals(out, res);
    }

    @Test
    void shouldDeleteRequest() {
        Integer studentId = 1000100575;
        UUID requestId = UUID.randomUUID();
        doNothing().when(changeRequestService).deleteChangeRequest(studentId, requestId);
        controller.deleteRequest(studentId, requestId);

        assertTrue(true);
    }

    @Test
    void shouldRequestExceptional() {
        Integer studentId = 1000100516;
        UUID requestId = UUID.randomUUID();
        String reason = "Me fui de viaje a El Cairo y me quede sin cupo";
        ChangeRequestDTO dto = new ChangeRequestDTO();
        dto.setId(requestId);
        when(changeRequestService.requestExceptionalReview(studentId, requestId, reason)).thenReturn(dto);
        ChangeRequestDTO res = controller.requestExceptional(studentId, requestId, reason);

        assertEquals(dto, res);
    }

    @Test
    void shouldGetExceptionalByStudent() {
        Integer studentId = 1000100516;
        ChangeRequestDTO c1 = new ChangeRequestDTO(); c1.setId(UUID.randomUUID());
        when(changeRequestService.getExceptionalRequestsByStudent(studentId)).thenReturn(List.of(c1));
        List<ChangeRequestDTO> res = controller.getExceptionalByStudent(studentId);

        assertEquals(1, res.size());
    }
}
