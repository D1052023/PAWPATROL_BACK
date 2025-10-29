package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.controller.AuthController;
import eci.edu.dosw.proyecto.dtos.*;
import eci.edu.dosw.proyecto.enums.Role;
import eci.edu.dosw.proyecto.models.AuthUser;
import eci.edu.dosw.proyecto.services.AuthUserService;
import eci.edu.dosw.proyecto.services.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthUserService authUserService;

    @InjectMocks
    private AuthController authController;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void registerAdminWithCorrectPrefixAndDomainAssignsAdminRole() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("ad@admin@mail.escuelaing.edu.co");
        dto.setPassword("1234");

        when(authUserService.getByEmail(dto.getEmail().toLowerCase())).thenReturn(null);
        when(authUserService.saveUser(any(AuthUser.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponseDTO response = authController.register(dto);

        assertEquals(Role.ADMIN, response.getRole());
    }

    @Test
    void registerSecretariatWithCorrectPrefixAndDomainAssignsSecretariatRole() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("se@office@escuelaing.edu.co");
        dto.setPassword("1234");

        when(authUserService.getByEmail(dto.getEmail().toLowerCase())).thenReturn(null);
        when(authUserService.saveUser(any(AuthUser.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponseDTO response = authController.register(dto);

        assertEquals(Role.SECRETARIAT, response.getRole());
    }

    @Test
    void registerStudentWithMailEscuelaingDomainAssignsStudentRole() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("student@mail.escuelaing.edu.co");
        dto.setPassword("1234");

        when(authUserService.getByEmail(dto.getEmail().toLowerCase())).thenReturn(null);
        when(authUserService.saveUser(any(AuthUser.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponseDTO response = authController.register(dto);

        assertEquals(Role.STUDENT, response.getRole());
    }

    @Test
    void registerDeaneryWithEscuelaingDomainAssignsDeaneryRole() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("professor@escuelaing.edu.co");
        dto.setPassword("1234");

        when(authUserService.getByEmail(dto.getEmail().toLowerCase())).thenReturn(null);
        when(authUserService.saveUser(any(AuthUser.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponseDTO response = authController.register(dto);

        assertEquals(Role.DEANERY, response.getRole());
    }

    @Test
    void registerWithInvalidEmailThrowsBadRequest() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("user@gmail.com");
        dto.setPassword("1234");

        when(authUserService.getByEmail(dto.getEmail().toLowerCase())).thenReturn(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            authController.register(dto);
        });

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void registerWithExistingEmailThrowsConflict() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("existing@mail.escuelaing.edu.co");
        dto.setPassword("1234");

        when(authUserService.getByEmail(dto.getEmail().toLowerCase())).thenReturn(new AuthUser());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            authController.register(dto);
        });

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void loginWithValidCredentialsReturnsToken() {
        AuthUser user = new AuthUser();
        user.setEmail("student@mail.escuelaing.edu.co");
        user.setPasswordHash(passwordEncoder.encode("1234"));
        user.setRole(Role.STUDENT);

        LoginDTO dto = new LoginDTO();
        dto.setEmail(user.getEmail());
        dto.setPassword("1234");

        when(authUserService.getByEmail(user.getEmail())).thenReturn(user);
        when(jwtService.generateToken(user.getEmail(), user.getRole().name())).thenReturn("mockedToken");

        LoginResponse response = authController.login(dto);

        assertEquals("mockedToken", response.getToken());
    }

    @Test
    void loginWithWrongPasswordThrowsUnauthorized() {
        AuthUser user = new AuthUser();
        user.setEmail("student@mail.escuelaing.edu.co");
        user.setPasswordHash(passwordEncoder.encode("1234"));
        user.setRole(Role.STUDENT);

        LoginDTO dto = new LoginDTO();
        dto.setEmail(user.getEmail());
        dto.setPassword("wrongpass");

        when(authUserService.getByEmail(user.getEmail())).thenReturn(user);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            authController.login(dto);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void loginWithNonExistingUserThrowsUnauthorized() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("noexist@mail.escuelaing.edu.co");
        dto.setPassword("1234");

        when(authUserService.getByEmail(dto.getEmail())).thenReturn(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            authController.login(dto);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void registerAdminCorrectEmailAssignsAdminRole() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("ad@mail.escuelaing.edu.co");
        dto.setPassword("1234");

        when(authUserService.getByEmail(anyString())).thenReturn(null);
        when(authUserService.saveUser(any(AuthUser.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponseDTO response = authController.register(dto);

        assertEquals(Role.ADMIN, response.getRole());
    }

    @Test
    void registerAdminWrongEndDoesNotAssignAdmin() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("ad@gmail.com"); 
        dto.setPassword("1234");

        when(authUserService.getByEmail(anyString())).thenReturn(null);

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> authController.register(dto)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void registerSecretariatCorrectEmailAssignsSecretariatRole() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("se@escuelaing.edu.co");
        dto.setPassword("1234");

        when(authUserService.getByEmail(anyString())).thenReturn(null);
        when(authUserService.saveUser(any(AuthUser.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponseDTO response = authController.register(dto);

        assertEquals(Role.SECRETARIAT, response.getRole());
    }


    @Test
    void registerSecretariatWrongEndThrowsBadRequest() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("se@gmail.com"); 
        dto.setPassword("1234");

        when(authUserService.getByEmail(anyString())).thenReturn(null);

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> authController.register(dto)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }


}
