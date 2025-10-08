package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.dtos.GroupDTO;
import eci.edu.dosw.proyecto.dtos.SubjectDTO;
import eci.edu.dosw.proyecto.enums.Curriculum;
import eci.edu.dosw.proyecto.dtos.ScheduleEntryDTO;
import eci.edu.dosw.proyecto.mappers.GroupMapper;
import eci.edu.dosw.proyecto.mappers.ScheduleEntryMapper;
import eci.edu.dosw.proyecto.mappers.SubjectMapper;
import eci.edu.dosw.proyecto.models.Group;
import eci.edu.dosw.proyecto.models.Subject;
import eci.edu.dosw.proyecto.models.ScheduleEntry;
import eci.edu.dosw.proyecto.repositories.GroupRepository;
import eci.edu.dosw.proyecto.services.impl.GroupServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class GroupServiceImplTest {

    @Mock
    GroupRepository groupRepository;

    @Mock
    GroupMapper groupMapper;

    @Mock
    ScheduleEntryMapper scheduleEntryMapper;

    @Mock
    SubjectMapper subjectMapper;

    @InjectMocks
    GroupServiceImpl groupService;

    /**
    @Test
    void ShouldCreateGroup() {
        GroupDTO dto = new GroupDTO();
        dto.setName("4");
        dto.setCurriculum(Curriculum.ISIS_15);

        Group model = new Group();
        model.setName("4");
        model.setCurriculum(Curriculum.ISIS_15);
        
        Group saved = new Group();
        saved.setName("4");
        saved.setCurriculum(Curriculum.ISIS_15);
        saved.setGroupId("DOSW");

        GroupDTO expectedDto = new GroupDTO();
        expectedDto.setName("4");
        expectedDto.setCurriculum(Curriculum.ISIS_15);
        expectedDto.setGroupId("DOSW");

        when(groupMapper.toModel(dto)).thenReturn(model);
        when(groupRepository.save(model)).thenReturn(saved);
        when(groupMapper.toDTO(saved)).thenReturn(expectedDto);

        GroupDTO out = groupService.createGroup(dto);
        assertNotNull(out);
        assertEquals("DOSW", out.getGroupId());
    }

    @Test
    void ShouldNotCreateGroup() {
        GroupDTO dto = new GroupDTO();
        dto.setName("5");
        dto.setCurriculum(Curriculum.ISIS_15);

        SubjectDTO subjDto = new SubjectDTO();
        subjDto.setCurriculum(Curriculum.ISIS_14);
        dto.setSubject(subjDto);

        Group model = new Group();
        model.setName("5");
        model.setCurriculum(Curriculum.ISIS_15);
        model.setSubject(new Subject());

        when(groupMapper.toModel(dto)).thenReturn(model);
        model.getSubject().setCurriculum(Curriculum.ISIS_14);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> groupService.createGroup(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("pensum") || ex.getMessage().toLowerCase().contains("corresponde"));
    }


    @Test
    void ShouldReturnAllGroups() {
        Group g = new Group();
        g.setGroupId("TPYC-1");
        g.setName("TPYC");

        GroupDTO dto = new GroupDTO();
        dto.setGroupId("TPYC-1");
        dto.setName("TPYC");

        when(groupRepository.findAll()).thenReturn(List.of(g));
        when(groupMapper.toDTOList(List.of(g))).thenReturn(List.of(dto));

        List<GroupDTO> list = groupService.getAllGroups();
        assertEquals(1, list.size());
        assertEquals("TPYC-1", list.get(0).getGroupId());
    }



    @Test
    void ShouldReturnGroupById() {
        Group g = new Group();
        g.setGroupId("DDYA-1");
        g.setName("DDYA");

        GroupDTO dto = new GroupDTO();
        dto.setGroupId("DDYA-1");
        dto.setName("DDYA");

        when(groupRepository.findByGroupId("DDYA-1")).thenReturn(Optional.of(g));
        when(groupMapper.toDTO(g)).thenReturn(dto);

        GroupDTO out = groupService.getGroupById("DDYA-1");
        assertEquals("DDYA-1", out.getGroupId());
    }

    @Test
    void ShouldThrowWhenGroupNotFound() {
        when(groupRepository.findByGroupId("NOsubject")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> groupService.getGroupById("NOsubject"));
        assertTrue(ex.getMessage().toLowerCase().contains("no encontrado") || ex.getMessage().toLowerCase().contains("no encontrado"));
    }


    @Test
    void ShouldUpdateGroup() {
        Group existing = new Group();
        existing.setGroupId("AYED-23");
        existing.setName("AYED");
        existing.setCurriculum(Curriculum.ISIS_14);
        existing.setCurrentCapacity(5);
        existing.setMaximumCapacity(20);

        GroupDTO dto = new GroupDTO();
        dto.setName("DDYA");
        dto.setTeacher("Alejandro Anzola Avila");
        dto.setMaximumCapacity(30);
        dto.setCurrentCapacity(10);
        dto.setCurriculum(Curriculum.ISIS_14);

        ScheduleEntryDTO seDto = new ScheduleEntryDTO();
        seDto.setDay("LUNES");
        dto.setSchedule(List.of(seDto));
        when(scheduleEntryMapper.toModelList(dto.getSchedule())).thenReturn(List.of(new ScheduleEntry()));
        when(groupRepository.findByGroupId("DDYA-236")).thenReturn(Optional.of(existing));
        Group saved = new Group();
        saved.setGroupId("DDYA-236");
        saved.setName("DDYA");
        saved.setTeacher("Alejandro Anzola Avila");
        saved.setCurrentCapacity(10);
        saved.setMaximumCapacity(30);
        saved.setCurriculum(Curriculum.ISIS_14);

        when(groupRepository.save(existing)).thenReturn(saved);

        GroupDTO outDto = new GroupDTO();
        outDto.setGroupId("DDYA-236");
        outDto.setName("DDYA");
        outDto.setTeacher("Alejandro Anzola Avila");
        outDto.setCurrentCapacity(10);
        outDto.setMaximumCapacity(30);

        when(groupMapper.toDTO(saved)).thenReturn(outDto);

        GroupDTO res = groupService.updateGroup("DDYA-236", dto);
        assertEquals("DDYA", res.getName());
        assertEquals("Alejandro Anzola Avila", res.getTeacher());
        assertEquals(10, res.getCurrentCapacity());
    }


    @Test
    void ShouldThrowWhenDeleteNotFound() {
        when(groupRepository.findByGroupId("RECO-1")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> groupService.deleteGroup("RECO-1"));
    }
    
    @Test
    void ShouldUpdateCapacity() {
        Group existing = new Group();

        existing.setGroupId("ECDI-6");
        existing.setCurrentCapacity(5);

        when(groupRepository.findByGroupId("ECDI-6")).thenReturn(Optional.of(existing));

        Group saved = new Group();

        saved.setGroupId("ECDI-6");
        saved.setCurrentCapacity(12);
        when(groupRepository.save(existing)).thenReturn(saved);
        
        GroupDTO dto = new GroupDTO();

        dto.setCurrentCapacity(12);
        when(groupMapper.toDTO(saved)).thenReturn(dto);

        GroupDTO out = groupService.updateCapacity("ECDI-6", 12);

        assertEquals(12, out.getCurrentCapacity());
    }

    @Test
    void ShouldReturnWaitlist() {
        Group existing = new Group();

        existing.setGroupId("CALD-12");
        existing.setWaitlist(List.of(1000100575, 1000100516)); 
        when(groupRepository.findByGroupId("CALD-12")).thenReturn(Optional.of(existing));

        var wl = groupService.getWaitlist("CALD-12");

        assertNotNull(wl);
        assertEquals(2, wl.size());
        assertEquals(1000100575, wl.get(0));
    }
**/
}