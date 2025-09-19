package eci.edu.dosw.proyecto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthControllerTest {

    private JwtService jwtService; 
    @Mock
    private UserRepository userRepository;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtService = new JwtService(); 
        authController = new AuthController(jwtService, userRepository);
    }

    @Test
    void testShouldLogin() {
        Login request = new Login();
        request.setUsername("nuevo");

        when(userRepository.findById("nuevo")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<LoginResponse> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jwtService.generateToken("nuevo"), response.getBody().getToken());
    }

    @Test
    void testShouldBeLoginWhenTheUserExists() {
        Login request = new Login();
        request.setUsername("juan");

        User existing = new User("juan", "Juan", "juan@mail.com");
        when(userRepository.findById("juan")).thenReturn(Optional.of(existing));

        ResponseEntity<LoginResponse> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jwtService.generateToken("juan"), response.getBody().getToken());
    }
}
