package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.controller.GroupController;
import eci.edu.dosw.proyecto.dtos.GroupDTO;
import eci.edu.dosw.proyecto.dtos.ScheduleEntryDTO;
import eci.edu.dosw.proyecto.dtos.StudentDTO;
import eci.edu.dosw.proyecto.enums.Curriculum;
import eci.edu.dosw.proyecto.enums.GroupStatus;
import eci.edu.dosw.proyecto.services.GroupService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupControllerTest {

    @InjectMocks
    private GroupController controller;

    @Mock
    private GroupService groupService;

    private GroupDTO group;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        group = new GroupDTO();
        group.setGroupId("MPIN-3");
        group.setName("MPIN");
        group.setMaximumCapacity(40);
        group.setCurrentCapacity(30);
        group.setCurriculum(Curriculum.ISIS_15);
        group.setGroupStatus(GroupStatus.ACTIVE);
    }

    @Test
    void ShouldCreateGroup() {
        when(groupService.createGroup(group)).thenReturn(group);

        GroupDTO result = controller.createGroup(group);

        assertEquals("MPIN-3", result.getGroupId());
        assertEquals("MPIN", result.getName());
    }

    @Test
    void ShouldGetAllGroups() {
        when(groupService.getAllGroups()).thenReturn(Arrays.asList(group));

        List<GroupDTO> result = controller.getAllGroups();

        assertEquals(1, result.size());
        assertEquals("MPIN-3", result.get(0).getGroupId());
    }

    @Test
    void ShouldGetGroupById() {
        when(groupService.getGroupById("MPIN-3")).thenReturn(group);

        GroupDTO result = controller.getGroupById("MPIN-3");

        assertEquals("MPIN-3", result.getGroupId());
        assertEquals("MPIN", result.getName());
    }

    @Test
    void ShouldUpdateGroup() {
        group.setName("ODSC-3");
        when(groupService.updateGroup("MPIN-3", group)).thenReturn(group);

        GroupDTO result = controller.updateGroup("MPIN-3", group);

        assertEquals("ODSC-3", result.getName());
    }


    @Test
    void ShouldUpdateCapacity() {
        group.setCurrentCapacity(35);
        when(groupService.updateCapacity("MPIN-3", 35)).thenReturn(group);

        GroupDTO result = controller.updateCapacity("MPIN-3", 35);

        assertEquals(35, result.getCurrentCapacity());
    }

    @Test
    void ShouldGetMaxAndCurrentCapacity() {
        when(groupService.getMaxCapacity("MPIN-3")).thenReturn(40);
        when(groupService.getCurrentCapacity("MPIN-3")).thenReturn(30);

        assertEquals(40, controller.getMaxCapacity("MPIN-3"));
        assertEquals(30, controller.getCurrentCapacity("MPIN-3"));
    }

    @Test
    void ShouldGetEnrolledCount() {
        when(groupService.getEnrolledCount("MPIN-3")).thenReturn(28);

        assertEquals(28, controller.getEnrolledCount("MPIN-3"));
    }

    @Test
    void ShouldAssignAndRemoveTeacher() {
        when(groupService.assignTeacherToGroup("MPIN-3", 1)).thenReturn(group);
        when(groupService.removeTeacherFromGroup("MPIN-3")).thenReturn(group);

        assertEquals(group, controller.assignTeacherToGroup("MPIN-3", 1));
        assertEquals(group, controller.removeTeacherFromGroup("MPIN-3"));
    }

    @Test
    void ShouldGetGroupsByTeacherAndSubject() {
        when(groupService.getGroupsByTeacher(1)).thenReturn(Arrays.asList(group));
        when(groupService.getGroupsBySubject("SUBJ1")).thenReturn(Arrays.asList(group));

        assertEquals(1, controller.getGroupsByTeacher(1).size());
        assertEquals(1, controller.getGroupsBySubject("SUBJ1").size());
    }

    @Test
    void ShouldAssignAndRemoveStudent() {
        when(groupService.assignStudentToGroup("MPIN-3", 101)).thenReturn(group);

        GroupDTO assigned = controller.assignStudentToGroup("MPIN-3", 101);
        assertEquals(group, assigned);

        controller.removeStudentFromGroup("MPIN-3", 101);
        verify(groupService, times(1)).removeStudentFromGroup("MPIN-3", 101);
    }



    @Test
    void ShouldGetWaitlistAndDetails() {
        group.setWaitlist(Arrays.asList(101, 102));
        when(groupService.getWaitlist("MPIN-3")).thenReturn(group.getWaitlist());
        when(groupService.getWaitlistDetails("MPIN-3")).thenReturn(Arrays.asList(new StudentDTO(), new StudentDTO()));

        assertEquals(2, controller.getWaitlist("MPIN-3").size());
        assertEquals(2, controller.getWaitlistDetails("MPIN-3").size());
    }

    @Test
    void ShouldManageSchedule() {
        ScheduleEntryDTO entry = new ScheduleEntryDTO();
        
        when(groupService.getSchedule("MPIN-3")).thenReturn(Arrays.asList(entry));
        when(groupService.addScheduleEntry("MPIN-3", entry)).thenReturn(entry);
        when(groupService.updateScheduleGlobal("MPIN-3", Arrays.asList(entry))).thenReturn(Arrays.asList(entry));
        when(groupService.updateScheduleForDay("MPIN-3", "Monday", Arrays.asList(entry))).thenReturn(Arrays.asList(entry));

        doNothing().when(groupService).deleteScheduleGlobal("MPIN-3");
        doNothing().when(groupService).deleteScheduleForDay("MPIN-3", "Monday");

        assertEquals(1, controller.getSchedule("MPIN-3").size());
        assertEquals(entry, controller.addScheduleEntry("MPIN-3", entry));
        assertEquals(1, controller.updateScheduleGlobal("MPIN-3", Arrays.asList(entry)).size());
        assertEquals(1, controller.updateScheduleForDay("MPIN-3", "Monday", Arrays.asList(entry)).size());

        controller.deleteScheduleGlobal("MPIN-3");
        controller.deleteScheduleForDay("MPIN-3", "Monday");

        verify(groupService, times(1)).deleteScheduleGlobal("MPIN-3");
        verify(groupService, times(1)).deleteScheduleForDay("MPIN-3", "Monday");
    }

    @Test
    void ShouldPartialUpdateGroup() {
        GroupDTO partialGroup = new GroupDTO();
        partialGroup.setName("DDYA-2");

        when(groupService.partialUpdateGroup("MPIN-3", partialGroup)).thenReturn(partialGroup);

        GroupDTO result = controller.partialUpdateGroup("MPIN-3", partialGroup);

        assertEquals("DDYA-2", result.getName());
    }

    @Test
    void ShouldDeleteGroup() {
        controller.deleteGroup("MPIN-3");
        verify(groupService, times(1)).deleteGroup("MPIN-3");
    }

}
