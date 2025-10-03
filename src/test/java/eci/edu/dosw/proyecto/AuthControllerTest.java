package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.controller.AuthController;
import eci.edu.dosw.proyecto.dtos.LoginDTO;
import eci.edu.dosw.proyecto.dtos.LoginResponse;
import eci.edu.dosw.proyecto.models.Student;
import eci.edu.dosw.proyecto.services.JwtService;
import eci.edu.dosw.proyecto.services.StudentService;
import eci.edu.dosw.proyecto.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private StudentService studentService;

    @InjectMocks
    private AuthController controller;

    private LoginDTO loginDTO;
    private Student student;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loginDTO = new LoginDTO();
        loginDTO.setEmail("juan.ccastellanos@mail.escuelaing.edu.co");
        loginDTO.setPassword("12345");

        student = new Student();
        student.setEmail("juan.ccastellanos@mail.escuelaing.edu.co");
        student.setRole(Role.STUDENT);
    }

    @Test
    void ShouldLoginSuccessfully() {
        String fakeToken = "jwt-token-123";

        when(studentService.getStudentByEmail("juan.ccastellanos@mail.escuelaing.edu.co")).thenReturn(student);
        when(jwtService.generateToken("juan.ccastellanos@mail.escuelaing.edu.co", "STUDENT")).thenReturn(fakeToken);

        LoginResponse response = controller.login(loginDTO);

        assertNotNull(response);
        assertEquals(fakeToken, response.getToken());
    }

    @Test
    void ShouldThrowUnauthorizedWhenStudentNotFound() {
        when(studentService.getStudentByEmail("juan.ccastellanos@mail.escuelaing.edu.co")).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> controller.login(loginDTO));

        assertEquals(401, exception.getStatusCode().value());
        assertEquals("Credenciales incorrectas", exception.getReason());
    }
}
