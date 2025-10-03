package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.controller.SecretariatController;
import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.RequestDatesDTO;
import eci.edu.dosw.proyecto.dtos.RequestDecisionDTO;
import eci.edu.dosw.proyecto.dtos.SecretariatDTO;
import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.services.SecretariatService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecretariatControllerTest {

    @Mock
    private SecretariatService secretariatService;

    @InjectMocks
    private SecretariatController controller;

    @Test
    void ShouldCreateSecretariat() {
        SecretariatDTO dto = new SecretariatDTO();
        dto.setId(1);
        dto.setName("Secretaría Académica");

        when(secretariatService.createSecretariat(dto)).thenReturn(dto);

        SecretariatDTO response = controller.createSecretariat(dto);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Secretaría Académica", response.getName());
    }

    @Test
    void ShouldGetSecretariatById() {
        SecretariatDTO dto = new SecretariatDTO();
        dto.setId(2);
        dto.setName("Secretaría de Sistemas");

        when(secretariatService.getSecretariatById(2)).thenReturn(dto);

        SecretariatDTO response = controller.getSecretariat(2);

        assertNotNull(response);
        assertEquals(2, response.getId());
        assertEquals("Secretaría de Sistemas", response.getName());
    }

    @Test
    void ShouldGetAllSecretariats() {
        SecretariatDTO s1 = new SecretariatDTO();
        s1.setId(1);
        SecretariatDTO s2 = new SecretariatDTO();
        s2.setId(2);

        when(secretariatService.getAllSecretariats()).thenReturn(List.of(s1, s2));

        List<SecretariatDTO> response = controller.getAllSecretariats();

        assertEquals(2, response.size());
    }

    @Test
    void ShouldUpdateSecretariat() {
        SecretariatDTO dto = new SecretariatDTO();
        dto.setId(3);
        dto.setName("Secretaría Actualizada");

        when(secretariatService.updateSecretariat(3, dto)).thenReturn(dto);

        SecretariatDTO response = controller.updateSecretariat(3, dto);

        assertNotNull(response);
        assertEquals("Secretaría Actualizada", response.getName());
    }

    @Test
    void ShouldDeleteSecretariat() {
        doNothing().when(secretariatService).deleteSecretariat(5);

        controller.deleteSecretariat(5);

        verify(secretariatService).deleteSecretariat(5);
    }

    @Test
    void ShouldUpdateRequestDates() {
        RequestDatesDTO dates = new RequestDatesDTO(LocalDateTime.now(), LocalDateTime.now().plusDays(5));

        doNothing().when(secretariatService).updateRequestDates(7, dates.getStartDate(), dates.getEndDate());

        controller.updateRequestDates(7, dates);

        verify(secretariatService).updateRequestDates(7, dates.getStartDate(), dates.getEndDate());
    }

    @Test
    void ShouldRespondRequestBySecretariat() {
        UUID requestId = UUID.randomUUID();
        RequestDecisionDTO decision = new RequestDecisionDTO();
        RequestDatesDTO dates = new RequestDatesDTO(LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        ChangeRequestDTO returned = new ChangeRequestDTO();

        when(secretariatService.respondRequestBySecretariat(requestId, decision, dates)).thenReturn(returned);

        ResponseEntity<ChangeRequestDTO> response = controller.respondRequestBySecretariat(
                requestId, decision, dates.getStartDate(), dates.getEndDate()
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(returned, response.getBody());
    }

    @SuppressWarnings("null")
    @Test
    void ShouldGetRequestsByFacultyAndStatus() {
        Faculty faculty = Faculty.INGENIERIA_DE_SISTEMAS;
        RequestStatus status = RequestStatus.PENDING;
        ChangeRequestDTO dto = new ChangeRequestDTO();

        when(secretariatService.getRequestsByFacultyAndStatus(faculty, status)).thenReturn(List.of(dto));

        ResponseEntity<List<ChangeRequestDTO>> response = controller.getRequestsByFacultyAndStatus(faculty, status);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }
}
