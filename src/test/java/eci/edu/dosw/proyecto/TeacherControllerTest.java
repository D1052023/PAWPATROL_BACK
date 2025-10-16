package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.controller.TeacherController;
import eci.edu.dosw.proyecto.dtos.TeacherDTO;
import eci.edu.dosw.proyecto.services.TeacherService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeacherControllerTest {

    @InjectMocks
    private TeacherController controller;

    @Mock
    private TeacherService teacherService;

    private TeacherDTO teacher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        teacher = new TeacherDTO();
        teacher.setId(1);
        teacher.setName("Robinhood");
        teacher.setEmail("robinhood@mail.escuelaing.edu.co");
    }

    @Test
    void ShouldCreateTeacher() {
        when(teacherService.createTeacher(teacher)).thenReturn(teacher);

        TeacherDTO result = controller.createTeacher(teacher);

        assertEquals("Robinhood", result.getName());
        assertEquals("robinhood@mail.escuelaing.edu.co", result.getEmail());
    }

    @Test
    void ShouldGetAllTeachers() {
        when(teacherService.getAllTeachers()).thenReturn(Arrays.asList(teacher));

        List<TeacherDTO> result = controller.getAllTeachers();

        assertEquals(1, result.size());
        assertEquals("Robinhood", result.get(0).getName());
    }

    @Test
    void ShouldGetTeacherById() {
        when(teacherService.getTeacherById(1)).thenReturn(teacher);

        TeacherDTO result = controller.getTeacherById(1);

        assertEquals("Robinhood", result.getName());
        assertEquals(1, result.getId());
    }

    @Test
    void ShouldUpdateTeacher() {
        TeacherDTO updated = new TeacherDTO();
        updated.setId(1);
        updated.setName("juan");
        updated.setEmail("juan.ccastellanos@mail.escuelaing.edu.co");

        when(teacherService.updateTeacher(1, updated)).thenReturn(updated);

        TeacherDTO result = controller.updateTeacher(1, updated);

        assertEquals("juan", result.getName());
        assertEquals("juan.ccastellanos@mail.escuelaing.edu.co", result.getEmail());
    }

    @Test
    void ShouldPartialUpdateTeacher() {
        TeacherDTO partial = new TeacherDTO();
        partial.setName("juan");

        when(teacherService.partialUpdateTeacher(1, partial)).thenReturn(partial);

        TeacherDTO result = controller.partialUpdateTeacher(1, partial);
        assertEquals("juan", result.getName());
    }

    @Test
    void ShouldDeleteTeacher() {
        controller.deleteTeacher(1);
        verify(teacherService, times(1)).deleteTeacher(1);
    }

    @Test
    void ShouldGetTeacherByEmailFound() {
        when(teacherService.getTeacherByEmail("robinhood@mail.escuelaing.edu.co")).thenReturn(Optional.of(teacher));

        TeacherDTO result = controller.getTeacherByEmail("robinhood@mail.escuelaing.edu.co");

        assertEquals("Robinhood", result.getName());
        assertEquals("robinhood@mail.escuelaing.edu.co", result.getEmail());
    }

    @SuppressWarnings("null")
    @Test
    void ShouldGetTeacherByEmailNotFound() {
        when(teacherService.getTeacherByEmail("noexiste@mail.com")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> controller.getTeacherByEmail("noexiste@mail.com"));

        assertEquals(404, exception.getStatusCode().value());
        assertTrue(exception.getReason().contains("Profesor no encontrado"));
    }
}
