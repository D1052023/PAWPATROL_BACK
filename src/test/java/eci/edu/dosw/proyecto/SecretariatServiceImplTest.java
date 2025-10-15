package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.RequestDatesDTO;
import eci.edu.dosw.proyecto.dtos.RequestDecisionDTO;
import eci.edu.dosw.proyecto.dtos.SecretariatDTO;
import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.mappers.ChangeRequestMapper;
import eci.edu.dosw.proyecto.mappers.SecretariatMapper;
import eci.edu.dosw.proyecto.models.ChangeRequest;
import eci.edu.dosw.proyecto.models.Group;
import eci.edu.dosw.proyecto.models.Secretariat;
import eci.edu.dosw.proyecto.repositories.ChangeRequestRepository;
import eci.edu.dosw.proyecto.repositories.GroupRepository;
import eci.edu.dosw.proyecto.repositories.SecretariatRepository;
import eci.edu.dosw.proyecto.services.AlertService;
import eci.edu.dosw.proyecto.services.HistoryService;
import eci.edu.dosw.proyecto.services.impl.SecretariatServiceImpl;
import eci.edu.dosw.proyecto.util.MessageExceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

    @Mock
    HistoryService historyService;

    @Mock
    MessageExceptions message;

    @InjectMocks
    SecretariatServiceImpl secretariatService;

    @Test
    void shouldGetSecretariatById() {
        Secretariat sec = new Secretariat();
        sec.setId(1000000398);
        sec.setName("Patricia Salazar Perdomo");
        when(message.findSecretariatOrThrow(1000000398)).thenReturn(sec);
        SecretariatDTO dto = new SecretariatDTO();
        dto.setId(1000000398);
        dto.setName("Patricia Salazar Perdomo");
        when(secretariatMapper.toDTO(sec)).thenReturn(dto);
        SecretariatDTO out = secretariatService.getSecretariatById(1000000398);

        assertNotNull(out);
        assertEquals(1000000398, out.getId());
        assertEquals("Patricia Salazar Perdomo", out.getName());
    }

    @Test
    void shouldCreateSecretariat() {
        SecretariatDTO input = new SecretariatDTO();
        input.setName("Oscar");
        input.setEmail("oscar@escuelaing.edu.co");
        Secretariat ent = new Secretariat();
        ent.setName("Oscar");
        ent.setEmail("oscar@escuelaing.edu.co");
        SecretariatDTO outDto = new SecretariatDTO();
        outDto.setName("Oscar");
        outDto.setEmail("oscar@escuelaing.edu.co");
        when(secretariatMapper.toEntity(input)).thenReturn(ent);
        when(secretariatRepository.save(ent)).thenReturn(ent);
        when(secretariatMapper.toDTO(ent)).thenReturn(outDto);
        SecretariatDTO res = secretariatService.createSecretariat(input);

        assertNotNull(res);
        assertEquals("Oscar", res.getName());
    }

    @Test
    void shouldGetAllSecretariats() {
        Secretariat s1 = new Secretariat(); s1.setId(1); s1.setName("S1");
        Secretariat s2 = new Secretariat(); s2.setId(2); s2.setName("S2");
        when(secretariatRepository.findAll()).thenReturn(List.of(s1, s2));
        when(secretariatMapper.toDTO(s1)).thenReturn(new SecretariatDTO() {{ setId(1); setName("S1"); }});
        when(secretariatMapper.toDTO(s2)).thenReturn(new SecretariatDTO() {{ setId(2); setName("S2"); }});
        List<SecretariatDTO> out = secretariatService.getAllSecretariats();

        assertEquals(2, out.size());
    }

    @Test
    void shouldUpdateSecretariatPartialFields() {
        int id = 22;
        Secretariat sec = new Secretariat();
        sec.setId(id);
        sec.setName("Diego Sánchez Fonseca");
        sec.setEmail("diego.fonseca@escuelaing.edu.co");
        sec.setRequestStartDate(LocalDateTime.now().minusDays(10));
        sec.setRequestEndDate(LocalDateTime.now().minusDays(1));
        when(message.findSecretariatOrThrow(id)).thenReturn(sec);
        SecretariatDTO update = new SecretariatDTO();
        update.setName("Patricia Salazar Perdomo");
        update.setRequestStartDate(LocalDateTime.now().minusDays(2));
        when(secretariatRepository.save(sec)).thenReturn(sec);
        when(secretariatMapper.toDTO(sec)).thenReturn(new SecretariatDTO() {{
            setId(id); setName("Patricia Salazar Perdomo"); setRequestStartDate(update.getRequestStartDate());
        }});
        SecretariatDTO out = secretariatService.updateSecretariat(id, update);

        assertNotNull(out);
        assertEquals("Patricia Salazar Perdomo", out.getName());
    }

    @Test
    void shouldUpdateRequestDates() {
        int id = 1000000398;
        Secretariat sec = new Secretariat();
        sec.setId(id);
        when(message.findSecretariatOrThrow(id)).thenReturn(sec);
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        secretariatService.updateRequestDates(id, start, end);

        assertEquals(start, sec.getRequestStartDate());
        assertEquals(end, sec.getRequestEndDate());
    }

    @Test
    void shouldDeleteSecretariat() {
        int id = 1000000398;
        Secretariat sec = new Secretariat();
        sec.setId(id);
        when(message.findSecretariatOrThrow(id)).thenReturn(sec);
        secretariatService.deleteSecretariat(id);

        assertEquals(id, sec.getId());
    }

    @Test
    void shouldRespondRequestBySecretariatRequestAdditionalInfoBranch() {
        UUID reqId = UUID.randomUUID();
        ChangeRequest req = new ChangeRequest();
        req.setId(reqId);
        req.setStatus(RequestStatus.PENDING);
        req.setObservations(null);
        when(message.findChangeRequestOrThrow(reqId)).thenReturn(req);
        doNothing().when(message).ensureRequestPending(req);
        RequestDecisionDTO decision = new RequestDecisionDTO();
        decision.setRequestAdditionalInfo(true);
        decision.setAdditionalInfoRequestMessage("Adjuntar docs");
        decision.setInfoDueDate(LocalDateTime.now().plusDays(5));
        decision.setStatus(RequestStatus.REQUEST_ADDITIONAL_INFO);
        RequestDatesDTO rd = new RequestDatesDTO();
        rd.setStartDate(LocalDateTime.now().minusDays(1));
        rd.setEndDate(LocalDateTime.now().plusDays(1));
        when(changeRequestRepository.save(req)).thenReturn(req);
        when(changeRequestMapper.toDTO(req)).thenReturn(new ChangeRequestDTO() {{ setId(reqId); }});
        ChangeRequestDTO out = secretariatService.respondRequestBySecretariat(reqId, decision, rd);

        assertNotNull(out);
        assertEquals(reqId, out.getId());
    }


    @Test
    void shouldRespondRequestBySecretariat_approvedBranch_removesFromCurrentWaitlist_and_savesGroups() {
        UUID reqId = UUID.randomUUID();
        ChangeRequest req = new ChangeRequest();
        req.setId(reqId);
        req.setStatus(RequestStatus.PENDING);
        req.setStudentId(1000100667);
        req.setCurrentGroup("DOSW-1");
        req.setTargetGroup("DOSW-2");
        when(message.findChangeRequestOrThrow(reqId)).thenReturn(req);
        doNothing().when(message).ensureRequestPending(req);
        Group current = new Group();
        current.setGroupId("DOSW-1");
        current.setWaitlist(new ArrayList<>(List.of(1000100667, 1000100282)));
        Group target = new Group();
        target.setGroupId("DOSW-2");
        target.setCurrentCapacity(5);
        target.setMaximumCapacity(30);
        target.setWaitlist(new ArrayList<>());
        when(message.findGroupOrThrow("DOSW-1")).thenReturn(current);
        when(message.findGroupOrThrow("DOSW-2")).thenReturn(target);
        RequestDecisionDTO decision = new RequestDecisionDTO();
        decision.setStatus(RequestStatus.APPROVED);
        decision.setObservations("OK");
        RequestDatesDTO rd = new RequestDatesDTO();
        doNothing().when(message).ensureGroupHasCapacity(target);
        when(changeRequestRepository.save(req)).thenReturn(req);
        when(changeRequestMapper.toDTO(req)).thenReturn(new ChangeRequestDTO() {{ setId(reqId); }});
        ChangeRequestDTO out = secretariatService.respondRequestBySecretariat(reqId, decision, rd);

        assertNotNull(out);
        assertEquals(reqId, out.getId());
        assertFalse(current.getWaitlist().contains(1000100667));
    }

    @Test
    void shouldGetRequestsByFacultyAndStatus() {
        ChangeRequest r1 = new ChangeRequest(); r1.setId(UUID.randomUUID()); r1.setStudentId(1);
        ChangeRequest r2 = new ChangeRequest(); r2.setId(UUID.randomUUID()); r2.setStudentId(2);
        when(changeRequestRepository.findByFacultyAndStatus(Faculty.ECONOMIA, RequestStatus.PENDING)).thenReturn(List.of(r1, r2));
        when(changeRequestMapper.toDTO(r1)).thenReturn(new ChangeRequestDTO() {{ setId(r1.getId()); }});
        when(changeRequestMapper.toDTO(r2)).thenReturn(new ChangeRequestDTO() {{ setId(r2.getId()); }});

        var out = secretariatService.getRequestsByFacultyAndStatus(Faculty.ECONOMIA, RequestStatus.PENDING);
        assertEquals(2, out.size());
    }

    @Test
    void shouldUpdateRequestAsSecretariat() {
        UUID reqId = UUID.randomUUID();
        ChangeRequest req = new ChangeRequest();
        req.setId(reqId);
        req.setStatus(RequestStatus.PENDING);
        when(message.findChangeRequestOrThrow(reqId)).thenReturn(req);
        doNothing().when(message).ensureRequestPending(req);
        RequestDecisionDTO decision = new RequestDecisionDTO();
        RequestDatesDTO rd = new RequestDatesDTO();
        when(changeRequestRepository.save(req)).thenReturn(req);
        when(changeRequestMapper.toDTO(req)).thenReturn(new ChangeRequestDTO() {{ setId(reqId); }});
        var out = secretariatService.updateRequestAsSecretariat(reqId, decision, rd);
        assertNotNull(out);
    }

    @Test
    void shouldUpdateRequestAsSecretariatWhenApproved() {
        UUID reqId = UUID.randomUUID();
        ChangeRequest req = new ChangeRequest();
        req.setId(reqId);
        req.setStatus(RequestStatus.PENDING);
        req.setCurrentGroup("TPYC-1");
        req.setTargetGroup("TPYC-5");
        req.setStudentId(1000100279);
        when(message.findChangeRequestOrThrow(reqId)).thenReturn(req);
        doNothing().when(message).ensureRequestPending(req);
        Group current = new Group();
        current.setGroupId("TPYC-1");
        current.setWaitlist(new ArrayList<>(List.of(1000100279)));
        Group target = new Group();
        target.setGroupId("TPYC-5");
        target.setCurrentCapacity(1);
        target.setMaximumCapacity(10);
        target.setWaitlist(new ArrayList<>());
        when(message.findGroupOrThrow("TPYC-1")).thenReturn(current);
        when(message.findGroupOrThrow("TPYC-5")).thenReturn(target);
        doNothing().when(message).ensureGroupHasCapacity(target);
        RequestDecisionDTO decision = new RequestDecisionDTO();
        decision.setStatus(RequestStatus.APPROVED);
        RequestDatesDTO rd = new RequestDatesDTO();
        when(changeRequestRepository.save(req)).thenReturn(req);
        when(changeRequestMapper.toDTO(req)).thenReturn(new ChangeRequestDTO() {{ setId(reqId); }});
        var out = secretariatService.updateRequestAsSecretariat(reqId, decision, rd);

        assertNotNull(out);
    }

    @Test
    void shouldDeleteRequestAsSecretariatDeleteRequest() {
        UUID reqId = UUID.randomUUID();
        ChangeRequest req = new ChangeRequest();
        req.setId(reqId);
        req.setStudentId(1000100279);
        req.setTargetGroup("EGI4-6");
        when(message.findChangeRequestOrThrow(reqId)).thenReturn(req);
        doNothing().when(message).ensureRequestPending(req);
        Group g = new Group();
        g.setGroupId("EGI4-6");
        g.setWaitlist(new ArrayList<>(List.of(1000100279, 1000100516)));
        when(groupRepository.findByGroupId("EGI4-6")).thenReturn(Optional.of(g));
        secretariatService.deleteRequestAsSecretariat(reqId);

        assertFalse(g.getWaitlist().contains(1000100279));
    }

    @Test
    void shouldGetRequestsByFacultyOrderedAndByPriorityAndAllVariants() {
        ChangeRequest a = new ChangeRequest(); a.setId(UUID.randomUUID());
        when(changeRequestRepository.findByFacultyOrderByPriorityAsc(Faculty.INGENIERIA_DE_SISTEMAS)).thenReturn(List.of(a));
        when(changeRequestMapper.toDTO(a)).thenReturn(new ChangeRequestDTO() {{ setId(a.getId()); }});
        var res1 = secretariatService.getRequestsByFacultyOrderedByPriority(Faculty.INGENIERIA_DE_SISTEMAS);
        assertEquals(1, res1.size());
        when(changeRequestRepository.findByFacultyAndPriorityOrderByPriorityAsc(Faculty.INGENIERIA_DE_SISTEMAS, 1)).thenReturn(List.of(a));
        var res2 = secretariatService.getRequestsByFacultyAndPriority(Faculty.INGENIERIA_DE_SISTEMAS, 1);
        assertEquals(1, res2.size());
        when(changeRequestRepository.findAllByOrderByPriorityAsc()).thenReturn(List.of(a));
        var all = secretariatService.getAllRequestsOrderedByPriority();
        assertEquals(1, all.size());
        when(changeRequestRepository.findByPriorityOrderByPriorityAsc(5)).thenReturn(List.of(a));
        var byPri = secretariatService.getAllRequestsByPriority(5);
        assertEquals(1, byPri.size());
        var search1 = secretariatService.searchRequestsByFacultyAndOrPriority(Faculty.INGENIERIA_DE_SISTEMAS, null);
        assertEquals(1, search1.size());
        var search2 = secretariatService.searchRequestsByFacultyAndOrPriority(null, 5);
        assertEquals(1, search2.size());
        var search3 = secretariatService.searchRequestsByFacultyAndOrPriority(null, null);
        assertEquals(1, search3.size());
    }

    @Test
    void shouldThrowWhenSecretariatNotFound_onGet() {
        when(message.findSecretariatOrThrow(6666)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe"));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> secretariatService.getSecretariatById(6666));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }


    @Test
    void shouldRejectRequestUpdate() {
        UUID reqId = UUID.randomUUID();
        ChangeRequest req = new ChangeRequest();
        req.setId(reqId);
        req.setStatus(RequestStatus.PENDING);
        when(message.findChangeRequestOrThrow(reqId)).thenReturn(req);
        RequestDecisionDTO decision = new RequestDecisionDTO();
        decision.setStatus(RequestStatus.REJECTED);
        decision.setObservations("No cumple");
        RequestDatesDTO rd = new RequestDatesDTO();
        when(changeRequestMapper.toDTO(req)).thenReturn(new ChangeRequestDTO() {{ setId(reqId); }});
        ChangeRequestDTO out = secretariatService.updateRequestAsSecretariat(reqId, decision, rd);

        assertNotNull(out);
        assertEquals(reqId, out.getId());
        assertEquals(RequestStatus.REJECTED, req.getStatus());
    }


    @Test
    void shouldApproveRequestUpdateAndRespond() {
        UUID reqId = UUID.randomUUID();
        ChangeRequest req = new ChangeRequest();
        req.setId(reqId);
        req.setStatus(RequestStatus.PENDING);
        req.setCurrentGroup("ODSC-1");
        req.setTargetGroup("ODSC-3");
        req.setStudentId(1000100282);
        when(message.findChangeRequestOrThrow(reqId)).thenReturn(req);
        doNothing().when(message).ensureRequestPending(req);
        Group current = new Group();
        current.setGroupId("ODSC-1");
        current.setWaitlist(new ArrayList<>(List.of(1000100282)));
        Group target = new Group();
        target.setGroupId("ODSC-3");
        target.setCurrentCapacity(2);
        target.setMaximumCapacity(30);
        target.setWaitlist(new ArrayList<>());
        when(message.findGroupOrThrow("ODSC-1")).thenReturn(current);
        when(message.findGroupOrThrow("ODSC-3")).thenReturn(target);
        doNothing().when(message).ensureGroupHasCapacity(target);
        RequestDecisionDTO decision = new RequestDecisionDTO();
        decision.setStatus(RequestStatus.APPROVED);
        RequestDatesDTO rd = new RequestDatesDTO();
        when(changeRequestRepository.save(req)).thenReturn(req);
        when(changeRequestMapper.toDTO(req)).thenReturn(new ChangeRequestDTO() {{ setId(reqId); }});
        ChangeRequestDTO out = secretariatService.updateRequestAsSecretariat(reqId, decision, rd);

        assertNotNull(out);
        assertEquals(reqId, out.getId());
        assertFalse(current.getWaitlist().contains(1000100282));
    }

    @Test
    void shouldUpdateSecretariatEmailOnly() {
        int id = 1000000398;
        Secretariat sec = new Secretariat();
        sec.setId(id);
        sec.setName("Old");
        sec.setEmail("old@e");
        when(message.findSecretariatOrThrow(id)).thenReturn(sec);
        SecretariatDTO dto = new SecretariatDTO();
        dto.setEmail("nuevo@e");
        when(secretariatRepository.save(sec)).thenReturn(sec);
        when(secretariatMapper.toDTO(sec)).thenReturn(new SecretariatDTO() {{ setId(id); setEmail("nuevo@e"); }});
        SecretariatDTO out = secretariatService.updateSecretariat(id, dto);

        assertNotNull(out);
        assertEquals("nuevo@e", out.getEmail());
    }

    @Test
    void shouldUpdateSecretariatBothDates() {
        int id = 1000000398;
        Secretariat sec = new Secretariat();
        sec.setId(id);
        when(message.findSecretariatOrThrow(id)).thenReturn(sec);
        LocalDateTime s = LocalDateTime.now().minusDays(2);
        LocalDateTime e = LocalDateTime.now().plusDays(2);
        SecretariatDTO dto = new SecretariatDTO();
        dto.setRequestStartDate(s);
        dto.setRequestEndDate(e);
        when(secretariatRepository.save(sec)).thenReturn(sec);
        when(secretariatMapper.toDTO(sec)).thenReturn(new SecretariatDTO() {{ setId(id); setRequestStartDate(s); setRequestEndDate(e); }});
        SecretariatDTO out = secretariatService.updateSecretariat(id, dto);

        assertNotNull(out);
        assertEquals(s, sec.getRequestStartDate());
        assertEquals(e, sec.getRequestEndDate());

    }

    @Test
    void shouldSearchRequestsFacultyAndPriority() {
        ChangeRequest a = new ChangeRequest(); a.setId(UUID.randomUUID());
        when(changeRequestRepository.findByFacultyAndPriorityOrderByPriorityAsc(Faculty.ECONOMIA, 2)).thenReturn(List.of(a));
        when(changeRequestMapper.toDTO(a)).thenReturn(new ChangeRequestDTO() {{ setId(a.getId()); }});
        var out = secretariatService.getRequestsByFacultyAndPriority(Faculty.ECONOMIA, 2);
        assertEquals(1, out.size());
        var searchBoth = secretariatService.searchRequestsByFacultyAndOrPriority(Faculty.ECONOMIA, 2);
        assertEquals(1, searchBoth.size());
    }

    @Test
    void shouldSearchRequestsPriorityOnly() {
        ChangeRequest a = new ChangeRequest(); a.setId(UUID.randomUUID());
        when(changeRequestRepository.findByPriorityOrderByPriorityAsc(5)).thenReturn(List.of(a));
        when(changeRequestMapper.toDTO(a)).thenReturn(new ChangeRequestDTO() {{ setId(a.getId()); }});
        var out = secretariatService.getAllRequestsByPriority(5);
        assertEquals(1, out.size());
        var search = secretariatService.searchRequestsByFacultyAndOrPriority(null, 5);
        assertEquals(1, search.size());
    }

    @Test
    void shouldSearchRequestsFacultyOnlyAndNone() {
        ChangeRequest a = new ChangeRequest(); a.setId(UUID.randomUUID());
        when(changeRequestRepository.findByFacultyOrderByPriorityAsc(Faculty.INGENIERIA_DE_SISTEMAS)).thenReturn(List.of(a));
        when(changeRequestMapper.toDTO(a)).thenReturn(new ChangeRequestDTO() {{ setId(a.getId()); }});
        var out = secretariatService.getRequestsByFacultyOrderedByPriority(Faculty.INGENIERIA_DE_SISTEMAS);
        assertEquals(1, out.size());
        var searchFacultyOnly = secretariatService.searchRequestsByFacultyAndOrPriority(Faculty.INGENIERIA_DE_SISTEMAS, null);
        assertEquals(1, searchFacultyOnly.size());
        when(changeRequestRepository.findAllByOrderByPriorityAsc()).thenReturn(List.of(a));
        var searchNone = secretariatService.searchRequestsByFacultyAndOrPriority(null, null);
        assertEquals(1, searchNone.size());
    }

}
