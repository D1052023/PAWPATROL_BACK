package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.enums.Curriculum;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.mappers.ChangeRequestMapper;
import eci.edu.dosw.proyecto.models.*;
import eci.edu.dosw.proyecto.repositories.*;
import eci.edu.dosw.proyecto.services.impl.ChangeRequestServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @InjectMocks
    private ChangeRequestServiceImpl changeRequestService;

    private Student student;
    private Subject subjectCurrent;
    private Subject subjectTarget;
    private Group groupCurrent;
    private Group groupTarget;
    private Secretariat secretariat;
    private ChangeRequestDTO requestDTO;
    private ChangeRequest changeRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        student = new Student();
        student.setId(1);
        student.setName("Juan");
        student.setCurriculum(Curriculum.ISIS_15);
        student.setRequests(new ArrayList<>());

        subjectCurrent = new Subject();
        subjectCurrent.setSubjectId("ODSC");
        subjectCurrent.setCurriculum(Curriculum.ISIS_15);

        subjectTarget = new Subject();
        subjectTarget.setSubjectId("DDYA");
        subjectTarget.setCurriculum(Curriculum.ISIS_15);

        groupCurrent = new Group();
        groupCurrent.setGroupId("ODSC-1");
        groupCurrent.setCurriculum(Curriculum.ISIS_15);

        groupTarget = new Group();
        groupTarget.setGroupId("DDYA-3");
        groupTarget.setCurriculum(Curriculum.ISIS_15);
        groupTarget.setWaitlist(new ArrayList<>());

        secretariat = new Secretariat();
        secretariat.setRequestStartDate(LocalDateTime.now().minusDays(1));
        secretariat.setRequestEndDate(LocalDateTime.now().plusDays(1));

        requestDTO = new ChangeRequestDTO();
        requestDTO.setCurrentSubject("ODSC");
        requestDTO.setTargetSubject("DDYA");
        requestDTO.setCurrentGroup("ODSC-1");
        requestDTO.setTargetGroup("DDYA-3");

        changeRequest = new ChangeRequest();
        changeRequest.setId(UUID.randomUUID());
        changeRequest.setStudentId(1);
        changeRequest.setStatus(RequestStatus.PENDING);
    }

    /**
    @Test
    void ShouldCreateChangeRequest() {
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(secretariatRepository.findFirstByRequestStartDateBeforeAndRequestEndDateAfter(any(), any())).thenReturn(Optional.of(secretariat));
        when(subjectRepository.findBySubjectId("ODSC")).thenReturn(Optional.of(subjectCurrent));
        when(subjectRepository.findBySubjectId("DDYA")).thenReturn(Optional.of(subjectTarget));
        when(groupRepository.findByGroupId("ODSC-1")).thenReturn(Optional.of(groupCurrent));
        when(groupRepository.findByGroupId("DDYA-3")).thenReturn(Optional.of(groupTarget));
        when(changeRequestMapper.toEntity(requestDTO)).thenReturn(changeRequest);
        when(changeRequestRepository.save(any(ChangeRequest.class))).thenReturn(changeRequest);
        when(changeRequestMapper.toDTO(changeRequest)).thenReturn(requestDTO);
        when(changeRequestRepository.findByStudentId(1)).thenReturn(new ArrayList<>());

        ChangeRequestDTO result = changeRequestService.createChangeRequest(1, requestDTO);

        assertNotNull(result);
        assertEquals("DDYA", result.getTargetSubject());
    }

     **/

    @Test
    void ShouldThrowWhenStudentNotFound() {
        when(studentRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> changeRequestService.createChangeRequest(1, requestDTO));
    }

    @Test
    void ShouldThrowWhenSecretariatNotActive() {
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(secretariatRepository.findFirstByRequestStartDateBeforeAndRequestEndDateAfter(any(), any())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> changeRequestService.createChangeRequest(1, requestDTO));
    }

    @Test
    void ShouldReturnAllRequestsByStudent() {
        when(studentRepository.existsById(1)).thenReturn(true);
        when(changeRequestRepository.findByStudentId(1)).thenReturn(List.of(changeRequest));
        when(changeRequestMapper.toDTOList(anyList())).thenReturn(List.of(requestDTO));

        List<ChangeRequestDTO> result = changeRequestService.getAllRequestsByStudent(1);

        assertEquals(1, result.size());
        assertEquals("DDYA", result.get(0).getTargetSubject());
    }

    @Test
    void ShouldThrowWhenStudentNotExistsForGetAll() {
        when(studentRepository.existsById(1)).thenReturn(false);
        assertThrows(ResponseStatusException.class, () -> changeRequestService.getAllRequestsByStudent(1));
    }

    @Test
    void ShouldReturnRequestById() {
        UUID reqId = changeRequest.getId();

        when(studentRepository.existsById(1)).thenReturn(true);
        when(changeRequestRepository.findById(reqId)).thenReturn(Optional.of(changeRequest));
        when(changeRequestMapper.toDTO(changeRequest)).thenReturn(requestDTO);

        ChangeRequestDTO result = changeRequestService.getRequestById(1, reqId);

        assertNotNull(result);
    }

    @Test
    void ShouldThroqWhenRequestConsultToCorrespondientStudent() {
        UUID reqId = changeRequest.getId();
        changeRequest.setStudentId(99);

        when(studentRepository.existsById(1)).thenReturn(true);
        when(changeRequestRepository.findById(reqId)).thenReturn(Optional.of(changeRequest));

        assertThrows(ResponseStatusException.class, () -> changeRequestService.getRequestById(1, reqId));
    }
}
