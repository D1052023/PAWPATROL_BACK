package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.dtos.TeacherDTO;
import eci.edu.dosw.proyecto.models.Teacher;
import eci.edu.dosw.proyecto.repositories.TeacherRepository;
import eci.edu.dosw.proyecto.services.impl.TeacherServiceImpl;
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

class TeacherServiceImplTest {

    @InjectMocks
    private TeacherServiceImpl teacherService;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private eci.edu.dosw.proyecto.mappers.TeacherMapper teacherMapper;

    @Mock
    private MessageExceptions message;

    private Teacher teacher;
    private TeacherDTO teacherDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        teacher = new Teacher();
        teacher.setId(1);
        teacher.setName("Martin Cantor");
        teacher.setEmail("andres.cantor-u@escuelaing.edu.co");

        teacherDTO = new TeacherDTO();
        teacherDTO.setId(1);
        teacherDTO.setName("Martin Cantor");
        teacherDTO.setEmail("andres.cantor-u@escuelaing.edu.co");

        when(teacherMapper.toDTO(any(Teacher.class))).thenAnswer(invocation -> {
            Teacher t = invocation.getArgument(0);
            TeacherDTO dto = new TeacherDTO();
            dto.setId(t.getId());
            dto.setName(t.getName());
            dto.setEmail(t.getEmail());
            return dto;
        });

        when(message.findTeacherOrThrow(1)).thenReturn(teacher);
        doNothing().when(message).ensureTeacherEmailNotRegisteredForCreate(anyString());
        doNothing().when(message).ensureTeacherEmailNotRegisteredForUpdate(anyInt(), anyString());
    }

    @Test
    void ShouldGetTeacherByIdFound() {
        when(teacherRepository.findById(1)).thenReturn(Optional.of(teacher));

        TeacherDTO result = teacherService.getTeacherById(1);

        assertEquals("Martin Cantor", result.getName());
        assertEquals("andres.cantor-u@escuelaing.edu.co", result.getEmail());
    }

    @Test
    void ShouldThrowTeacherByIdNotFound() {
        when(message.findTeacherOrThrow(1)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesor no encontrado con id: 1"));

        assertThrows(RuntimeException.class, () -> teacherService.getTeacherById(1));
    }

    @Test
    void ShouldGetAllTeachers() {
        when(teacherRepository.findAll()).thenReturn(Arrays.asList(teacher));

        List<TeacherDTO> result = teacherService.getAllTeachers();

        assertEquals(1, result.size());
        assertEquals("Martin Cantor", result.get(0).getName());
    }

    @Test
    void ShouldCreateTeacherSuccessfully() {
        when(teacherMapper.toEntity(teacherDTO)).thenReturn(teacher);
        when(teacherRepository.save(teacher)).thenReturn(teacher);

        TeacherDTO result = teacherService.createTeacher(teacherDTO);

        assertEquals("Martin Cantor", result.getName());
        assertEquals("andres.cantor-u@escuelaing.edu.co", result.getEmail());
    }

    @Test
    void ShouldThrowCreateTeacherEmailExists() {
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email ya existe: " + teacherDTO.getEmail()))
                .when(message).ensureTeacherEmailNotRegisteredForCreate(teacherDTO.getEmail());
        assertThrows(RuntimeException.class, () -> teacherService.createTeacher(teacherDTO));
    }

    @Test
    void ShouldDeleteTeacherSuccessfully() {
        doNothing().when(teacherRepository).deleteById(1);

        teacherService.deleteTeacher(1);
        verify(teacherRepository, times(1)).deleteById(1);
    }

    @Test
    void ShouldThrowDeleteTeacherNotFound() {
        when(message.findTeacherOrThrow(1)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesor no encontrado con id: 1"));

        assertThrows(RuntimeException.class, () -> teacherService.deleteTeacher(1));
    }

    @Test
    void ShouldUpdateTeacherSuccessfully() {
        TeacherDTO updateDTO = new TeacherDTO();
        updateDTO.setName("Martin Cantor Updated");
        updateDTO.setEmail("andres.cantor-u@escuelaing.edu.co");

        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

        TeacherDTO result = teacherService.updateTeacher(1, updateDTO);

        assertEquals("Martin Cantor Updated", result.getName());
        assertEquals("andres.cantor-u@escuelaing.edu.co", result.getEmail());
    }

    @Test
    void ShouldThrowUpdateTeacherEmailExists() {
        TeacherDTO updateDTO = new TeacherDTO();
        updateDTO.setName("Martin Cantor Updated");
        updateDTO.setEmail("Uandres.cantor-u@escuelaing.edu.co");

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email ya registrado: " + updateDTO.getEmail()))
                .when(message).ensureTeacherEmailNotRegisteredForUpdate(1, updateDTO.getEmail());
        when(message.findTeacherOrThrow(1)).thenReturn(teacher);

        assertThrows(RuntimeException.class, () -> teacherService.updateTeacher(1, updateDTO));
    }

    @Test
    void ShouldThrowUpdateTeacherNotFound() {
        when(message.findTeacherOrThrow(1)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesor no encontrado con id: 1"));

        assertThrows(RuntimeException.class, () -> teacherService.updateTeacher(1, teacherDTO));
    }

    @Test
    void ShouldPartialUpdateTeacherName() {
        TeacherDTO updateDTO = new TeacherDTO();
        updateDTO.setName("Nuevo Nombre");

        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

        TeacherDTO result = teacherService.partialUpdateTeacher(1, updateDTO);

        assertEquals("Nuevo Nombre", result.getName());
    }

    @Test
    void ShouldPartialUpdateTeacherEmail() {
        TeacherDTO updateDTO = new TeacherDTO();
        updateDTO.setEmail("nuevo@mail.com");

        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

        TeacherDTO result = teacherService.partialUpdateTeacher(1, updateDTO);

        assertEquals("nuevo@mail.com", result.getEmail());
    }

    @Test
    void ShouldThrowPartialUpdateTeacherEmailExists() {
        TeacherDTO updateDTO = new TeacherDTO();
        updateDTO.setEmail("Uandres.cantor-u@escuelaing.edu.co");

        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Email ya registrado: " + updateDTO.getEmail()))
                .when(message).ensureTeacherEmailNotRegisteredForUpdate(1, updateDTO.getEmail());

        when(message.findTeacherOrThrow(1)).thenReturn(teacher);

        assertThrows(ResponseStatusException.class,
                () -> teacherService.partialUpdateTeacher(1, updateDTO));
    }

    @Test
    void ShouldThrowPartialUpdateTeacherNotFound() {
        TeacherDTO updateDTO = new TeacherDTO();
        when(message.findTeacherOrThrow(1)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesor no encontrado con id: 1"));

        assertThrows(ResponseStatusException.class,
                () -> teacherService.partialUpdateTeacher(1, updateDTO));
    }

    @Test
    void ShouldGetTeacherByEmailFound() {
        when(teacherRepository.findByEmail("andres.cantor-u@escuelaing.edu.co")).thenReturn(Optional.of(teacher));

        Optional<TeacherDTO> result = teacherService.getTeacherByEmail("andres.cantor-u@escuelaing.edu.co");

        assertTrue(result.isPresent());
        assertEquals("Martin Cantor", result.get().getName());
    }

    @Test
    void ShouldGetTeacherByEmailNotFound() {
        when(teacherRepository.findByEmail("Martin@mail.com")).thenReturn(Optional.empty());

        Optional<TeacherDTO> result = teacherService.getTeacherByEmail("Martin@mail.com");

        assertTrue(result.isEmpty());
    }

}
