package eci.edu.dosw.proyecto;

import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eci.edu.dosw.proyecto.services.JwtService;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Date;

class JwtServiceTest {

    private JwtService jwtService;
    private KeyPair kp;

    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        kp = kpg.generateKeyPair();
        jwtService = new JwtService(kp.getPrivate(), kp.getPublic());
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

    @Test
    void ShouldBeInvalidExpiredToken() {
        String expired = Jwts.builder()
                .setSubject("expired@mail.escuelaing.edu.co")
                .claim("role", "STUDENT")
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 2)) // hace 2 horas
                .setExpiration(new Date(System.currentTimeMillis() - 1000L))
                .signWith(kp.getPrivate(), SignatureAlgorithm.RS256)
                .compact();

        assertFalse(jwtService.isTokenValid(expired));
    }

    @Test
    void ShouldBeInvalidMalformedTokenShouldBeInvalid() {
        String bad = "this.is.not.a.jwt";
        assertFalse(jwtService.isTokenValid(bad));
        assertThrows(JwtException.class, () -> jwtService.getClaims(bad));
    }

    @Test
    void ShouldBeInvalidNullToken() {
        assertFalse(jwtService.isTokenValid(null));
    }

    @Test
    void ShouldBeInvalidTokenWithInvalidSignature() throws Exception {
        KeyPairGenerator kpg2 = KeyPairGenerator.getInstance("RSA");
        kpg2.initialize(2048);
        KeyPair other = kpg2.generateKeyPair();
        String tokenSignedByOther = Jwts.builder()
                .setSubject("hacker@mail.escuelaing.edu.co")
                .claim("role", "ADMIN")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60))
                .signWith(other.getPrivate(), SignatureAlgorithm.RS256)
                .compact();

        assertFalse(jwtService.isTokenValid(tokenSignedByOther));
        assertThrows(JwtException.class, () -> jwtService.getClaims(tokenSignedByOther));
    }
}