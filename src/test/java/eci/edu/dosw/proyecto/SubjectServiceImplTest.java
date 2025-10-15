package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.dtos.SubjectDTO;
import eci.edu.dosw.proyecto.enums.Curriculum;
import eci.edu.dosw.proyecto.enums.SubjectStatus;
import eci.edu.dosw.proyecto.enums.SubjectType;
import eci.edu.dosw.proyecto.mappers.SubjectMapper;
import eci.edu.dosw.proyecto.models.Group;
import eci.edu.dosw.proyecto.models.ScheduleEntry;
import eci.edu.dosw.proyecto.models.Subject;
import eci.edu.dosw.proyecto.models.Student;
import eci.edu.dosw.proyecto.repositories.GroupRepository;
import eci.edu.dosw.proyecto.repositories.StudentRepository;
import eci.edu.dosw.proyecto.repositories.SubjectRepository;
import eci.edu.dosw.proyecto.repositories.TeacherRepository;
import eci.edu.dosw.proyecto.services.HistoryService;
import eci.edu.dosw.proyecto.services.impl.SubjectServiceImpl;
import eci.edu.dosw.proyecto.util.MessageExceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SubjectServiceImplTest {

    @InjectMocks
    private SubjectServiceImpl subjectService;

    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private SubjectMapper subjectMapper;
    @Mock
    private HistoryService historyService;
    @Mock
    private MessageExceptions message;
    @Mock
    private StudentRepository studentRepository;

    private SubjectDTO dto;
    private Subject subject;
    private Group group;
    private Student student;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        dto = new SubjectDTO();
        dto.setSubjectId("ODSC");
        dto.setName("Organización de los Sistemas de Computo");
        dto.setCredits(3);
        dto.setMaximumCapacity(50);
        dto.setCurriculum(Curriculum.ISIS_15);
        dto.setType(SubjectType.MANDATORY);
        dto.setSubjectStatus(SubjectStatus.OPEN);
        dto.setPrerequisites(Arrays.asList("MAT1000100516"));
        dto.setDescription("Descripción");

        subject = new Subject();
        subject.setSubjectId("ODSC");
        subject.setMaximumCapacity(50);

        group = new Group();
        group.setMaximumCapacity(20);

        student = new Student();
        student.setId(1000100516);
        student.setEnrolledSubjects(new ArrayList<>());
        student.setSchedule(new ArrayList<>());
    }  

    @Test
    void ShouldReturnSubjectsByTeacher() {
        int teacherId = 1001;

        Subject subject1 = new Subject();
        subject1.setSubjectId("ODSC");
        Subject subject2 = new Subject();
        subject2.setSubjectId("DDYA");

        List<Subject> subjects = Arrays.asList(subject1, subject2);
        List<SubjectDTO> subjectDTOs = Arrays.asList(new SubjectDTO(), new SubjectDTO());

        when(subjectRepository.findByTeacherId(teacherId)).thenReturn(subjects);
        when(subjectMapper.toDTOList(subjects)).thenReturn(subjectDTOs);

        List<SubjectDTO> result = subjectService.getSubjectsByTeacher(teacherId);

        assertNotNull(result);
        assertEquals(2, result.size());
    }




    @Test
    void ShouldGetAllSubjects() {
        when(subjectRepository.findAll()).thenReturn(Arrays.asList(subject));
        when(subjectMapper.toDTOList(Arrays.asList(subject))).thenReturn(Arrays.asList(dto));

        List<SubjectDTO> result = subjectService.getAllSubjects();
        assertEquals(1, result.size());
    }

    @Test
    void ShouldGetSubjectByIdFound() {
        when(message.findSubjectOrThrow("ODSC")).thenReturn(subject);
        when(subjectMapper.toDTO(subject)).thenReturn(dto);

        SubjectDTO result = subjectService.getSubjectById("ODSC");
        assertEquals("ODSC", result.getSubjectId());
    }

    @Test
    void ShouldThrowSubjectByIdNotFound() {
        when(message.findSubjectOrThrow("ODSC"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Materia no encontrada"));
        assertThrows(ResponseStatusException.class,
                () -> subjectService.getSubjectById("ODSC"));
    }

    @Test
    void ShouldUpdateSubjectSuccessfully() {
        when(subjectRepository.existsBySubjectId("ODSC")).thenReturn(true);
        when(subjectMapper.toModel(dto)).thenReturn(subject);
        when(subjectRepository.save(subject)).thenReturn(subject);
        when(subjectMapper.toDTO(subject)).thenReturn(dto);

        SubjectDTO result = subjectService.updateSubject("ODSC", dto);
        assertEquals("ODSC", result.getSubjectId());
    }

    @Test
    void ShouldThrowUpdateSubjectNotFound() {
        when(subjectRepository.existsBySubjectId("ODSC")).thenReturn(false);
        assertThrows(RuntimeException.class,
                () -> subjectService.updateSubject("ODSC", dto));
    }



    @Test
    void ShouldDeleteSubject() {
        subjectService.deleteSubject("ODSC");
        verify(subjectRepository, times(1)).deleteById("ODSC");
    }

    @Test
    void ShouldUpdateNameAndCreditsIfPresent() {
        when(message.findSubjectOrThrow("ODSC")).thenReturn(subject);
        when(groupRepository.findBySubjectId("ODSC")).thenReturn(Collections.emptyList());
        when(subjectRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(subjectMapper.toDTO(any())).thenAnswer(invocation -> {
            Subject arg = invocation.getArgument(0);
            SubjectDTO updatedDto = new SubjectDTO();
            updatedDto.setName(arg.getName());
            updatedDto.setCredits(arg.getCredits());
            return updatedDto;
        });

        SubjectDTO updateDto = new SubjectDTO();
        updateDto.setName("DDYA");
        updateDto.setCredits(5);

        SubjectDTO result = subjectService.partialUpdateSubject("ODSC", updateDto);

        assertEquals("DDYA", result.getName()); 
        assertEquals(5, result.getCredits());
    }


    @Test
    void ShouldUpdateMaximumCapacityWhenValid() {
        when(message.findSubjectOrThrow("ODSC")).thenReturn(subject);
        when(groupRepository.findBySubjectId("ODSC")).thenReturn(Arrays.asList(group));
        when(subjectRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(subjectMapper.toDTO(any())).thenReturn(dto);

        dto.setMaximumCapacity(30); 
        SubjectDTO result = subjectService.partialUpdateSubject("ODSC", dto);

        assertEquals(30, result.getMaximumCapacity());
        assertEquals(30, subject.getMaximumCapacity());
    }

    @Test
    void ShouldThrowIfMaximumCapacityLessThanSumGroups() {
        when(subjectRepository.findBySubjectId("ODSC")).thenReturn(Optional.of(subject));
        group.setMaximumCapacity(20);
        when(groupRepository.findBySubjectId("ODSC")).thenReturn(Arrays.asList(group));

        dto.setMaximumCapacity(10);
        assertThrows(RuntimeException.class,
                () -> subjectService.partialUpdateSubject("ODSC", dto));
    }

    @Test
    void ShouldInitializeEnrolledSubjectsIfNull() {
        student.setEnrolledSubjects(null);
        when(message.findSubjectOrThrow("ODSC")).thenReturn(subject);
        when(message.findStudentOrThrow(1000100516)).thenReturn(student);
        doNothing().when(message).ensureCurriculumMatchesStudent(student, subject);
        when(subjectMapper.toDTO(subject)).thenReturn(dto);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        subjectService.assignStudentToSubject("ODSC", 1000100516);

        assertNotNull(student.getEnrolledSubjects());
        assertTrue(student.getEnrolledSubjects().contains("ODSC"));
    }

    @Test
    void ShouldRemoveStudentFromEnrolledAndSchedule() {
        student.getEnrolledSubjects().add("ODSC");
        ScheduleEntry entry = new ScheduleEntry();
        entry.setSubject("ODSC");
        student.getSchedule().add(entry);

        when(message.findSubjectOrThrow("ODSC")).thenReturn(subject);
        when(message.findStudentOrThrow(1000100516)).thenReturn(student);
        when(subjectMapper.toDTO(subject)).thenReturn(dto);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        subjectService.removeStudentFromSubject("ODSC", 1000100516);

        assertFalse(student.getEnrolledSubjects().contains("ODSC"));
        assertTrue(student.getSchedule().isEmpty());
    }

    
}
