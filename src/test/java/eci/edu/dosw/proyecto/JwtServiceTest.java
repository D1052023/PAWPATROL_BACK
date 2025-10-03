package eci.edu.dosw.proyecto;

import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eci.edu.dosw.proyecto.services.JwtService;

import java.nio.charset.StandardCharsets;
import java.security.Key;

class JwtServiceTest {

    private JwtService jwtService;
    private Key key;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        key = Keys.hmacShaKeyFor("UltraSecretoDestroy9778123456789012".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void generateToken_shouldReturnValidToken() {
        String email = "test@example.com";
        String role = "STUDENT";

        String token = jwtService.generateToken(email, role);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(email, claims.getSubject());
        assertEquals(role, claims.get("role"));
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().getTime() > System.currentTimeMillis());
    }
}
