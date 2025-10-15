package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.controller.StudentController;
import eci.edu.dosw.proyecto.dtos.*;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.services.StudentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    private StudentDTO studentDTO;

    @BeforeEach
    void setup() {
        studentDTO = new StudentDTO();
        studentDTO.setId(1);
        studentDTO.setName("Juan");
        studentDTO.setEmail("juan@mail.com");
    }

    @Test
    void ShouldReturnAllStudents() {
        when(studentService.getAllStudents()).thenReturn(List.of(studentDTO));

        List<StudentDTO> result = studentController.getAllStudents();

        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getName());
    }

    @Test
    void ShouldReturnStudentById() {
        when(studentService.getStudentById(1)).thenReturn(studentDTO);

        StudentDTO result = studentController.getStudentById(1);

        assertNotNull(result);
        assertEquals("Juan", result.getName());
    }

    @Test
    void ShouldCreateStudent() {
        when(studentService.createStudent(studentDTO)).thenReturn(studentDTO);

        StudentDTO result = studentController.createStudent(studentDTO);

        assertEquals(studentDTO.getEmail(), result.getEmail());
    }

    @Test
    void ShouldUpdateStudent() {
        StudentDTO updated = new StudentDTO();
        updated.setId(1);
        updated.setName("Juan Carlos");

        when(studentService.updateStudent(1, updated)).thenReturn(updated);

        StudentDTO result = studentController.updateStudent(1, updated);

        assertEquals("Juan Carlos", result.getName());
    }

    @Test
    void ShouldPartiallyUpdateStudent() {
        StudentDTO partial = new StudentDTO();
        partial.setName("Robinson Nuñez");

        when(studentService.partialUpdateStudent(1, partial)).thenReturn(partial);

        StudentDTO result = studentController.partialUpdateStudent(1, partial);

        assertEquals("Robinson Nuñez", result.getName());
    }

    @Test
    void ShouldGetStudentRequests() {
        ChangeRequestDTO request = new ChangeRequestDTO();
        when(studentService.getStudentRequests(1)).thenReturn(List.of(request));

        List<ChangeRequestDTO> result = studentController.getStudentRequests(1);

        assertEquals(1, result.size());
    }

    @Test
    void ShouldGetStudentRequestsByStatusWhenStatusProvided() {
        ChangeRequestDTO req = new ChangeRequestDTO();
        when(studentService.getStudentRequestsByStatus(1, RequestStatus.APPROVED)).thenReturn(List.of(req));

        List<ChangeRequestDTO> result = studentController.getStudentRequestsByStatus(1, RequestStatus.APPROVED);

        assertEquals(1, result.size());
    }

    @Test
    void ShouldGetStudentRequestsByStatusWhenStatusIsNull() {
        ChangeRequestDTO req = new ChangeRequestDTO();
        when(studentService.getStudentRequests(1)).thenReturn(List.of(req));

        List<ChangeRequestDTO> result = studentController.getStudentRequestsByStatus(1, null);

        assertEquals(1, result.size());
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
