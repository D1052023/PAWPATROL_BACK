package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.dtos.*;
import eci.edu.dosw.proyecto.enums.Curriculum;
import eci.edu.dosw.proyecto.enums.GroupStatus;
import eci.edu.dosw.proyecto.mappers.*;
import eci.edu.dosw.proyecto.models.*;
import eci.edu.dosw.proyecto.repositories.*;
import eci.edu.dosw.proyecto.services.*;
import eci.edu.dosw.proyecto.services.impl.GroupServiceImpl;
import eci.edu.dosw.proyecto.util.MessageExceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.mongodb.core.MongoTemplate;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GroupServiceImplTest {

    @Mock 
    private GroupRepository groupRepository;
    @Mock 
    private MongoTemplate mongoTemplate;
    @Mock 
    private AlertService alertService;
    @Mock 
    private GroupMapper groupMapper;
    @Mock 
    private ScheduleEntryMapper scheduleEntryMapper;
    @Mock 
    private MessageExceptions message;
    @Mock 
    private StudentRepository studentRepository;
    @Mock 
    private StudentService studentService;
    @Mock 
    private HistoryService historyService;

    @InjectMocks 
    private GroupServiceImpl groupService;

    private Group group;
    private GroupDTO groupDTO;
    private Student student;
    private StudentDTO studentDTO;
    private ScheduleEntryDTO scheduleEntryDTO;
    private ScheduleEntry scheduleEntry;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        group = new Group();
        group.setGroupId("DDYA-4");
        group.setName("Diseño de estructuras y algoritmos");
        group.setMaximumCapacity(10);
        group.setCurrentCapacity(5);
        group.setSubjectId("DDYA");
        group.setWaitlist(new ArrayList<>(List.of(1)));
        group.setSchedule(new ArrayList<>());

        groupDTO = new GroupDTO();
        groupDTO.setGroupId("DDYA-4");
        groupDTO.setName("Diseño de estructuras y algoritmos");
        groupDTO.setMaximumCapacity(10);
        groupDTO.setCurrentCapacity(5);
        groupDTO.setSubjectId("DDYA");

        student = new Student();
        student.setId(1);
        student.setSchedule(new ArrayList<>());
        student.setEnrolledSubjects(new ArrayList<>());

        studentDTO = new StudentDTO();
        studentDTO.setId(1);

        scheduleEntryDTO = new ScheduleEntryDTO();
        scheduleEntryDTO.setDay("Monday");
        scheduleEntryDTO.setFrom("08:00");
        scheduleEntryDTO.setTo("10:00");

        scheduleEntry = new ScheduleEntry();
        scheduleEntry.setDay("Monday");
        scheduleEntry.setFrom("08:00");
        scheduleEntry.setTo("10:00");

        when(groupMapper.toDTO(any(Group.class))).thenAnswer(invocation -> {
            Group g = invocation.getArgument(0);
            GroupDTO dto = new GroupDTO();
            dto.setGroupId(g.getGroupId());
            dto.setName(g.getName());
            dto.setMaximumCapacity(g.getMaximumCapacity());
            dto.setCurrentCapacity(g.getCurrentCapacity());
            dto.setSubjectId(g.getSubjectId());
            return dto;
        });

        when(groupMapper.toDTO(any(Group.class))).thenAnswer(invocation -> {
            Group g = invocation.getArgument(0);
            GroupDTO dto = new GroupDTO();
            dto.setGroupId(g.getGroupId());
            dto.setName(g.getName());
            dto.setMaximumCapacity(g.getMaximumCapacity());
            dto.setCurrentCapacity(g.getCurrentCapacity());
            dto.setSubjectId(g.getSubjectId());
            dto.setCurriculum(g.getCurriculum()); 
            dto.setSchedule(scheduleEntryMapper.toDTOList(g.getSchedule()));
            return dto;
        });


        when(scheduleEntryMapper.toDTOList(anyList())).thenAnswer(invocation -> {
            List<ScheduleEntry> entries = invocation.getArgument(0);
            List<ScheduleEntryDTO> dtos = new ArrayList<>();
            for (ScheduleEntry e : entries) {
                ScheduleEntryDTO dto = new ScheduleEntryDTO();
                dto.setDay(e.getDay());
                dto.setFrom(e.getFrom());
                dto.setTo(e.getTo());
                dtos.add(dto);
            }
            return dtos;
        });

        when(scheduleEntryMapper.toDTO(any(ScheduleEntry.class))).thenReturn(scheduleEntryDTO);
        when(scheduleEntryMapper.toModel(scheduleEntryDTO)).thenReturn(scheduleEntry);
        when(scheduleEntryMapper.toModelList(anyList())).thenAnswer(invocation -> {
            List<ScheduleEntryDTO> dtos = invocation.getArgument(0);
            List<ScheduleEntry> list = new ArrayList<>();
            for (ScheduleEntryDTO dto : dtos) {
                ScheduleEntry e = new ScheduleEntry();
                e.setDay(dto.getDay());
                e.setFrom(dto.getFrom());
                e.setTo(dto.getTo());
                list.add(e);
            }
            return list;
        });
    }

    @Test
    void ShouldCreateGroup() {
        Subject subject = new Subject();
        subject.setSubjectId("DDYA");
        subject.setMaximumCapacity(50);

        Group groupModel = new Group();
        groupModel.setGroupId("DDYA-4");
        groupModel.setSubjectId("DDYA");
        groupModel.setMaximumCapacity(10);

        Group savedGroup = new Group();
        savedGroup.setGroupId("DDYA-4");
        savedGroup.setSubjectId("DDYA");
        savedGroup.setMaximumCapacity(10);

        when(groupMapper.toModel(groupDTO)).thenReturn(groupModel);
        when(message.findSubjectOrThrow(groupModel.getSubjectId())).thenReturn(subject);
        when(groupRepository.findBySubjectId("DDYA")).thenReturn(Collections.emptyList());
        when(groupRepository.save(any(Group.class))).thenReturn(savedGroup);
        when(groupMapper.toDTO(savedGroup)).thenReturn(groupDTO);

        GroupDTO result = groupService.createGroup(groupDTO);

        assertNotNull(result);
        assertEquals("DDYA-4", result.getGroupId());

    }

    @Test
    void ShouldReturnAllGroups() {
        Group group1 = new Group();
        group1.setGroupId("DDYA-1");
        group1.setSubjectId("DDYA");

        Group group2 = new Group();
        group2.setGroupId("ODSC-2");
        group2.setSubjectId("ODSC");

        List<Group> groups = List.of(group1, group2);
        List<GroupDTO> groupDTOs = List.of(groupDTO, groupDTO);

        when(groupRepository.findAll()).thenReturn(groups);
        when(groupMapper.toDTO(group1)).thenReturn(groupDTOs.get(0));
        when(groupMapper.toDTO(group2)).thenReturn(groupDTOs.get(1));

        List<GroupDTO> result = groupService.getAllGroups();

        assertNotNull(result);
        assertEquals(2, result.size());
    }


    @Test
    void ShouldGetWaitlistAndDetails() {
        when(message.findGroupOrThrow("DDYA-4")).thenReturn(group);
        when(studentService.getStudentById(1)).thenReturn(studentDTO);

        List<Integer> waitlist = groupService.getWaitlist("DDYA-4");
        assertEquals(List.of(1), waitlist);

        List<StudentDTO> details = groupService.getWaitlistDetails("DDYA-4");
        assertEquals(1, details.size());
        assertEquals(1, details.get(0).getId());
    }

    @Test
    void ShouldAddAndGetSchedule() {
        when(message.findGroupOrThrow("DDYA-4")).thenReturn(group);
        when(groupRepository.save(group)).thenReturn(group);

        ScheduleEntryDTO result = groupService.addScheduleEntry("DDYA-4", scheduleEntryDTO);
        assertNotNull(result);
        assertEquals("Monday", result.getDay());

        List<ScheduleEntryDTO> schedule = groupService.getSchedule("DDYA-4");
        assertEquals(1, schedule.size());
    }

    @Test
    void ShouldReturnGroupById() {
        group.setGroupId("DDYA-4");
        group.setSubjectId("DDYA");

        when(message.findGroupOrThrow("DDYA-4")).thenReturn(group);
        when(groupMapper.toDTO(group)).thenReturn(groupDTO);

        GroupDTO result = groupService.getGroupById("DDYA-4");

        assertNotNull(result);
    }

    @Test
    void ShouldUpdateScheduleGlobalAndForDay() {
        when(message.findGroupOrThrow("DDYA-4")).thenReturn(group);
        when(groupRepository.save(group)).thenReturn(group);

        List<ScheduleEntryDTO> globalUpdated = groupService.updateScheduleGlobal("DDYA-4",
                new ArrayList<>(List.of(scheduleEntryDTO)));
        assertEquals(1, globalUpdated.size());

        List<ScheduleEntryDTO> dayUpdated = groupService.updateScheduleForDay("DDYA-4", "Monday",
                new ArrayList<>(List.of(scheduleEntryDTO)));
        assertEquals(1, dayUpdated.size());
    }

    @Test
    void ShouldDeleteScheduleGlobalAndForDay() {
        when(message.findGroupOrThrow("DDYA-4")).thenReturn(group);
        when(groupRepository.save(group)).thenReturn(group);

        groupService.deleteScheduleGlobal("DDYA-4");
        assertTrue(group.getSchedule().isEmpty());

        group.getSchedule().add(scheduleEntry);
        groupService.deleteScheduleForDay("DDYA-4", "Monday");
        assertTrue(group.getSchedule().isEmpty());
    }

    @Test
    void ShouldGetGroupsByTeacherAndSubject() {
        when(groupRepository.findByTeacher(5)).thenReturn(List.of(group));
        when(groupRepository.findBySubjectId("DDYA")).thenReturn(List.of(group));

        List<GroupDTO> byTeacher = groupService.getGroupsByTeacher(5);
        List<GroupDTO> bySubject = groupService.getGroupsBySubject("DDYA");

        assertEquals(1, byTeacher.size());
        assertEquals(1, bySubject.size());
    }

    @Test
    void ShouldCapacities() {
        when(message.findGroupOrThrow("DDYA-4")).thenReturn(group);
        when(groupRepository.save(group)).thenReturn(group);

        assertEquals(10, groupService.getMaxCapacity("DDYA-4"));
        assertEquals(5, groupService.getCurrentCapacity("DDYA-4"));

        GroupDTO capUpdated = groupService.updateCapacity("DDYA-4", 15);
        assertEquals("Diseño de estructuras y algoritmos", capUpdated.getName());
    }

    @Test
    void ShouldUpdateGroup() {
        GroupDTO dto = new GroupDTO();
        dto.setMaximumCapacity(15);
        dto.setCurrentCapacity(7);
        dto.setName("Nuevo Nombre");
        dto.setSchedule(new ArrayList<>(List.of(scheduleEntryDTO)));
        dto.setCurriculum(Curriculum.ADMI_05);

        when(message.findGroupOrThrow("DDYA-4")).thenReturn(group);
        when(message.findSubjectOrThrow(group.getSubjectId())).thenReturn(new Subject() {{
            setSubjectId("DDYA");
            setMaximumCapacity(100);
        }});
        when(groupRepository.findBySubjectId("DDYA")).thenReturn(List.of(group));
        when(groupRepository.save(group)).thenReturn(group);

        GroupDTO updated = groupService.updateGroup("DDYA-4", dto);

        assertEquals("Nuevo Nombre", updated.getName());
        assertEquals(15, updated.getMaximumCapacity());
        assertEquals(7, updated.getCurrentCapacity());
        assertEquals(Curriculum.ADMI_05, updated.getCurriculum());
        assertEquals(1, updated.getSchedule().size());
    }

    @Test
    void ShouldDeleteGroup() {
        when(message.findGroupOrThrow("DDYA-4")).thenReturn(group);
        groupService.deleteGroup("DDYA-4");

        verify(groupRepository, times(1)).delete(group);
    }

    @Test
    void ShouldPartialUpdateGroup() {
        GroupDTO dto = new GroupDTO();
        dto.setName("Algoritmos");                 
        dto.setMaximumCapacity(0);                  
        dto.setCurrentCapacity(8);                  
        dto.setCurriculum(Curriculum.ADMI_05);     
        dto.setSchedule(new ArrayList<>(List.of(scheduleEntryDTO))); 
        dto.setSubjectId(null);                     
        dto.setGroupStatus(null);                   

        when(message.findGroupOrThrow("DDYA-4")).thenReturn(group);
        when(groupRepository.save(group)).thenReturn(group);

        GroupDTO updated = groupService.partialUpdateGroup("DDYA-4", dto);

        assertEquals("Algoritmos", updated.getName(), "El nombre debería actualizarse");
        assertEquals(8, updated.getCurrentCapacity(), "La capacidad actual debería actualizarse");
        assertEquals(Curriculum.ADMI_05, updated.getCurriculum(), "El curriculum debería actualizarse");
        assertEquals(1, updated.getSchedule().size(), "El schedule debería actualizarse");

        assertEquals(10, updated.getMaximumCapacity(), "La capacidad máxima no debería cambiar");
        assertEquals("DDYA", updated.getSubjectId(), "El subjectId no debería cambiar");
    }


    @Test
    void ShouldAssignTeacherToGroup() {
        group.setTeacher(null);
        when(message.findGroupOrThrow("DDYA-4")).thenReturn(group);
        when(groupRepository.save(group)).thenReturn(group);

        groupService.assignTeacherToGroup("DDYA-4", 10);

        assertEquals(10, group.getTeacher());
    }

    @Test
    void ShouldRemoveTeacherFromGroup() {
        group.setTeacher(10);
        when(message.findGroupOrThrow("DDYA-4")).thenReturn(group);
        when(groupRepository.save(group)).thenReturn(group);

        groupService.removeTeacherFromGroup("DDYA-4");

        assertEquals(0, group.getTeacher());
}


    @Test
    void ShouldGetEnrolledCount() {
        group.setCurrentCapacity(7);
        when(message.findGroupOrThrow("DDYA-4")).thenReturn(group);
        int enrolled = groupService.getEnrolledCount("DDYA-4");

        assertEquals(7, enrolled);
    }

    @Test
    void ShouldAssignStudentToGroupAddsScheduleWithoutDuplicates() {
        group.setSchedule(new ArrayList<>(List.of(scheduleEntry)));
        student.setSchedule(new ArrayList<>()); 

        when(message.findStudentOrThrow(1)).thenReturn(student);
        when(message.findGroupOrThrow("DDYA-4")).thenReturn(group);
        when(mongoTemplate.findAndModify(any(), any(), any(), eq(Group.class))).thenReturn(group);
        when(studentRepository.save(student)).thenReturn(student);
        when(groupMapper.toDTO(group)).thenReturn(groupDTO);

        groupService.assignStudentToGroup("DDYA-4", 1);

        assertEquals(1, student.getSchedule().size());
        ScheduleEntry seAdded = student.getSchedule().get(0);
        assertEquals("DDYA", seAdded.getSubject());
        assertEquals("DDYA-4", seAdded.getGroup());
        assertEquals("Monday", seAdded.getDay());
    }

    @Test
    void ShouldAssignStudentToGroupAvoidsDuplicateSchedule() {
        group.setSchedule(new ArrayList<>(List.of(scheduleEntry)));
        
        ScheduleEntry studentEntry = new ScheduleEntry();
        studentEntry.setSubject("DDYA");
        studentEntry.setGroup("DDYA-4");
        studentEntry.setDay("Monday");
        studentEntry.setFrom("08:00");
        studentEntry.setTo("10:00");
        student.setSchedule(new ArrayList<>(List.of(studentEntry)));

        when(message.findStudentOrThrow(1)).thenReturn(student);
        when(message.findGroupOrThrow("DDYA-4")).thenReturn(group);
        when(mongoTemplate.findAndModify(any(), any(), any(), eq(Group.class))).thenReturn(group);
        when(studentRepository.save(student)).thenReturn(student);
        when(groupMapper.toDTO(group)).thenReturn(groupDTO);

        groupService.assignStudentToGroup("DDYA-4", 1);

        assertEquals(1, student.getSchedule().size());
    }

    @Test
    void ShouldRemoveStudentFromGroup() {
        String groupId = "DDYA-1";
        int studentId = 1001;

        student.setId(studentId);
        student.setEnrolledSubjects(new ArrayList<>(List.of("DDYA")));
        student.setSchedule(new ArrayList<>());

        group.setGroupId(groupId);
        group.setSubjectId("DDYA");
        group.setCurrentCapacity(5);

        Group updatedGroup = new Group();
        updatedGroup.setGroupId(groupId);
        updatedGroup.setSubjectId("DDYA");
        updatedGroup.setCurrentCapacity(4);

        when(message.findStudentOrThrow(studentId)).thenReturn(student);
        when(message.findGroupOrThrow(groupId)).thenReturn(group);
        when(mongoTemplate.findAndModify(any(), any(), any(), eq(Group.class))).thenReturn(updatedGroup);
        when(groupMapper.toDTO(updatedGroup)).thenReturn(groupDTO);

        GroupDTO result = groupService.removeStudentFromGroup(groupId, studentId);

        assertNotNull(result);
    }

    @Test
    void ShouldPartiallyUpdateGroupFields() {
        String groupId = "ISIS-1";

        Group existing = new Group();
        existing.setGroupId(groupId);
        existing.setName("Grupo Original");
        existing.setMaximumCapacity(20);
        existing.setCurrentCapacity(10);
        existing.setSubjectId("MAT01");
        existing.setCurriculum(Curriculum.ISIS_15);
        existing.setGroupStatus(GroupStatus.ACTIVE);
        existing.setSchedule(new ArrayList<>());

        GroupDTO dto = new GroupDTO();
        dto.setName("Grupo Actualizado");
        dto.setMaximumCapacity(40); 
        dto.setCurrentCapacity(15); 
        dto.setSchedule(Arrays.asList(new ScheduleEntryDTO())); 
        dto.setSubjectId("MAT02"); 
        dto.setCurriculum(Curriculum.MATE_04); 
        dto.setGroupStatus(GroupStatus.INACTIVE);

        List<ScheduleEntry> mappedSchedule = Arrays.asList(new ScheduleEntry());
        when(scheduleEntryMapper.toModelList(anyList())).thenReturn(mappedSchedule);

        when(message.findGroupOrThrow(groupId)).thenReturn(existing);
        when(groupRepository.save(any(Group.class))).thenAnswer(inv -> inv.getArgument(0));
        when(groupMapper.toDTO(any(Group.class))).thenReturn(dto);

        GroupDTO result = groupService.partialUpdateGroup(groupId, dto);

        assertNotNull(result);
        assertEquals("Grupo Actualizado", existing.getName());
        assertEquals(40, existing.getMaximumCapacity());
        assertEquals(15, existing.getCurrentCapacity());
        assertEquals("MAT02", existing.getSubjectId());
        assertEquals(Curriculum.MATE_04, existing.getCurriculum());
        assertEquals(GroupStatus.INACTIVE, existing.getGroupStatus());
        assertEquals(mappedSchedule, existing.getSchedule());

    }

    @Test
    void ShouldAssignStudentToGroupWhenGroupHasNoSchedule() {
        String groupId = "DDYA-4";
        int studentId = 1;

        student.setId(studentId);
        student.setSchedule(new ArrayList<>());

        group.setGroupId(groupId);
        group.setSubjectId("DDYA");
        group.setMaximumCapacity(10);
        group.setCurrentCapacity(5);
        group.setSchedule(null); 

        Group updatedGroup = new Group();
        updatedGroup.setGroupId(groupId);
        updatedGroup.setSubjectId("DDYA");
        updatedGroup.setMaximumCapacity(10);
        updatedGroup.setCurrentCapacity(6);
        updatedGroup.setSchedule(null); 

        when(message.findStudentOrThrow(studentId)).thenReturn(student);
        when(message.findGroupOrThrow(groupId)).thenReturn(group);
        when(mongoTemplate.findAndModify(any(), any(), any(), eq(Group.class))).thenReturn(updatedGroup);
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(groupMapper.toDTO(any(Group.class))).thenReturn(new GroupDTO());

        GroupDTO result = groupService.assignStudentToGroup(groupId, studentId);

        assertNotNull(result);
        assertEquals(1, student.getSchedule().size());

        ScheduleEntry entry = student.getSchedule().get(0);
        assertEquals("DDYA", entry.getSubject());
        assertEquals("DDYA-4", entry.getGroup());

    }

}