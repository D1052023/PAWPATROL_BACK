package eci.edu.dosw.proyecto;

import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eci.edu.dosw.proyecto.services.JwtService;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    void ShouldGenerateToken() {
        String email = "test@mail.escuelaing.edu.co";
        String role = "STUDENT";

        String token = jwtService.generateToken(email, role);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = jwtService.getClaims(token);
        assertEquals(email, claims.getSubject());
        assertEquals(role, claims.get("role"));
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().getTime() > System.currentTimeMillis());
    }

    @Test
    void ShouldGetEmailFromToken() {
        String email = "user@mail.escuelaing.edu.co";
        String role = "ADMIN";
        String token = jwtService.generateToken(email, role);

        String extractedEmail = jwtService.getEmailFromToken(token);
        assertEquals(email, extractedEmail);
    }

    @Test
    void ShouldGetRoleFromToken() {
        String email = "user@mail.escuelaing.edu.co";
        String role = "TEACHER";
        String token = jwtService.generateToken(email, role);

        String extractedRole = jwtService.getRoleFromToken(token);
        assertEquals(role, extractedRole);
    }

    @Test
    void ShouldIsTokenValid() {
        String token = jwtService.generateToken("student@mail.escuelaing.edu.co", "STUDENT");
        assertTrue(jwtService.isTokenValid(token));
    }

}
