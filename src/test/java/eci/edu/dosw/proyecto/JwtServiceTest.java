package eci.edu.dosw.proyecto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken("Juan Caballero");

        assertNotNull(token);
        assertTrue(token.length() > 20); 
    }
}

