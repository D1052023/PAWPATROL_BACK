package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.dtos.DeaneryDTO;
import eci.edu.dosw.proyecto.dtos.RequestDatesDTO;
import eci.edu.dosw.proyecto.dtos.RequestDecisionDTO;
import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.mappers.ChangeRequestMapper;
import eci.edu.dosw.proyecto.mappers.DeaneryMapper;
import eci.edu.dosw.proyecto.models.ChangeRequest;
import eci.edu.dosw.proyecto.models.Deanery;
import eci.edu.dosw.proyecto.models.Group;
import eci.edu.dosw.proyecto.repositories.ChangeRequestRepository;
import eci.edu.dosw.proyecto.repositories.DeaneryRepository;
import eci.edu.dosw.proyecto.repositories.GroupRepository;
import eci.edu.dosw.proyecto.services.AlertService;
import eci.edu.dosw.proyecto.services.impl.DeaneryServiceImpl;

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
class DeaneryServiceImplTest {

    @Mock
    DeaneryRepository deaneryRepository;

    @Mock
    ChangeRequestRepository changeRequestRepository;

    @Mock
    DeaneryMapper deaneryMapper;

    @Mock
    ChangeRequestMapper changeRequestMapper;

    @Mock
    GroupRepository groupRepository;

    @Mock
    AlertService alertService;

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
    void ShouldGetDeaneryById() {
        Deanery d = new Deanery();
        d.setId(2);
        d.setName("Oswaldo Castillo Navetty"); 
        d.setEmail("oswaldo.castillo@escuelaing.edu.co");

        when(deaneryRepository.findById(2)).thenReturn(Optional.of(d));
        when(deaneryMapper.toDTO(d)).thenReturn(new DeaneryDTO(){{
            setId(2); setName("Oswaldo Castillo Navetty"); setEmail("oswaldo.castillo@escuelaing.edu.co");
        }});

        DeaneryDTO res = deaneryService.getDeaneryById(2);
        assertEquals(2, res.getId());
        assertEquals("Oswaldo Castillo Navetty", res.getName());
    }

    @Test
    void ShouldThrowWhenGetDeaneryByIdMissing() {
        when(deaneryRepository.findById(999)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> deaneryService.getDeaneryById(999));
        assertTrue(ex.getMessage().contains("Decan@ no encontrado"));
    }

    @Test
    void ShouldGetDeaneryByFaculty() {
        Deanery d = new Deanery();
        d.setId(3);
        d.setFaculty(Faculty.INGENIERIA_DE_SISTEMAS);
        d.setName("Decana Ingeniería Claudia Santiago");
        
        when(deaneryRepository.findByFaculty(Faculty.INGENIERIA_DE_SISTEMAS)).thenReturn(Optional.of(d));
        when(deaneryMapper.toDTO(d)).thenReturn(new DeaneryDTO(){{
            setId(3); setName("Decana Ingeniería Claudia Santiago");
        }});

        DeaneryDTO res = deaneryService.getDeaneryByFaculty(Faculty.INGENIERIA_DE_SISTEMAS);
        assertEquals(3, res.getId());
        assertEquals("Decana Ingeniería Claudia Santiago", res.getName());
    }

    @Test
    void ShouldThrowWhenGetDeaneryByFacultyNotFound() {
        when(deaneryRepository.findByFaculty(Faculty.INGENIERIA_DE_SISTEMAS)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> deaneryService.getDeaneryByFaculty(Faculty.INGENIERIA_DE_SISTEMAS));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
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
    void ShouldUpdateDeanery() {
        Deanery existing = new Deanery();
        existing.setId(8);
        existing.setName("Old");
        existing.setEmail("old@mail");

        DeaneryDTO update = new DeaneryDTO();
        update.setName("Claudia Patricia Santiago Cely");
        update.setEmail("claudia.santiago@escuelaing.edu.co");

        when(deaneryRepository.findById(8)).thenReturn(Optional.of(existing));
        when(deaneryRepository.save(existing)).thenReturn(existing);
        when(deaneryMapper.toDTO(existing)).thenReturn(new DeaneryDTO(){{
            setId(8); setName("Claudia Patricia Santiago Cely"); setEmail("claudia.santiago@escuelaing.edu.co");
        }});

        DeaneryDTO res = deaneryService.updateDeanery(8, update);
        assertEquals(8, res.getId());
        assertEquals("Claudia Patricia Santiago Cely", res.getName());
    }

    @Test
    void ShouldDeleteDeanery() {
        when(deaneryRepository.existsById(12)).thenReturn(true);
        deaneryService.deleteDeanery(12);
        verify(deaneryRepository).deleteById(12);
    }

    @Test
    void shouldThrowWhenDeleteMissingDeanery() {
        when(deaneryRepository.existsById(9999)).thenReturn(false);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> deaneryService.deleteDeanery(9999));
        assertTrue(ex.getMessage().contains("Decan@ no encontrado"));
    }

    @Test
    void ShouldThrowWhenDeaneryNotFound() {
        int deaneryId = 99;
        UUID reqId = UUID.randomUUID();

        when(deaneryRepository.findById(deaneryId)).thenReturn(Optional.empty());

        RequestDecisionDTO decision = new RequestDecisionDTO();
        RequestDatesDTO dates = new RequestDatesDTO();

        dates.setStartDate(LocalDateTime.now().minusDays(1));
        dates.setEndDate(LocalDateTime.now().plusDays(1));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> deaneryService.respondRequestByDeanery(deaneryId, reqId, decision, dates));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void ShouldThrowWhenRequestNotFound() {
        int deaneryId = 1;

        Deanery deanery = new Deanery();
        deanery.setId(deaneryId);
        deanery.setFaculty(Faculty.INGENIERIA_DE_SISTEMAS);
        UUID reqId = UUID.randomUUID();
        
        when(deaneryRepository.findById(deaneryId)).thenReturn(Optional.of(deanery));
        when(changeRequestRepository.findById(reqId)).thenReturn(Optional.empty());

        RequestDecisionDTO decision = new RequestDecisionDTO();
        RequestDatesDTO dates = new RequestDatesDTO();

        dates.setStartDate(LocalDateTime.now().minusDays(1));
        dates.setEndDate(LocalDateTime.now().plusDays(1));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> deaneryService.respondRequestByDeanery(deaneryId, reqId, decision, dates));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void ShouldThrowWhenOutsideDates() {
        int deaneryId = 1;
        Deanery deanery = new Deanery();
        deanery.setId(deaneryId);
        deanery.setFaculty(Faculty.INGENIERIA_DE_SISTEMAS);
        UUID reqId = UUID.randomUUID();
        ChangeRequest req = new ChangeRequest();
        req.setId(reqId);
        req.setStatus(RequestStatus.PENDING);
        req.setFaculty(Faculty.INGENIERIA_DE_SISTEMAS);

        when(deaneryRepository.findById(deaneryId)).thenReturn(Optional.of(deanery));
        when(changeRequestRepository.findById(reqId)).thenReturn(Optional.of(req));

        RequestDecisionDTO decision = new RequestDecisionDTO();
        RequestDatesDTO dates = new RequestDatesDTO();
        dates.setStartDate(LocalDateTime.now().plusDays(1)); 
        dates.setEndDate(LocalDateTime.now().plusDays(2));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> deaneryService.respondRequestByDeanery(deaneryId, reqId, decision, dates));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void ShouldThrowWhenFacultyMismatch() {
        int deaneryId = 1;

        Deanery deanery = new Deanery();
        deanery.setId(deaneryId);
        deanery.setFaculty(Faculty.INGENIERIA_DE_SISTEMAS);
        UUID reqId = UUID.randomUUID();

        ChangeRequest req = new ChangeRequest();
        req.setId(reqId);
        req.setStatus(RequestStatus.PENDING);
        req.setFaculty(null); 
        
        when(deaneryRepository.findById(deaneryId)).thenReturn(Optional.of(deanery));
        when(changeRequestRepository.findById(reqId)).thenReturn(Optional.of(req));

        RequestDecisionDTO decision = new RequestDecisionDTO();
        RequestDatesDTO dates = new RequestDatesDTO();
        dates.setStartDate(LocalDateTime.now().minusDays(1));
        dates.setEndDate(LocalDateTime.now().plusDays(1));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> deaneryService.respondRequestByDeanery(deaneryId, reqId, decision, dates));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void ShouldThrowWhenRequestAlreadyProcessed() {
        int deaneryId = 1;

        Deanery deanery = new Deanery();
        deanery.setId(deaneryId);
        deanery.setFaculty(Faculty.INGENIERIA_DE_SISTEMAS);
        UUID reqId = UUID.randomUUID();

        ChangeRequest req = new ChangeRequest();
        req.setId(reqId);
        req.setStatus(RequestStatus.APPROVED); 
        req.setFaculty(Faculty.INGENIERIA_DE_SISTEMAS);

        when(deaneryRepository.findById(deaneryId)).thenReturn(Optional.of(deanery));
        when(changeRequestRepository.findById(reqId)).thenReturn(Optional.of(req));

        RequestDecisionDTO decision = new RequestDecisionDTO();
        RequestDatesDTO dates = new RequestDatesDTO();
        dates.setStartDate(LocalDateTime.now().minusDays(1));
        dates.setEndDate(LocalDateTime.now().plusDays(1));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> deaneryService.respondRequestByDeanery(deaneryId, reqId, decision, dates));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void shouldThrowWhenTargetGroupFull() {
        int deaneryId = 1;

        Deanery deanery = new Deanery();
        deanery.setId(deaneryId);
        deanery.setFaculty(Faculty.INGENIERIA_DE_SISTEMAS);
        UUID reqId = UUID.randomUUID();

        ChangeRequest req = new ChangeRequest();
        req.setId(reqId);
        req.setStatus(RequestStatus.PENDING);
        req.setFaculty(Faculty.INGENIERIA_DE_SISTEMAS);
        req.setCurrentGroup("1");
        req.setTargetGroup("2");

        Group current = new Group();
        current.setGroupId("1");
        current.setCurrentCapacity(5);
        current.setMaximumCapacity(30);

        Group target = new Group();
        target.setGroupId("2");
        target.setCurrentCapacity(30);
        target.setMaximumCapacity(30); 

        when(deaneryRepository.findById(deaneryId)).thenReturn(Optional.of(deanery));
        when(changeRequestRepository.findById(reqId)).thenReturn(Optional.of(req));
        when(groupRepository.findByGroupId("1")).thenReturn(Optional.of(current));
        when(groupRepository.findByGroupId("2")).thenReturn(Optional.of(target));

        RequestDecisionDTO decision = new RequestDecisionDTO();
        decision.setStatus(RequestStatus.APPROVED);

        RequestDatesDTO dates = new RequestDatesDTO();
        dates.setStartDate(LocalDateTime.now().minusDays(1));
        dates.setEndDate(LocalDateTime.now().plusDays(1));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> deaneryService.respondRequestByDeanery(deaneryId, reqId, decision, dates));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
}