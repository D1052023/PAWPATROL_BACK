package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.controller.StudentController;
import eci.edu.dosw.proyecto.dtos.AcademicPlanDTO;
import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.StudentDTO;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.services.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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
    private StudentController studentController;

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
        StudentDTO res = studentController.createStudent(in);

        assertNotNull(res);
        assertEquals(1000100516, res.getId());
        assertEquals("Juan Pablo Caballero", res.getName());
    }

    @Test
    void shouldGetAllStudents() {
        StudentDTO s1 = new StudentDTO(); s1.setId(1); s1.setName("Oscar Porras Sanchez");
        StudentDTO s2 = new StudentDTO(); s2.setId(2); s2.setName("David Santiago Palcios");
        when(studentService.getAllStudents()).thenReturn(List.of(s1, s2));
        List<StudentDTO> res = studentController.getAllStudents();

        assertEquals(2, res.size());
        assertEquals("Oscar Porras Sanchez", res.get(0).getName());
    }

    @Test
    void shouldGetStudentById() {
        StudentDTO dto = new StudentDTO(); dto.setId(1000100667); dto.setName("Diego Fernando Chavarro");
        when(studentService.getStudentById(1000100667)).thenReturn(dto);
        StudentDTO res = studentController.getStudentById(1000100667);

        assertEquals(1000100667, res.getId());
        assertEquals("Diego Fernando Chavarro", res.getName());
    }

    @Test
    void shouldUpdateStudent() {
        StudentDTO updated = new StudentDTO();
        updated.setId(1);
        updated.setName("Juan Carlos");
        updated.setEmail("juan@example.com");
        StudentDTO returned = new StudentDTO();
        returned.setId(1000100575);
        returned.setName("Robinson Steven Nuñez");
        returned.setEmail("robinson.nunez-p@mail.escuelaing.edu.co");
        when(studentService.updateStudent(eq(1000100575), any(StudentDTO.class))).thenReturn(returned);
        when(studentService.updateStudent(eq(1), any(StudentDTO.class))).thenReturn(updated);
        StudentDTO res = studentController.updateStudent(1000100575, updated);
        assertNotNull(res, "Respuesta para id 1000100575 no debe ser null");
        assertEquals("Robinson Steven Nuñez", res.getName());
        StudentDTO result = studentController.updateStudent(1, updated);

        assertNotNull(result, "Respuesta para id 1 no debe ser null");
        assertEquals("Juan Carlos", result.getName());
    }

    @Test
    void shouldPartialUpdateStudent() {
        StudentDTO patch = new StudentDTO();
        patch.setName("Parcial");
        StudentDTO retFor100 = new StudentDTO();
        retFor100.setId(1000100575);
        retFor100.setName("Parcial");
        StudentDTO retFor1 = new StudentDTO();
        retFor1.setId(1);
        retFor1.setName("Robinson Nuñez");
        when(studentService.partialUpdateStudent(eq(1000100575), any(StudentDTO.class))).thenReturn(retFor100);
        when(studentService.partialUpdateStudent(eq(1), any(StudentDTO.class))).thenReturn(retFor1);
        StudentDTO res = studentController.partialUpdateStudent(1000100575, patch);
        assertNotNull(res);
        assertEquals("Parcial", res.getName());
        StudentDTO result = studentController.partialUpdateStudent(1, patch);

        assertNotNull(result);
        assertEquals("Robinson Nuñez", result.getName());
    }

    @Test
    void shouldGetStudentRequests() {
        ChangeRequestDTO r1 = new ChangeRequestDTO(); r1.setId(UUID.randomUUID());
        when(studentService.getStudentRequests(1000100575)).thenReturn(List.of(r1));
        List<ChangeRequestDTO> res = studentController.getStudentRequests(1000100575);

        assertEquals(1, res.size());
    }

    @Test
    void shouldGetStudentRequestsByStatusWhenProvided() {
        ChangeRequestDTO r1 = new ChangeRequestDTO(); r1.setId(UUID.randomUUID());
        when(studentService.getStudentRequestsByStatus(1000100575, RequestStatus.PENDING)).thenReturn(List.of(r1));
        List<ChangeRequestDTO> res = studentController.getStudentRequestsByStatus(1000100575, RequestStatus.PENDING);

        assertEquals(1, res.size());
    }

    @Test
    void shouldGetStudentRequestsByStatusWhenNullUsesAll() {
        ChangeRequestDTO r1 = new ChangeRequestDTO(); r1.setId(UUID.randomUUID());
        when(studentService.getStudentRequests(1000100575)).thenReturn(List.of(r1));
        List<ChangeRequestDTO> res = studentController.getStudentRequestsByStatus(1000100575, null);

        assertEquals(1, res.size());
    }

    @Test
    void shouldDeleteStudent() {
        doNothing().when(studentService).deleteStudent(1000100575);
        studentController.deleteStudent(1000100575);

        verify(studentService, times(1)).deleteStudent(1000100575);
    }

    @Test
    void shouldGetAcademicPlan() {
        AcademicPlanDTO dto = new AcademicPlanDTO();
        dto.setStudentId(1000100575);
        when(studentService.getAcademicPlan(1000100575)).thenReturn(dto);
        AcademicPlanDTO res = studentController.getAcademicPlan(1000100575);

        assertEquals(1000100575, res.getStudentId());
    }

    @Test
    void ShouldDeleteStudent() {
        doNothing().when(studentService).deleteStudent(1);
        studentController.deleteStudent(1);
        verify(studentService, times(1)).deleteStudent(1);
    }

    @Test
    void ShouldGetAcademicPlan() {
        AcademicPlanDTO plan = new AcademicPlanDTO();
        when(studentService.getAcademicPlan(1)).thenReturn(plan);
        AcademicPlanDTO result = studentController.getAcademicPlan(1);

        assertNotNull(result);
    }
}
