package eci.edu.dosw.proyecto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * Clase que es un controlador de Spring que se encarga en manejar peticiones
 * de la autenticación del uusario
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody Login request) {
        String token = jwtService.generateToken(request.getUsername());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
