package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.dtos.SubjectDTO;
import eci.edu.dosw.proyecto.enums.Curriculum;
import eci.edu.dosw.proyecto.enums.SubjectStatus;
import eci.edu.dosw.proyecto.enums.SubjectType;
import eci.edu.dosw.proyecto.mappers.SubjectMapper;
import eci.edu.dosw.proyecto.models.Subject;
import eci.edu.dosw.proyecto.repositories.SubjectRepository;
import eci.edu.dosw.proyecto.services.impl.SubjectServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SubjectServiceImplTest {

    private SubjectRepository subjectRepository;
    private SubjectMapper subjectMapper;
    private SubjectServiceImpl subjectService;

    @BeforeEach
    void setUp() {
        subjectRepository = Mockito.mock(SubjectRepository.class);
        subjectMapper = Mockito.mock(SubjectMapper.class);
        subjectService = new SubjectServiceImpl(subjectRepository, subjectMapper);
    }

    @Test
    void ShouldCreateSubject() {
        SubjectDTO dto = new SubjectDTO(
                "MATE01",
                "Matemáticas",
                3,
                Curriculum.ISIS_15,
                SubjectType.MANDATORY,
                SubjectStatus.OPEN,
                List.of("CALC01"),
                "Curso de matemáticas básicas"
        );

        Subject subject = new Subject();
        subject.setSubjectId("MATE01");
        subject.setName("Matemáticas");
        subject.setCredits(3);
        subject.setCurriculum(Curriculum.ISIS_15);
        subject.setType(SubjectType.MANDATORY);
        subject.setSubjectStatus(SubjectStatus.OPEN);
        subject.setPrerequisites(List.of("CALC01"));
        subject.setDescription("Curso de matemáticas básicas");

        Mockito.when(subjectMapper.toModel(dto)).thenReturn(subject);
        Mockito.when(subjectRepository.save(subject)).thenReturn(subject);
        Mockito.when(subjectMapper.toDTO(subject)).thenReturn(dto);

        SubjectDTO result = subjectService.createSubject(dto);

        assertEquals("MATE01", result.getSubjectId());
        assertEquals("Matemáticas", result.getName());
        assertEquals(3, result.getCredits());
        assertEquals(Curriculum.ISIS_15, result.getCurriculum());
        assertEquals(SubjectType.MANDATORY, result.getType());
        assertEquals(SubjectStatus.OPEN, result.getSubjectStatus());
        assertTrue(result.getPrerequisites().contains("CALC01"));
    }

    @Test
    void ShouldGetAllSubjects() {
        Subject subject = new Subject();
        subject.setSubjectId("MATE01");

        SubjectDTO dto = new SubjectDTO(
                "MATE01",
                "Matemáticas",
                3,
                Curriculum.ISIS_15,
                SubjectType.MANDATORY,
                SubjectStatus.OPEN,
                List.of(),
                "Curso básico"
        );

        Mockito.when(subjectRepository.findAll()).thenReturn(List.of(subject));
        Mockito.when(subjectMapper.toDTOList(List.of(subject))).thenReturn(List.of(dto));

        List<SubjectDTO> result = subjectService.getAllSubjects();

        assertEquals(1, result.size());
        assertEquals("MATE01", result.get(0).getSubjectId());
    }

    @Test
    void ShouldGetSubjectById() {
        Subject subject = new Subject();
        subject.setSubjectId("DOSW");

        SubjectDTO dto = new SubjectDTO(
                "DOSW", "Desarrollo de software", 3,
                Curriculum.ISIS_15, SubjectType.MANDATORY,
                SubjectStatus.OPEN, List.of(), "Curso básico"
        );

        Mockito.when(subjectRepository.findBySubjectId("DOSW")).thenReturn(Optional.of(subject));
        Mockito.when(subjectMapper.toDTO(subject)).thenReturn(dto);

        SubjectDTO result = subjectService.getSubjectById("DOSW");

        assertEquals("DOSW", result.getSubjectId());
    }

    @Test
    void ShouldNotGetSubjectById() {
        Mockito.when(subjectRepository.findBySubjectId("ODSC")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> subjectService.getSubjectById("ODCS"));
    }

    @Test
    void ShouldUpdateSubject() {
        SubjectDTO dto = new SubjectDTO(
                "MATE01", "Matemáticas Avanzadas", 4,
                Curriculum.ISIS_15, SubjectType.ELECTIVE,
                SubjectStatus.OPEN, List.of("CALC01"), "Curso avanzado"
        );

        Subject updated = new Subject();
        updated.setSubjectId("CALD");
        updated.setName("Matemáticas Avanzadas");
        updated.setCredits(4);
        updated.setCurriculum(Curriculum.ISIS_15);
        updated.setType(SubjectType.ELECTIVE);
        updated.setSubjectStatus(SubjectStatus.OPEN);
        updated.setPrerequisites(List.of("CALC01"));
        updated.setDescription("Curso avanzado");

        Mockito.when(subjectRepository.existsBySubjectId("CALD")).thenReturn(true);
        Mockito.when(subjectMapper.toModel(dto)).thenReturn(updated);
        Mockito.when(subjectRepository.save(updated)).thenReturn(updated);
        Mockito.when(subjectMapper.toDTO(updated)).thenReturn(dto);

        SubjectDTO result = subjectService.updateSubject("CALD", dto);

        assertEquals("Matemáticas Avanzadas", result.getName());
        assertEquals(4, result.getCredits());
        assertEquals(Curriculum.ISIS_15, result.getCurriculum());
        assertEquals(SubjectType.ELECTIVE, result.getType());
    }

    @Test
    void ShouldNotUpdateSubject() {
        SubjectDTO dto = new SubjectDTO("DDYA", "DISEÑO DATOS", 3, Curriculum.ISIS_15, null, null, null, null);
        Mockito.when(subjectRepository.existsBySubjectId("DDYA")).thenReturn(false);
        assertThrows(RuntimeException.class, () -> subjectService.updateSubject("DDYA", dto));
    }

    @Test
    void ShouldPartialUpdateSubjec() {
        SubjectDTO dto = new SubjectDTO(
                null, "Nuevo Nombre", 5,
                null, null, SubjectStatus.CLOSE,
                null, null
        );

        Subject existing = new Subject();
        existing.setSubjectId("MATE01");
        existing.setName("Viejo Nombre");
        existing.setCredits(3);
        existing.setCurriculum(Curriculum.ISIS_15);
        existing.setType(SubjectType.MANDATORY);
        existing.setSubjectStatus(SubjectStatus.OPEN);
        existing.setPrerequisites(List.of("CALC01"));
        existing.setDescription("Curso base");

        Mockito.when(subjectRepository.findBySubjectId("MATE01")).thenReturn(Optional.of(existing));
        Mockito.when(subjectRepository.save(existing)).thenReturn(existing);

        SubjectDTO expected = new SubjectDTO(
                "MATE01", "Nuevo Nombre", 5,
                Curriculum.ISIS_15, SubjectType.MANDATORY,
                SubjectStatus.CLOSE, List.of("CALC01"), "Curso base"
        );

        Mockito.when(subjectMapper.toDTO(existing)).thenReturn(expected);

        SubjectDTO result = subjectService.partialUpdateSubject("MATE01", dto);

        assertEquals("Nuevo Nombre", result.getName());
        assertEquals(5, result.getCredits());
        assertEquals(SubjectStatus.CLOSE, result.getSubjectStatus());
    }

    @Test
    void ShouldNotpartialUpdateSubject() {
        SubjectDTO dto = new SubjectDTO("ECDI", "Ecuaciones", 3, null, null, null, null, null);
        Mockito.when(subjectRepository.findBySubjectId("ECDI")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> subjectService.partialUpdateSubject("ECDI", dto));
    }

    @Test
    void ShouldDeleteSubject() {
        assertDoesNotThrow(() -> subjectService.deleteSubject("MATE01"));
    }
}
