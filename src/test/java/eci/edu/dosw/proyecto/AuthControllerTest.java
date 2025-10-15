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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthUserService authUserService;

    @Mock
    private JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void ShouldLoginCorrectCredentials() {
        String email = "user@mail.escuelaing.edu.co";
        String password = "password123";

        AuthUser user = new AuthUser();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(Role.STUDENT);

        when(authUserService.getByEmail(email)).thenReturn(user);
        when(jwtService.generateToken(email, "STUDENT")).thenReturn("token-falso-prueba");

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(email);
        loginDTO.setPassword(password);

        LoginResponse response = authController.login(loginDTO);

        assertEquals("token-falso-prueba", response.getToken());
    }


    @Test
    void ShouldNotLoginWrongPassword() {
        String email = "user@mail.escuelaing.edu.co";

        AuthUser user = new AuthUser();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode("otherpassword"));
        user.setRole(Role.STUDENT);

        when(authUserService.getByEmail(email)).thenReturn(user);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(email);
        loginDTO.setPassword("wrongpassword");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authController.login(loginDTO));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void ShouldRegisterStudentWithValidEmail() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("student@mail.escuelaing.edu.co");
        registerDTO.setPassword("password123");
        registerDTO.setRole(Role.STUDENT);

        when(authUserService.getByEmail(registerDTO.getEmail())).thenReturn(null);

        AuthUser savedUser = new AuthUser();
        savedUser.setId("1000100516");
        savedUser.setEmail(registerDTO.getEmail());
        savedUser.setRole(Role.STUDENT);

        when(authUserService.saveUser(any(AuthUser.class))).thenReturn(savedUser);

        UserResponseDTO response = authController.register(registerDTO);

        assertEquals("1000100516", response.getId());
        assertEquals(registerDTO.getEmail(), response.getEmail());
        assertEquals(Role.STUDENT, response.getRole());
    }

    @Test
    void ShouldRegisterExistingEmail() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("existing@mail.escuelaing.edu.co");
        registerDTO.setPassword("password");
        registerDTO.setRole(Role.STUDENT);

        when(authUserService.getByEmail(registerDTO.getEmail())).thenReturn(new AuthUser());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authController.register(registerDTO));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    void ShouldRegisterInvalidStudentEmail() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("student@gmail.co");
        registerDTO.setPassword("password");
        registerDTO.setRole(Role.STUDENT);

        when(authUserService.getByEmail(registerDTO.getEmail())).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authController.register(registerDTO));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void ShouldThrowBadRequestWhenRoleIsNull() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("user@mail.escuelaing.edu.co");
        registerDTO.setPassword("password123");
        registerDTO.setRole(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authController.register(registerDTO));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void ShouldThrowBadRequestForStudentWithInvalidEmail() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("student@gmail.com");
        registerDTO.setPassword("password123");
        registerDTO.setRole(Role.STUDENT);

        when(authUserService.getByEmail(registerDTO.getEmail())).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authController.register(registerDTO));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void ShouldThrowBadRequestForDeaneryWithInvalidEmail() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("deanery@gmail.com"); 
        registerDTO.setPassword("password123");
        registerDTO.setRole(Role.DEANERY);

        when(authUserService.getByEmail(registerDTO.getEmail())).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authController.register(registerDTO));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void ShouldThrowBadRequestForSecretariatWithInvalidEmail() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("secretariat@gmail.com"); 
        registerDTO.setPassword("password123");
        registerDTO.setRole(Role.SECRETARIAT);

        when(authUserService.getByEmail(registerDTO.getEmail())).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authController.register(registerDTO));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void ShouldRegisterDeaneryWithValidEmail() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("decano@escuelaing.edu.co");
        registerDTO.setPassword("password123");
        registerDTO.setRole(Role.DEANERY);

        when(authUserService.getByEmail(registerDTO.getEmail())).thenReturn(null);

        AuthUser savedUser = new AuthUser();
        savedUser.setId("1000100575");
        savedUser.setEmail(registerDTO.getEmail());
        savedUser.setRole(Role.DEANERY);

        when(authUserService.saveUser(any(AuthUser.class))).thenReturn(savedUser);

        UserResponseDTO response = authController.register(registerDTO);

        assertEquals("1000100575", response.getId());
        assertEquals(registerDTO.getEmail(), response.getEmail());
        assertEquals(Role.DEANERY, response.getRole());
    }

    @Test
    void ShouldRegisterSecretariatWithValidEmail() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("secretaria@escuelaing.edu.co");
        registerDTO.setPassword("password123");
        registerDTO.setRole(Role.SECRETARIAT);

        when(authUserService.getByEmail(registerDTO.getEmail())).thenReturn(null);

        AuthUser savedUser = new AuthUser();
        savedUser.setId("1000100575");
        savedUser.setEmail(registerDTO.getEmail());
        savedUser.setRole(Role.SECRETARIAT);

        when(authUserService.saveUser(any(AuthUser.class))).thenReturn(savedUser);

        UserResponseDTO response = authController.register(registerDTO);

        assertEquals("1000100575", response.getId());
        assertEquals(registerDTO.getEmail(), response.getEmail());
        assertEquals(Role.SECRETARIAT, response.getRole());
    }

    @Test
    void ShouldNotLoginWhenUserIsNull() {
        String email = "nonexistent@mail.escuelaing.edu.co";
        String password = "password123";

        when(authUserService.getByEmail(email)).thenReturn(null);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(email);
        loginDTO.setPassword(password);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authController.login(loginDTO));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

}
