package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.models.AuthUser;
import eci.edu.dosw.proyecto.repositories.AuthUserRepository;
import eci.edu.dosw.proyecto.services.impl.AuthUserServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

class AuthUserServiceImplTest {

    @InjectMocks
    private AuthUserServiceImpl authUserService;

    @Mock
    private AuthUserRepository repository;

    private AuthUser user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new AuthUser();
        user.setId("1000100516");
        user.setEmail("juan.ccastellanos@mail.escuelaing.edu.co");
        user.setPasswordHash("hashedpassword");
    }

    @Test
    void ShouldReturnUserWhenEmailExists() {
        when(repository.findByEmail("juan.ccastellanos@mail.escuelaing.edu.co")).thenReturn(Optional.of(user));

        AuthUser result = authUserService.getByEmail("juan.ccastellanos@mail.escuelaing.edu.co");

        assertNotNull(result);
        assertEquals("juan.ccastellanos@mail.escuelaing.edu.co", result.getEmail());
    }

    @Test
    void ShouldReturnNullWhenEmailDoesNotExist() {
        when(repository.findByEmail("robinhood@mail.escuelaing.edu.co")).thenReturn(Optional.empty());

        AuthUser result = authUserService.getByEmail("robinhood@mail.escuelaing.edu.co");

        assertNull(result);
    }

    @Test
    void ShouldSaveUserSuccessfully() {
        when(repository.save(user)).thenReturn(user);

        AuthUser saved = authUserService.saveUser(user);

        assertNotNull(saved);
        assertEquals("1000100516", saved.getId());
    }
}
