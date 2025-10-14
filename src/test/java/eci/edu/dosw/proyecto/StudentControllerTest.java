package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.controller.StudentController;
import eci.edu.dosw.proyecto.dtos.AcademicPlanDTO;
import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.StudentDTO;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.services.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController controller;

    @Test
    void shouldCreateStudent() {
        StudentDTO in = new StudentDTO();
        in.setName("Juan Pablo Caballero");
        in.setEmail("juan.ccastellanos@mail.escuelaing.edu.co");
        StudentDTO out = new StudentDTO();
        out.setId(1000100516);
        out.setName("Juan Pablo Caballero");
        out.setEmail("juan.ccastellanos@mail.escuelaing.edu.co");
        when(studentService.createStudent(in)).thenReturn(out);
        StudentDTO res = controller.createStudent(in);

        assertNotNull(res);
        assertEquals(1000100516, res.getId());
        assertEquals("Juan Pablo Caballero", res.getName());
    }

    @Test
    void shouldGetAllStudents() {
        StudentDTO s1 = new StudentDTO(); s1.setId(1); s1.setName("Oscar Porras Sanchez");
        StudentDTO s2 = new StudentDTO(); s2.setId(2); s2.setName("David Santiago Palcios");
        when(studentService.getAllStudents()).thenReturn(List.of(s1, s2));
        List<StudentDTO> res = controller.getAllStudents();

        assertEquals(2, res.size());
        assertEquals("Oscar Porras Sanchez", res.get(0).getName());
    }

    @Test
    void shouldGetStudentById() {
        StudentDTO dto = new StudentDTO(); dto.setId(1000100667); dto.setName("Diego Fernando Chavarro");
        when(studentService.getStudentById(1000100667)).thenReturn(dto);
        StudentDTO res = controller.getStudentById(1000100667);

        assertEquals(1000100667, res.getId());
        assertEquals("Diego Fernando Chavarro", res.getName());
    }

    @Test
    void shouldUpdateStudent() {
        StudentDTO updated = new StudentDTO();
        updated.setName("Robinson Steven Nuñez");
        updated.setEmail("robinson.nunez-p@mail.escuelaing.edu.co");
        StudentDTO returned = new StudentDTO();
        returned.setId(1000100575);
        returned.setName("Robinson Steven Nuñez");
        returned.setEmail("robinson.nunez-p@mail.escuelaing.edu.co");
        when(studentService.updateStudent(1000100575, updated)).thenReturn(returned);
        StudentDTO res = controller.updateStudent(1000100575, updated);

        assertNotNull(res);
        assertEquals("Robinson Steven Nuñez", res.getName());

    }

    @Test
    void shouldPartialUpdateStudent() {
        StudentDTO patch = new StudentDTO();
        patch.setName("Parcial");
        StudentDTO ret = new StudentDTO();
        ret.setId(1000100575);
        ret.setName("Parcial");
        when(studentService.partialUpdateStudent(1000100575, patch)).thenReturn(ret);
        StudentDTO res = controller.partialUpdateStudent(1000100575, patch);

        assertEquals("Parcial", res.getName());

    }

    @Test
    void shouldGetStudentRequests() {
        ChangeRequestDTO r1 = new ChangeRequestDTO(); r1.setId(UUID.randomUUID());
        when(studentService.getStudentRequests(1000100575)).thenReturn(List.of(r1));
        List<ChangeRequestDTO> res = controller.getStudentRequests(1000100575);

        assertEquals(1, res.size());

    }

    @Test
    void shouldGetStudentRequestsByStatusWhenProvided() {
        ChangeRequestDTO r1 = new ChangeRequestDTO(); r1.setId(UUID.randomUUID());
        when(studentService.getStudentRequestsByStatus(1000100575, RequestStatus.PENDING)).thenReturn(List.of(r1));
        List<ChangeRequestDTO> res = controller.getStudentRequestsByStatus(1000100575, RequestStatus.PENDING);

        assertEquals(1, res.size());

    }

    @Test
    void shouldGetStudentRequestsByStatusWhenNullUsesAll() {
        ChangeRequestDTO r1 = new ChangeRequestDTO(); r1.setId(UUID.randomUUID());
        when(studentService.getStudentRequests(1000100575)).thenReturn(List.of(r1));
        List<ChangeRequestDTO> res = controller.getStudentRequestsByStatus(1000100575, null);

        assertEquals(1, res.size());

    }

    @Test
    void shouldDeleteStudent() {
        doNothing().when(studentService).deleteStudent(1000100575);
        controller.deleteStudent(1000100575);
    }

    @Test
    void shouldGetAcademicPlan() {
        AcademicPlanDTO dto = new AcademicPlanDTO();
        dto.setStudentId(1000100575);
        when(studentService.getAcademicPlan(1000100575)).thenReturn(dto);
        AcademicPlanDTO res = controller.getAcademicPlan(1000100575);

        assertEquals(1000100575, res.getStudentId());
    }
}
