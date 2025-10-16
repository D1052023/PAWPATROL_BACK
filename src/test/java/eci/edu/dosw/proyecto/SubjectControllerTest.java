package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.controller.SubjectController;
import eci.edu.dosw.proyecto.dtos.SubjectDTO;
import eci.edu.dosw.proyecto.services.SubjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubjectControllerTest {

    @InjectMocks
    private SubjectController subjectController;

    @Mock
    private SubjectService subjectService;

    private SubjectDTO subjectDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        subjectDTO = new SubjectDTO();
        subjectDTO.setSubjectId("ODSC");
        subjectDTO.setName("Organización de los Sistemas de Computo");
        subjectDTO.setCredits(3);
        subjectDTO.setMaximumCapacity(50);
    }

    @Test
    void ShouldTestCreateSubject() {
        when(subjectService.createSubject(subjectDTO)).thenReturn(subjectDTO);

        SubjectDTO result = subjectController.createSubject(subjectDTO);

        assertNotNull(result);
        assertEquals("ODSC", result.getSubjectId());
    }

    @Test
    void ShouldTestGetAllSubjects() {
        when(subjectService.getAllSubjects()).thenReturn(Arrays.asList(subjectDTO));

        List<SubjectDTO> result = subjectController.getAllSubjects();

        assertEquals(1, result.size());
        assertEquals("Organización de los Sistemas de Computo", result.get(0).getName());
    }

    @Test
    void ShouldTestGetSubjectById() {
        when(subjectService.getSubjectById("ODSC")).thenReturn(subjectDTO);

        SubjectDTO result = subjectController.getSubjectById("ODSC");

        assertNotNull(result);
        assertEquals("ODSC", result.getSubjectId());
    }

    @Test
    void ShouldTestUpdateSubject() {
        when(subjectService.updateSubject("ODSC", subjectDTO)).thenReturn(subjectDTO);

        SubjectDTO result = subjectController.updateSubject("ODSC", subjectDTO);

        assertEquals("Organización de los Sistemas de Computo", result.getName());
    }

    @Test
    void ShouldTestPartialUpdateSubject() {
        when(subjectService.partialUpdateSubject("ODSC", subjectDTO)).thenReturn(subjectDTO);

        SubjectDTO result = subjectController.partialUpdateSubject("ODSC", subjectDTO);

        assertEquals(3, result.getCredits());
    }

    @Test
    void ShouldDeleteSubject() {
        doNothing().when(subjectService).deleteSubject("ODSC");

        subjectController.deleteSubject("ODSC");
        verify(subjectService, times(1)).deleteSubject("ODSC");
    }

    @Test
    void ShouldGetSubjectsByTeacher() {
        when(subjectService.getSubjectsByTeacher(1)).thenReturn(Arrays.asList(subjectDTO));

        List<SubjectDTO> result = subjectController.getSubjectsByTeacher(1);

        assertEquals(1, result.size());
        assertEquals("ODSC", result.get(0).getSubjectId());
    }

    @SuppressWarnings("null")
    @Test
    void ShouldUpdateSubjectCapacity() {
        SubjectDTO dto = new SubjectDTO();
        dto.setMaximumCapacity(60);

        when(subjectService.partialUpdateSubject("ODSC", dto)).thenReturn(dto);

        ResponseEntity<SubjectDTO> response = subjectController.updateSubjectCapacity("ODSC", 60);

        assertNotNull(response.getBody());
        assertEquals(60, response.getBody().getMaximumCapacity());
    }

    @Test
    void ShouldAssignAndRemoveStudent() {
        when(subjectService.assignStudentToSubject("ODSC", 101)).thenReturn(subjectDTO);
        when(subjectService.removeStudentFromSubject("ODSC", 101)).thenReturn(subjectDTO);

        ResponseEntity<SubjectDTO> assigned = subjectController.assignStudentToSubject("ODSC", 101);
        assertEquals(subjectDTO, assigned.getBody());

        ResponseEntity<SubjectDTO> removed = subjectController.removeStudentFromSubject("ODSC", 101);
        assertEquals(subjectDTO, removed.getBody());
    }
}
