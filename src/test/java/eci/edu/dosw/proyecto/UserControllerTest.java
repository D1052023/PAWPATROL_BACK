package eci.edu.dosw.proyecto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    @Test
    void testShouldGetAllUsers() {
        List<User> users = Collections.singletonList(new User("1", "Juan", "juan@mail.com"));
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userController.getAllUser();

        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getName());
    }

    @Test
    void testShouldGetUserByIdUserExists() {
        User user = new User("1", "Juan", "juan@mail.com");
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.getUserById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Juan", response.getBody().getName());
    }

    @Test
    void testShouldGetUserByIdUserNotFound() {
        when(userRepository.findById("2")).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserById("2");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testShouldCreateUser() {
        UserDTO dto = new UserDTO();
        dto.setName("Juan");
        dto.setEmail("juan@mail.com");

        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId("123"); 
            return u;
        });

        User created = userController.createUser(dto);

        assertNotNull(created.getId());
        assertEquals("Juan", created.getName());
        assertEquals("juan@mail.com", created.getEmail());
    }

    @Test
    void testShouldDeleteUser() {
        when(userRepository.existsById("1")).thenReturn(true);

        ResponseEntity<Void> response = userController.deleteUser("1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userRepository).deleteById("1");
    }

    @Test
    void testShouldDeleteUserNotFound() {
        when(userRepository.existsById("2")).thenReturn(false);

        ResponseEntity<Void> response = userController.deleteUser("2");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
