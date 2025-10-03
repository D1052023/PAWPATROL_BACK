package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.controller.DeaneryController;
import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.DeaneryDTO;
import eci.edu.dosw.proyecto.dtos.RequestDatesDTO;
import eci.edu.dosw.proyecto.dtos.RequestDecisionDTO;
import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.services.DeaneryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeaneryControllerTest {

    @Mock
    private DeaneryService deaneryService;

    @InjectMocks
    private DeaneryController controller;

    @Test
    void ShouldCreateDeanery() {
        DeaneryDTO dto = new DeaneryDTO();
        dto.setId(1);
        dto.setName("Decano Ingeniería");

        when(deaneryService.createDeanery(dto)).thenReturn(dto);

        ResponseEntity<DeaneryDTO> response = controller.createDeanery(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void ShouldGetDeaneryById() {
        DeaneryDTO dto = new DeaneryDTO();
        dto.setId(5);
        dto.setName("Decano de Economía");

        when(deaneryService.getDeaneryById(5)).thenReturn(dto);

        ResponseEntity<DeaneryDTO> response = controller.getDeaneryById(5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
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

    @SuppressWarnings("null")
    @Test
    void ShouldGetAllDeaneries() {
        DeaneryDTO d1 = new DeaneryDTO();
        d1.setId(1);
        DeaneryDTO d2 = new DeaneryDTO();
        d2.setId(2);

        when(deaneryService.getAllDeaneries()).thenReturn(List.of(d1, d2));

        ResponseEntity<List<DeaneryDTO>> response = controller.getAllDeaneries();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @SuppressWarnings("null")
    @Test
    void ShouldUpdateDeanery() {
        DeaneryDTO dto = new DeaneryDTO();
        dto.setId(3);
        dto.setName("Nuevo nombre");

        when(deaneryService.updateDeanery(3, dto)).thenReturn(dto);

        ResponseEntity<DeaneryDTO> response = controller.updateDeanery(3, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Nuevo nombre", response.getBody().getName());
    }

    @Test
    void ShouldDeleteDeanery() {
        doNothing().when(deaneryService).deleteDeanery(7);

        ResponseEntity<Void> response = controller.deleteDeanery(7);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(deaneryService).deleteDeanery(7);
    }

    @Test
    void ShouldRespondRequestByDeanery() {
        int deaneryId = 10;
        UUID requestId = UUID.randomUUID();
        RequestDecisionDTO decision = new RequestDecisionDTO();
        RequestDatesDTO dates = new RequestDatesDTO();
        ChangeRequestDTO returned = new ChangeRequestDTO();

        when(deaneryService.respondRequestByDeanery(deaneryId, requestId, decision, dates)).thenReturn(returned);

        ResponseEntity<ChangeRequestDTO> response = controller.respondRequestByDeanery(deaneryId, requestId, decision, dates);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(returned, response.getBody());
    }

    @SuppressWarnings("null")
    @Test
    void ShouldGetRequestsByFacultyAndStatus() {
        Faculty faculty = Faculty.INGENIERIA_DE_SISTEMAS;
        RequestStatus status = RequestStatus.PENDING;
        ChangeRequestDTO dto = new ChangeRequestDTO();

        when(deaneryService.getRequestsByFacultyAndStatus(faculty, status)).thenReturn(List.of(dto));

        ResponseEntity<List<ChangeRequestDTO>> response = controller.getRequestsByFacultyAndStatus(faculty, status);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }
}
