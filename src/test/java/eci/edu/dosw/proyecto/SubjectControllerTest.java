package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.controller.SubjectController;
import eci.edu.dosw.proyecto.dtos.SubjectDTO;
import eci.edu.dosw.proyecto.services.SubjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubjectControllerTest {

    @Mock
    private SubjectService subjectService;

    @InjectMocks
    private SubjectController subjectController;

    private SubjectDTO subjectDTO;

    @BeforeEach
    void setUp() {
        subjectDTO = new SubjectDTO();
        subjectDTO.setSubjectId("ODSC");
        subjectDTO.setName("Organización de los Sistemas de Computo");
        subjectDTO.setCredits(3);
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

        assertNotNull(result);
        assertEquals("Organización de los Sistemas de Computo", result.getName());
    }

    @Test
    void ShouldTestPartialUpdateSubject() {
        when(subjectService.partialUpdateSubject("ODSC", subjectDTO)).thenReturn(subjectDTO);

        SubjectDTO result = subjectController.partialUpdateSubject("ODSC", subjectDTO);

        assertNotNull(result);
        assertEquals(3, result.getCredits());
    }

}
