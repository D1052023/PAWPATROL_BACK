package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.dtos.ChangeRequestCreateDTO;
import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.ChangeRequestUpdateDTO;
import eci.edu.dosw.proyecto.dtos.ExceptionalRequestDTO;
import eci.edu.dosw.proyecto.enums.Curriculum;
import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.mappers.ChangeRequestMapper;
import eci.edu.dosw.proyecto.models.*;
import eci.edu.dosw.proyecto.repositories.*;
import eci.edu.dosw.proyecto.services.DeaneryService;
import eci.edu.dosw.proyecto.services.HistoryService;
import eci.edu.dosw.proyecto.services.impl.ChangeRequestServiceImpl;
import eci.edu.dosw.proyecto.util.CurriculumToFacultyMapper;
import eci.edu.dosw.proyecto.util.MessageExceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


import java.time.LocalDateTime;

import java.util.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ChangeRequestServiceImplTest {

    @Mock
    private ChangeRequestRepository changeRequestRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private ChangeRequestMapper changeRequestMapper;

    @Mock
    private SecretariatRepository secretariatRepository;

    @Mock
    private HistoryService historyService;

    @Mock
    private DeaneryRepository deaneryRepository;

    @Mock
    private DeaneryService deaneryService;

    @Mock
    private MessageExceptions message;

    @Mock
    private CurriculumToFacultyMapper curriculumToFacultyMapper;

    @InjectMocks
    private ChangeRequestServiceImpl changeRequestService;

    @Test
    void ShouldCreateChangeRequest() {

        Integer studentId = 1000100516;
        Student student = new Student();
        student.setId(studentId);
        student.setName("Juan Pablo Caballero");
        student.setEmail("juan.ccastellanos@mail.escuelaing.edu.co");
        student.setCurriculum(Curriculum.ISIS_14);
        student.setRequests(new ArrayList<>());

        Secretariat sec = new Secretariat();
        sec.setRequestStartDate(LocalDateTime.now().minusDays(1));
        sec.setRequestEndDate(LocalDateTime.now().plusDays(10));

        Subject current = new Subject();
        current.setSubjectId("DOSW");
        current.setCurriculum(Curriculum.ISIS_14);
        current.setCredits(3);

        Subject target  = new Subject();
        target.setSubjectId("TPYC");
        target.setCurriculum(Curriculum.ISIS_14);
        target.setCredits(4);

        Group gCurrent = new Group();
        gCurrent.setGroupId("DOSW-1");

        Group gTarget  = new Group();
        gTarget.setGroupId("TPYC-1");
        gTarget.setWaitlist(new ArrayList<>());

        ChangeRequestCreateDTO dto = new ChangeRequestCreateDTO();
        dto.setCurrentSubject("DOSW");
        dto.setTargetSubject("TPYC");
        dto.setCurrentGroup("DOSW-1");
        dto.setTargetGroup("TPYC-1");
        dto.setStudentName("Juan Pablo Caballero");

        ChangeRequest entityToSave = new ChangeRequest();

        when(message.findStudentOrThrow(studentId)).thenReturn(student);
        when(message.findActiveSecretariatOrThrow(any())).thenReturn(sec);
        when(message.findSubjectOrThrow("DOSW")).thenReturn(current);
        when(message.findSubjectOrThrow("TPYC")).thenReturn(target);
        when(message.findGroupOrThrow("DOSW-1")).thenReturn(gCurrent);
        when(message.findGroupOrThrow("TPYC-1")).thenReturn(gTarget);
        when(changeRequestMapper.toEntity(any(ChangeRequestDTO.class))).thenReturn(entityToSave);
        when(changeRequestRepository.findByStudentId(studentId)).thenReturn(Collections.emptyList());
        when(changeRequestRepository.save(any(ChangeRequest.class))).thenAnswer(i -> i.getArgument(0));
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));
        when(changeRequestMapper.toDTO(any(ChangeRequest.class))).thenReturn(new ChangeRequestDTO(){{ setId(UUID.randomUUID()); }});
        when(curriculumToFacultyMapper.map(Curriculum.ISIS_14)).thenReturn(Faculty.INGENIERIA_DE_SISTEMAS);
        ChangeRequestDTO result = changeRequestService.createChangeRequest(studentId, dto);

        assertNotNull(result);
        assertTrue(gTarget.getWaitlist().contains(studentId));
    }

    @Test
    void ShouldGetAllRequestsByStudent() {
        Integer studentId = 1000100575;
        when(message.findStudentOrThrow(studentId)).thenReturn(new Student());
        ChangeRequest r1 = new ChangeRequest(); r1.setId(UUID.randomUUID());
        when(changeRequestRepository.findByStudentId(studentId)).thenReturn(List.of(r1));
        when(changeRequestMapper.toDTOList(anyList())).thenReturn(List.of(new ChangeRequestDTO(){{setId(r1.getId());}}));
        List<ChangeRequestDTO> out = changeRequestService.getAllRequestsByStudent(studentId);

        assertEquals(1, out.size());
    }

    @Test
    void ShouldGetRequestByIdOwner() {
        Integer studentId = 1000100667;
        UUID reqId = UUID.randomUUID();

        when(message.findStudentOrThrow(studentId)).thenReturn(new Student());
        ChangeRequest r = new ChangeRequest(); r.setId(reqId); r.setStudentId(studentId);

        when(message.findChangeRequestOrThrow(reqId)).thenReturn(r);
        when(changeRequestMapper.toDTO(r)).thenReturn(new ChangeRequestDTO(){ { setId(reqId); } });
        ChangeRequestDTO res = changeRequestService.getRequestById(studentId, reqId);

        assertNotNull(res);
        assertEquals(reqId, res.getId());
    }

    @Test
    void ShouldGetRequestByIdNotOwnerThrows() {
        Integer studentId = 1000100282;
        UUID reqId = UUID.randomUUID();

        when(message.findStudentOrThrow(studentId)).thenReturn(new Student());
        ChangeRequest r = new ChangeRequest(); r.setId(reqId); r.setStudentId(99999);

        when(message.findChangeRequestOrThrow(reqId)).thenReturn(r);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> changeRequestService.getRequestById(studentId, reqId));
        String reason = ex.getReason();

        assertNotNull(reason, "Se esperaba que la excepción tuviera 'reason'");
        assertTrue(reason.contains("Esta solicitud no pertenece"));
    }

    @Test
    void ShouldUpdateChangeRequestUpdateTargetGroup() {
        Integer studentId = 1000100516;
        UUID reqId = UUID.randomUUID();
        Student student = new Student();
        student.setId(studentId);

        ChangeRequest request = new ChangeRequest();
        request.setId(reqId);
        request.setStudentId(studentId);
        request.setStatus(RequestStatus.PENDING);
        request.setTargetGroup("DOSW-2");

        Group oldG = new Group(); oldG.setGroupId("DOSW-2"); oldG.setWaitlist(new ArrayList<>(List.of(studentId)));
        Group newG = new Group(); newG.setGroupId("PRI2IS-3"); newG.setWaitlist(new ArrayList<>());
        ChangeRequestUpdateDTO incoming = new ChangeRequestUpdateDTO();

        incoming.setTargetGroup("PRI2IS-3");
        when(message.findStudentOrThrow(studentId)).thenReturn(student);
        when(message.findChangeRequestOrThrow(reqId)).thenReturn(request);
        when(message.findGroupOrThrow("PRI2IS-3")).thenReturn(newG);
        when(groupRepository.findByGroupId("DOSW-2")).thenReturn(Optional.of(oldG));
        when(changeRequestRepository.save(any(ChangeRequest.class))).thenAnswer(i -> i.getArgument(0));
        when(changeRequestMapper.toDTO(any(ChangeRequest.class))).thenReturn(new ChangeRequestDTO(){ { setId(reqId); } });
        ChangeRequestDTO out = changeRequestService.updateChangeRequest(studentId, reqId, incoming);

        assertNotNull(out);
        assertEquals(reqId, out.getId());
        assertFalse(oldG.getWaitlist().contains(studentId));
        assertTrue(newG.getWaitlist().contains(studentId));
    }

    @Test
    void ShouldDeleteChangeRequest() {
        Integer studentId = 1000100279;
        UUID reqId = UUID.randomUUID();

        Student student = new Student();
        student.setId(studentId);

        ChangeRequest req = new ChangeRequest();
        req.setId(reqId);
        req.setStudentId(studentId);
        req.setStatus(RequestStatus.PENDING);
        req.setTargetGroup("ODSC-3");
        student.setRequests(new ArrayList<>(List.of(req)));

        when(message.findStudentOrThrow(studentId)).thenReturn(student);
        when(message.findChangeRequestOrThrow(reqId)).thenReturn(req);
        when(groupRepository.findByGroupId("ODSC-3")).thenReturn(Optional.of(new Group(){{
            setGroupId("ODSC-3");
            setWaitlist(new ArrayList<>(List.of(studentId)));
        }}));
        changeRequestService.deleteChangeRequest(studentId, reqId);

        assertTrue(student.getRequests().isEmpty());
    }

    @Test
    void ShouldRequestExceptionalReview() {
        Integer studentId = 1000100667;
        UUID reqId = UUID.randomUUID();

        Student student = new Student(); student.setId(studentId);
        ChangeRequest req = new ChangeRequest(); req.setId(reqId); req.setStudentId(studentId);

        when(message.findStudentOrThrow(studentId)).thenReturn(student);
        when(message.findChangeRequestOrThrow(reqId)).thenReturn(req);
        when(changeRequestRepository.save(any(ChangeRequest.class))).thenAnswer(i -> i.getArgument(0));
        when(changeRequestMapper.toDTO(any(ChangeRequest.class))).thenReturn(new ChangeRequestDTO(){ { setId(reqId); } });
        ChangeRequestDTO out = changeRequestService.requestExceptionalReview(studentId, reqId, "Motivo excepcional");

        assertNotNull(out);
        assertTrue(req.isExceptional());
        assertNotNull(req.getExceptionalResolutionDeadline());
    }

    @Test
    void ShouldGetExceptionalRequestsByDeaneryAndByStudent() {
        int deaneryId = 1000000451;
        Deanery dean = new Deanery(); dean.setId(deaneryId); dean.setFaculty(Faculty.INGENIERIA_DE_SISTEMAS);
        ChangeRequest cr1 = new ChangeRequest(); cr1.setId(UUID.randomUUID()); cr1.setFaculty(Faculty.INGENIERIA_DE_SISTEMAS); cr1.setExceptional(true);

        when(message.findDeaneryOrThrow(deaneryId)).thenReturn(dean);
        when(changeRequestRepository.findByFacultyAndExceptionalTrue(dean.getFaculty())).thenReturn(List.of(cr1));
        when(changeRequestMapper.toDTO(cr1)).thenReturn(new ChangeRequestDTO(){ { setId(cr1.getId()); } });
        List<ChangeRequestDTO> outDeanery = changeRequestService.getExceptionalRequestsByDeanery(deaneryId);

        assertEquals(1, outDeanery.size());

        int studentId = 1000100282;
        when(message.findStudentOrThrow(studentId)).thenReturn(new Student());
        ChangeRequest cr2 = new ChangeRequest(); cr2.setId(UUID.randomUUID()); cr2.setExceptional(true);

        when(changeRequestRepository.findByStudentIdAndExceptionalTrue(studentId)).thenReturn(List.of(cr2));
        when(changeRequestMapper.toDTO(cr2)).thenReturn(new ChangeRequestDTO(){ { setId(cr2.getId()); } });
        List<ChangeRequestDTO> outStudent = changeRequestService.getExceptionalRequestsByStudent(studentId);

        assertEquals(1, outStudent.size());
    }

    @Test
    void ShouldGetAllExceptionalRequests() {
        ChangeRequest cr = new ChangeRequest(); cr.setId(UUID.randomUUID()); cr.setExceptional(true);

        when(changeRequestRepository.findByExceptionalTrue()).thenReturn(List.of(cr));
        when(changeRequestMapper.toDTO(cr)).thenReturn(new ChangeRequestDTO(){ { setId(cr.getId()); } });
        List<ChangeRequestDTO> out = changeRequestService.getAllExceptionalRequests();

        assertEquals(1, out.size());
    }

    @Test
    void ShouldApproveExceptionalRequestApproveAndProcess() {
        int approverId = 1000000451;
        Deanery dean = new Deanery(); dean.setId(approverId); dean.setFaculty(Faculty.INGENIERIA_DE_SISTEMAS);
        ChangeRequest req = new ChangeRequest();
        req.setId(UUID.randomUUID());
        req.setExceptional(true);
        req.setFaculty(Faculty.INGENIERIA_DE_SISTEMAS);

        when(deaneryRepository.findById(approverId)).thenReturn(Optional.of(dean));
        when(message.findChangeRequestOrThrow(req.getId())).thenReturn(req);

        when(changeRequestRepository.save(any(ChangeRequest.class))).thenAnswer(i -> i.getArgument(0));
        when(changeRequestMapper.toDTO(any(ChangeRequest.class))).thenReturn(new ChangeRequestDTO(){ { setId(req.getId()); } });
        ChangeRequestDTO out = changeRequestService.approveExceptionalRequest(approverId, req.getId(), true, "OK");

        assertEquals(req.getId(), out.getId());
        assertTrue(req.getExceptionalApproved());
        assertEquals(RequestStatus.APPROVED, req.getStatus());
    }

    @Test
    void ShouldApproveExceptionalRequestReject() {
        int approverId = 77777;
        when(deaneryRepository.findById(approverId)).thenReturn(Optional.empty());

        ChangeRequest req = new ChangeRequest();
        req.setId(UUID.randomUUID());
        req.setExceptional(true);
        req.setFaculty(null);

        when(message.findChangeRequestOrThrow(req.getId())).thenReturn(req);
        when(changeRequestRepository.save(any(ChangeRequest.class))).thenAnswer(i -> i.getArgument(0));
        when(changeRequestMapper.toDTO(any(ChangeRequest.class))).thenReturn(new ChangeRequestDTO(){ { setId(req.getId()); } });
        ChangeRequestDTO out = changeRequestService.approveExceptionalRequest(approverId, req.getId(), false, "No procede");

        assertEquals(req.getId(), out.getId());
        assertFalse(req.getExceptionalApproved());
        assertEquals(RequestStatus.REJECTED, req.getStatus());
    }

    @Test
    void ShouldGetExceptionalRequestsByStudentForDeaneryFiltered() {
        int deaneryId = 1000100692;
        Integer studentId = 1000100282;

        Deanery dean = new Deanery();
        dean.setId(deaneryId);
        dean.setFaculty(Faculty.ECONOMIA);

        when(message.findDeaneryOrThrow(deaneryId)).thenReturn(dean);
        when(message.findStudentOrThrow(studentId)).thenReturn(new Student());

        ChangeRequest matching = new ChangeRequest();
        matching.setId(UUID.randomUUID());
        matching.setStudentId(studentId);
        matching.setExceptional(true);
        matching.setFaculty(Faculty.ECONOMIA);
        ChangeRequest other = new ChangeRequest();
        other.setId(UUID.randomUUID());
        other.setStudentId(studentId);
        other.setExceptional(true);
        other.setFaculty(Faculty.ADMINISTRACION_DE_EMPRESAS);

        when(changeRequestRepository.findByStudentIdAndExceptionalTrue(studentId)).thenReturn(List.of(matching, other));
        when(changeRequestMapper.toDTO(matching)).thenReturn(new ChangeRequestDTO() {{ setId(matching.getId()); }});
        List<ChangeRequestDTO> out = changeRequestService.getExceptionalRequestsByStudentForDeanery(deaneryId, studentId);

        assertEquals(1, out.size());
        assertEquals(matching.getId(), out.get(0).getId());
    }

    @Test
    void shouldUpdatesFacultyAndSubject() {
        Integer studentId = 1000100575;
        UUID reqId = UUID.randomUUID();

        Student student = new Student();
        student.setId(studentId);

        ChangeRequest request = new ChangeRequest();
        request.setId(reqId);
        request.setStudentId(studentId);
        request.setStatus(RequestStatus.PENDING);
        request.setTargetSubject("FUPR");
        request.setFaculty(Faculty.ADMINISTRACION_DE_EMPRESAS);

        ChangeRequestUpdateDTO dto = new ChangeRequestUpdateDTO();
        dto.setTargetSubject("FUEC");

        Subject newSubject = new Subject();
        newSubject.setSubjectId("FUEC");
        newSubject.setCurriculum(Curriculum.ISIS_14);

        when(message.findStudentOrThrow(studentId)).thenReturn(student);
        when(message.findChangeRequestOrThrow(reqId)).thenReturn(request);
        when(message.findSubjectOrThrow("FUEC")).thenReturn(newSubject);
        when(curriculumToFacultyMapper.map(newSubject.getCurriculum())).thenReturn(Faculty.INGENIERIA_DE_SISTEMAS);
        when(changeRequestRepository.save(any(ChangeRequest.class))).thenAnswer(i -> i.getArgument(0));
        when(changeRequestMapper.toDTO(any(ChangeRequest.class))).thenReturn(new ChangeRequestDTO(){ { setId(reqId); } });
        ChangeRequestDTO out = changeRequestService.updateChangeRequest(studentId, reqId, dto);

        assertNotNull(out);
        assertEquals(reqId, out.getId());
        assertEquals("FUEC", request.getTargetSubject());
        assertEquals(Faculty.INGENIERIA_DE_SISTEMAS, request.getFaculty());
    }

    @Test
    void shouldRemovesFromOldWaitlistAndAddsToNew() {
        Integer studentId = 1000100279;
        UUID reqId = UUID.randomUUID();

        Student student = new Student(); student.setId(studentId);
        ChangeRequest request = new ChangeRequest();
        request.setId(reqId);
        request.setStudentId(studentId);
        request.setStatus(RequestStatus.PENDING);
        request.setTargetGroup("DOSW-1");

        ChangeRequestUpdateDTO dto = new ChangeRequestUpdateDTO();
        dto.setTargetGroup("DOSW-2");

        Group oldG = new Group();
        oldG.setGroupId("DOSW-1");
        oldG.setWaitlist(new ArrayList<>(List.of(studentId)));

        Group newG = new Group();
        newG.setGroupId("DOSW-2");
        newG.setWaitlist(null);

        when(message.findStudentOrThrow(studentId)).thenReturn(student);
        when(message.findChangeRequestOrThrow(reqId)).thenReturn(request);
        when(message.findGroupOrThrow("DOSW-2")).thenReturn(newG);
        when(groupRepository.findByGroupId("DOSW-1")).thenReturn(Optional.of(oldG));
        when(changeRequestRepository.save(any(ChangeRequest.class))).thenAnswer(i -> i.getArgument(0));
        when(changeRequestMapper.toDTO(any(ChangeRequest.class))).thenReturn(new ChangeRequestDTO(){ { setId(reqId); } });
        ChangeRequestDTO out = changeRequestService.updateChangeRequest(studentId, reqId, dto);

        assertNotNull(out);
        assertEquals(reqId, out.getId());
        assertFalse(oldG.getWaitlist().contains(studentId));
        assertNotNull(newG.getWaitlist());
        assertTrue(newG.getWaitlist().contains(studentId));
    }

    @Test
    void shouldUpdatesBothAndSavesGroupsAndRequest() {
        Integer studentId = 1000100492;
        UUID reqId = UUID.randomUUID();

        Student student = new Student();
        student.setId(studentId);

        ChangeRequest request = new ChangeRequest();
        request.setId(reqId);
        request.setStudentId(studentId);
        request.setStatus(RequestStatus.PENDING);
        request.setTargetGroup("DOSW-2");
        request.setTargetSubject("DOSW-2");

        ChangeRequestUpdateDTO dto = new ChangeRequestUpdateDTO();
        dto.setTargetSubject("DOSW");
        dto.setTargetGroup("DOSW-1");

        Subject newSubject = new Subject();
        newSubject.setSubjectId("DOSW");
        newSubject.setCurriculum(Curriculum.ISIS_14);

        Group oldG = new Group();
        oldG.setGroupId("DOSW-2");
        oldG.setWaitlist(new ArrayList<>(List.of(studentId)));

        Group newG = new Group();
        newG.setGroupId("DOSW-1");
        newG.setWaitlist(new ArrayList<>());

        when(message.findStudentOrThrow(studentId)).thenReturn(student);
        when(message.findChangeRequestOrThrow(reqId)).thenReturn(request);
        when(message.findSubjectOrThrow("DOSW")).thenReturn(newSubject);
        when(curriculumToFacultyMapper.map(newSubject.getCurriculum())).thenReturn(Faculty.ECONOMIA);
        when(message.findGroupOrThrow("DOSW-1")).thenReturn(newG);
        when(groupRepository.findByGroupId("DOSW-2")).thenReturn(Optional.of(oldG));
        when(changeRequestRepository.save(any(ChangeRequest.class))).thenAnswer(i -> i.getArgument(0));
        when(groupRepository.save(any(Group.class))).thenAnswer(i -> i.getArgument(0));
        when(changeRequestMapper.toDTO(any(ChangeRequest.class))).thenReturn(new ChangeRequestDTO(){ { setId(reqId); } });
        ChangeRequestDTO out = changeRequestService.updateChangeRequest(studentId, reqId, dto);

        assertNotNull(out);
        assertEquals(reqId, out.getId());
        assertEquals("DOSW", request.getTargetSubject());
        assertEquals(Faculty.ECONOMIA, request.getFaculty());
        assertFalse(oldG.getWaitlist().contains(studentId));
        assertTrue(newG.getWaitlist().contains(studentId));
    }


    @Test
    void shouldUpdatesAndSavesRequest() {
        Integer studentId = 1000100279;
        UUID reqId = UUID.randomUUID();

        Student student = new Student(); student.setId(studentId);
        ChangeRequest request = new ChangeRequest();
        request.setId(reqId);
        request.setStudentId(studentId);
        request.setStatus(RequestStatus.PENDING);

        ChangeRequestUpdateDTO dto = new ChangeRequestUpdateDTO();
        dto.setObservations("Nueva observación");

        when(message.findStudentOrThrow(studentId)).thenReturn(student);
        when(message.findChangeRequestOrThrow(reqId)).thenReturn(request);
        when(changeRequestRepository.save(any(ChangeRequest.class))).thenAnswer(i -> i.getArgument(0));
        when(changeRequestMapper.toDTO(any(ChangeRequest.class))).thenReturn(new ChangeRequestDTO(){ { setId(reqId); } });
        ChangeRequestDTO out = changeRequestService.updateChangeRequest(studentId, reqId, dto);

        assertNotNull(out);
        assertEquals(reqId, out.getId());
        assertEquals("Nueva observación", request.getObservations());
    }

    @Test
    void shouldCreateChangeRequestWhenStudentRequestsNullAndTargetWaitlistNull() {
        Integer studentId = 1000100575;

        Student student = new Student();
        student.setId(studentId);
        student.setName("Test Student");
        student.setEmail("s@test.edu");
        student.setCurriculum(Curriculum.ISIS_14);

        Secretariat sec = new Secretariat();
        sec.setRequestStartDate(LocalDateTime.now().minusDays(1));
        sec.setRequestEndDate(LocalDateTime.now().plusDays(10));

        Subject current = new Subject();
        current.setSubjectId("DOSW");
        current.setCurriculum(Curriculum.ISIS_14);
        current.setCredits(3);

        Subject target = new Subject();
        target.setSubjectId("TPYC");
        target.setCurriculum(Curriculum.ISIS_14);
        target.setCredits(4);

        Group gCurrent = new Group(); gCurrent.setGroupId("DOSW-1");
        Group gTarget = new Group(); gTarget.setGroupId("TPYC-1");

        ChangeRequestCreateDTO dto = new ChangeRequestCreateDTO();
        dto.setCurrentSubject("DOSW");
        dto.setTargetSubject("TPYC");
        dto.setCurrentGroup("DOSW-1");
        dto.setTargetGroup("TPYC-1");
        
        when(message.findStudentOrThrow(studentId)).thenReturn(student);
        when(message.findActiveSecretariatOrThrow(any())).thenReturn(sec);
        when(message.findSubjectOrThrow("DOSW")).thenReturn(current);
        when(message.findSubjectOrThrow("TPYC")).thenReturn(target);
        when(message.findGroupOrThrow("DOSW-1")).thenReturn(gCurrent);
        when(message.findGroupOrThrow("TPYC-1")).thenReturn(gTarget);
        when(message.findActiveSecretariatOrThrow(ArgumentMatchers.any(LocalDateTime.class))).thenReturn(sec);
        when(changeRequestRepository.findByStudentId(studentId)).thenReturn(Collections.emptyList());
        when(changeRequestRepository.save(any(ChangeRequest.class))).thenAnswer(i -> i.getArgument(0));
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));
        when(changeRequestMapper.toDTO(any(ChangeRequest.class))).thenReturn(new ChangeRequestDTO(){{ setId(UUID.randomUUID()); }});
        when(curriculumToFacultyMapper.map(target.getCurriculum())).thenReturn(Faculty.INGENIERIA_DE_SISTEMAS);
        ChangeRequestDTO result = changeRequestService.createChangeRequest(studentId, dto);

        assertNotNull(result);
        assertNotNull(gTarget.getWaitlist());
        assertTrue(gTarget.getWaitlist().contains(studentId));
    }

    @Test
    void ShouldCreateExceptionalChangeRequest() {
        Integer studentId = 1000100516;
        Student student = new Student();
        student.setId(studentId);
        student.setName("Juan Pablo Caballero");
        student.setCurriculum(Curriculum.ISIS_14);
        student.setRequests(new ArrayList<>());

        Secretariat sec = new Secretariat();
        sec.setRequestStartDate(LocalDateTime.now().minusDays(1));
        sec.setRequestEndDate(LocalDateTime.now().plusDays(10));

        Subject current = new Subject();
        current.setSubjectId("DOSW");
        current.setCurriculum(Curriculum.ISIS_14);
        current.setCredits(3);

        Subject target = new Subject();
        target.setSubjectId("TPYC");
        target.setCurriculum(Curriculum.ISIS_14);
        target.setCredits(4);

        Group gCurrent = new Group(); gCurrent.setGroupId("DOSW-1");
        Group gTarget = new Group(); gTarget.setGroupId("TPYC-1");
        gTarget.setWaitlist(new ArrayList<>());

        ChangeRequestCreateDTO dto = new ChangeRequestCreateDTO();
        dto.setCurrentSubject("DOSW");
        dto.setTargetSubject("TPYC");
        dto.setCurrentGroup("DOSW-1");
        dto.setTargetGroup("TPYC-1");
        dto.setExceptional(true);

        when(message.findStudentOrThrow(studentId)).thenReturn(student);
        when(message.findActiveSecretariatOrThrow(any())).thenReturn(sec);
        when(message.findSubjectOrThrow("DOSW")).thenReturn(current);
        when(message.findSubjectOrThrow("TPYC")).thenReturn(target);
        when(message.findGroupOrThrow("DOSW-1")).thenReturn(gCurrent);
        when(message.findGroupOrThrow("TPYC-1")).thenReturn(gTarget);
        when(changeRequestRepository.findByStudentId(studentId)).thenReturn(Collections.emptyList());
        when(changeRequestRepository.save(any(ChangeRequest.class))).thenAnswer(i -> i.getArgument(0));
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));
        when(changeRequestMapper.toDTO(any(ChangeRequest.class))).thenReturn(new ChangeRequestDTO(){{ setId(UUID.randomUUID()); }});
        when(curriculumToFacultyMapper.map(Curriculum.ISIS_14)).thenReturn(Faculty.INGENIERIA_DE_SISTEMAS);

        ChangeRequestDTO result = changeRequestService.createChangeRequest(studentId, dto);

        assertNotNull(result);
        assertTrue(gTarget.getWaitlist().contains(studentId));
        assertTrue(student.getRequests().size() > 0);

        ChangeRequest saved = student.getRequests().get(0);

        assertTrue(saved.isExceptional());
        assertNotNull(saved.getExceptionalRequestedAt());
        assertNotNull(saved.getExceptionalResolutionDeadline());
    }

    @Test
    void ShouldRequestExceptionalSuccessfully() {
        Integer studentId = 1000100516;
        UUID requestId = UUID.randomUUID();

        ChangeRequest request = new ChangeRequest();
        request.setId(requestId);
        request.setStudentId(studentId);
        request.setStatus(RequestStatus.PENDING);

        ExceptionalRequestDTO dto = new ExceptionalRequestDTO();
        dto.setReason("Medical emergency – needs schedule flexibility.");

        when(message.findChangeRequestOrThrow(requestId)).thenReturn(request);
        when(changeRequestRepository.save(any(ChangeRequest.class))).thenAnswer(i -> i.getArgument(0));
        when(changeRequestMapper.toDTO(any(ChangeRequest.class))).thenReturn(new ChangeRequestDTO() {{
            setId(requestId);
        }});

        ChangeRequestDTO result = changeRequestService.requestExceptional(studentId, requestId, dto);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertTrue(request.isExceptional());
        assertEquals("Medical emergency – needs schedule flexibility.", request.getExceptionalReason());
        assertNotNull(request.getExceptionalRequestedAt());
        assertNotNull(request.getExceptionalResolutionDeadline());
        assertEquals("STUDENT:" + studentId, request.getExceptionalRequestedBy());
    }


}