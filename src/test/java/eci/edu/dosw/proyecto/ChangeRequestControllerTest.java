package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.controller.ChangeRequestController;
import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.services.ChangeRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeRequestControllerTest {

    @Mock
    private ChangeRequestService changeRequestService;

    @InjectMocks
    private ChangeRequestController changeRequestController;

    private ChangeRequestDTO requestDTO;
    private UUID requestId;

    @BeforeEach
    void setup() {
        requestDTO = new ChangeRequestDTO();
        requestDTO.setCurrentSubject("DOSW");
        requestDTO.setTargetSubject("DDYA");

        requestId = UUID.randomUUID();
    }

    @Test
    void testCreateRequest() {
        when(changeRequestService.createChangeRequest(eq(1), any(ChangeRequestDTO.class)))
                .thenReturn(requestDTO);

        ChangeRequestDTO result = changeRequestController.createRequest(1, requestDTO);

        assertNotNull(result);
        assertEquals("DOSW", result.getCurrentSubject());
        assertEquals("DDYA", result.getTargetSubject());
    }

    @Test
    void testGetRequestsByStudent() {
        when(changeRequestService.getAllRequestsByStudent(1))
                .thenReturn(Arrays.asList(requestDTO));

        List<ChangeRequestDTO> result = changeRequestController.getRequestsByStudent(1);

        assertEquals(1, result.size());
        assertEquals("DOSW", result.get(0).getCurrentSubject());
    }

    @Test
    void testGetRequestById() {
        when(changeRequestService.getRequestById(1, requestId))
                .thenReturn(requestDTO);

        ChangeRequestDTO result = changeRequestController.getRequestById(1, requestId);

        assertNotNull(result);
        assertEquals("DDYA", result.getTargetSubject());
    }

    @Test
    void testGetRequestsByStudentAndStatus() {
        when(changeRequestService.getAllRequestsByStudent(1))
                .thenReturn(Arrays.asList(requestDTO));

        List<ChangeRequestDTO> result = changeRequestController.getRequestsByStudentAndStatus(1, RequestStatus.PENDING);

        assertEquals(1, result.size());
    }
}
