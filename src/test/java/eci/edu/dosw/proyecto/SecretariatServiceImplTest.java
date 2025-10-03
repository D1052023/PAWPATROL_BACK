package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.dtos.RequestDatesDTO;
import eci.edu.dosw.proyecto.dtos.RequestDecisionDTO;
import eci.edu.dosw.proyecto.dtos.SecretariatDTO;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.mappers.ChangeRequestMapper;
import eci.edu.dosw.proyecto.mappers.SecretariatMapper;
import eci.edu.dosw.proyecto.models.ChangeRequest;
import eci.edu.dosw.proyecto.models.Secretariat;
import eci.edu.dosw.proyecto.repositories.ChangeRequestRepository;
import eci.edu.dosw.proyecto.repositories.GroupRepository;
import eci.edu.dosw.proyecto.repositories.SecretariatRepository;
import eci.edu.dosw.proyecto.services.AlertService;
import eci.edu.dosw.proyecto.services.impl.SecretariatServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class SecretariatServiceImplTest {

    @Mock
    SecretariatRepository secretariatRepository;

    @Mock
    ChangeRequestRepository changeRequestRepository;

    @Mock
    SecretariatMapper secretariatMapper;

    @Mock
    ChangeRequestMapper changeRequestMapper;

    @Mock
    GroupRepository groupRepository;

    @Mock
    AlertService alertService;

    @InjectMocks
    SecretariatServiceImpl secretariatService;

    @Test
    void ShouldGetSecretariatById() {
        Secretariat sec = new Secretariat();
        sec.setId(1);
        sec.setName("Patricia Salazar Perdomo");
        sec.setEmail("patricia.salazar@escuelaing.edu.co");

        SecretariatDTO dto = new SecretariatDTO();
        dto.setId(1);
        dto.setName("Patricia Salazar Perdomo");
        dto.setEmail("patricia.salazar@escuelaing.edu.co");

        when(secretariatRepository.findById(1)).thenReturn(Optional.of(sec));
        when(secretariatMapper.toDTO(sec)).thenReturn(dto);

        SecretariatDTO out = secretariatService.getSecretariatById(1);
        assertNotNull(out);
        assertEquals(1, out.getId());
        assertEquals("Patricia Salazar Perdomo", out.getName());
        assertEquals("patricia.salazar@escuelaing.edu.co", out.getEmail());
    }

    @Test
    void ShouldCreateSecretariat() {
        SecretariatDTO in = new SecretariatDTO();
        in.setName("Patricia Salazar Perdomo");
        in.setEmail("patricia.salazar@escuelaing.edu.co");

        Secretariat entity = new Secretariat();
        entity.setName("Patricia Salazar Perdomo");
        entity.setEmail("patricia.salazar@escuelaing.edu.co");

        Secretariat saved = new Secretariat();
        saved.setId(1000000398);
        saved.setName(entity.getName());
        saved.setEmail(entity.getEmail());

        SecretariatDTO outDto = new SecretariatDTO();
        outDto.setId(1000000398);
        outDto.setName(entity.getName());
        outDto.setEmail(entity.getEmail());

        when(secretariatMapper.toEntity(in)).thenReturn(entity);
        when(secretariatRepository.save(entity)).thenReturn(saved);
        when(secretariatMapper.toDTO(entity)).thenReturn(outDto);

        SecretariatDTO res = secretariatService.createSecretariat(in);
        assertEquals(1000000398, res.getId());
        assertEquals("Patricia Salazar Perdomo", res.getName());
        assertEquals("patricia.salazar@escuelaing.edu.co", res.getEmail());
    }

    @Test
    void ShouldReturnAllSecretariats() {
        Secretariat s1 = new Secretariat(); s1.setId(1); s1.setName("Patricia Salazar Perdomo");
        Secretariat s2 = new Secretariat(); s2.setId(2); s2.setName("Myriam Astrid Angarita Gómez");

        SecretariatDTO d1 = new SecretariatDTO(); d1.setId(1); d1.setName("Patricia Salazar Perdomo");
        SecretariatDTO d2 = new SecretariatDTO(); d2.setId(2); d2.setName("Myriam Astrid Angarita Gómez");

        when(secretariatRepository.findAll()).thenReturn(List.of(s1, s2));
        when(secretariatMapper.toDTO(s1)).thenReturn(d1);
        when(secretariatMapper.toDTO(s2)).thenReturn(d2);

        List<SecretariatDTO> list = secretariatService.getAllSecretariats();
        assertEquals(2, list.size());
        assertEquals("Patricia Salazar Perdomo", list.get(0).getName());
    }

    @Test
    void ShouldUpdateSecretariat() {
        Secretariat existing = new Secretariat();
        existing.setId(5);
        existing.setName("Old Secretary");
        existing.setEmail("old@escuelaing.edu.co");
        existing.setRequestStartDate(LocalDateTime.of(2023,1,1,0,0));
        existing.setRequestEndDate(LocalDateTime.of(2023,12,31,0,0));

        SecretariatDTO update = new SecretariatDTO();
        update.setName("Patricia Salazar Perdomo");
        update.setEmail("patricia.salazar@escuelaing.edu.co");

        when(secretariatRepository.findById(5)).thenReturn(Optional.of(existing));
        when(secretariatMapper.toDTO(existing)).thenReturn(new SecretariatDTO(){{
            setId(5); setName("Patricia Salazar Perdomo"); setEmail("patricia.salazar@escuelaing.edu.co");
        }});

        SecretariatDTO res = secretariatService.updateSecretariat(5, update);
        assertEquals(5, res.getId());
        assertEquals("Patricia Salazar Perdomo", res.getName());
    }

    @Test
    void ShouldUpdateRequestDates() {
        Secretariat existing = new Secretariat();
        existing.setId(7);
        when(secretariatRepository.findById(7)).thenReturn(Optional.of(existing));

        LocalDateTime s = LocalDateTime.now().minusDays(1);
        LocalDateTime e = LocalDateTime.now().plusDays(1);

        secretariatService.updateRequestDates(7, s, e);

        assertEquals(s, existing.getRequestStartDate());
        assertEquals(e, existing.getRequestEndDate());
    }

    @Test
    void ShouldThrowOnDeleteSecretariat() {
        when(secretariatRepository.existsById(999)).thenReturn(false);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> secretariatService.deleteSecretariat(999));

        assertTrue(ex.getMessage().contains("Secretari@ no encontrad@"));
    }


    @Test
    void ShouldThrowWhenRequestAlreadyProcessed() {
        UUID reqId = UUID.randomUUID();
        ChangeRequest req = new ChangeRequest();
        req.setId(reqId);
        req.setStatus(RequestStatus.APPROVED);

        when(changeRequestRepository.findById(reqId)).thenReturn(Optional.of(req));

        RequestDecisionDTO decision = new RequestDecisionDTO();
        RequestDatesDTO dates = new RequestDatesDTO();
        dates.setStartDate(LocalDateTime.now().minusDays(1));
        dates.setEndDate(LocalDateTime.now().plusDays(1));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> secretariatService.respondRequestBySecretariat(reqId, decision, dates));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void ShouldThrowWhenDateOutRange() {
        UUID reqId = UUID.randomUUID();
        ChangeRequest req = new ChangeRequest();
        req.setId(reqId);
        req.setStatus(RequestStatus.PENDING);

        when(changeRequestRepository.findById(reqId)).thenReturn(Optional.of(req));

        RequestDecisionDTO decision = new RequestDecisionDTO();
        RequestDatesDTO dates = new RequestDatesDTO();
        dates.setStartDate(LocalDateTime.now().minusDays(10));
        dates.setEndDate(LocalDateTime.now().minusDays(5));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> secretariatService.respondRequestBySecretariat(reqId, decision, dates));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

}