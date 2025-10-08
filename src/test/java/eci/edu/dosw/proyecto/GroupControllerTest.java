package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.controller.GroupController;
import eci.edu.dosw.proyecto.dtos.GroupDTO;
import eci.edu.dosw.proyecto.enums.Curriculum;
import eci.edu.dosw.proyecto.enums.GroupStatus;
import eci.edu.dosw.proyecto.services.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GroupControllerTest {

    @InjectMocks
    private GroupController controller;

    @Mock
    private GroupService groupService;

    private GroupDTO group;

    /**
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        group = new GroupDTO();
        group.setGroupId("MPIN");
        group.setName("MPIN-32");
        group.setTeacher("Wilmer Edicson");
        group.setMaximumCapacity(40);
        group.setCurrentCapacity(30);
        group.setCurriculum(Curriculum.ECON_15);
        group.setGroupStatus(GroupStatus.ACTIVE);
    }

    @Test
    void ShouldCreateGroup() {
        when(groupService.createGroup(group)).thenReturn(group);

        GroupDTO result = controller.createGroup(group);

        assertEquals("MPIN", result.getGroupId());
        assertEquals("MPIN-32", result.getName());
    }

    @Test
    void ShouldGetAllGroups() {
        when(groupService.getAllGroups()).thenReturn(Arrays.asList(group));

        List<GroupDTO> result = controller.getAllGroups();

        assertEquals(1, result.size());
        assertEquals("MPIN", result.get(0).getGroupId());
    }

    @Test
    void ShouldGetGroupById() {
        when(groupService.getGroupById("MPIN")).thenReturn(group);

        GroupDTO result = controller.getGroupById("MPIN");

        assertEquals("MPIN", result.getGroupId());
        assertEquals("MPIN-32", result.getName());
    }

    @Test
    void shouldUpdateGroup() {
        group.setName("Grupo Actualizado");
        when(groupService.updateGroup("MPIN", group)).thenReturn(group);

        GroupDTO result = controller.updateGroup("MPIN", group);

        assertEquals("Grupo Actualizado", result.getName());
    }

    @Test
    void shouldPartialUpdateGroup() {
        group.setTeacher("Profesor Y");
        when(groupService.updateGroup("MPIN", group)).thenReturn(group);

        GroupDTO result = controller.partialUpdateGroup("MPIN", group);

        assertEquals("Profesor Y", result.getTeacher());
    }

    @Test
    void shouldUpdateCapacity() {
        group.setCurrentCapacity(35);
        when(groupService.updateCapacity("MPIN", 35)).thenReturn(group);

        GroupDTO result = controller.updateCapacity("MPIN", 35);

        assertEquals(35, result.getCurrentCapacity());
    }

    @Test
    void shouldGetWaitlist() {
        group.setWaitlist(Arrays.asList(101, 102));
        when(groupService.getWaitlist("MPIN")).thenReturn(group.getWaitlist());

        List<Integer> result = controller.getWaitlist("MPIN");

        assertEquals(2, result.size());
        assertEquals(101, result.get(0));
    }
    **/
}
