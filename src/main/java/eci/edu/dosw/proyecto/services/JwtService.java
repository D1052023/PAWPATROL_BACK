package eci.edu.dosw.proyecto.services;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

/**
 * Clase que es un servicio de Spring encargado de generar tokens JWT que permiten autenticar a los usuarios.
 */
@Service
public class JwtService {

    private final Key key = Keys.hmacShaKeyFor(
        "UltraSecretoDestroy9778123456789012".getBytes(StandardCharsets.UTF_8)
    );

    /**
     * Genera un token JWT para el usuario dado.
     */
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email) 
                .claim("role", role) 
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1h
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    } 
}
