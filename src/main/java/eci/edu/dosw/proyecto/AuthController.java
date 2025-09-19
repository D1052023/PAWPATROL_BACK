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
    private final UserRepository userRepository;

    public AuthController(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody Login request) {
        User user = userRepository.findById(request.getUsername())
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setId(request.getUsername());
                        newUser.setName(request.getUsername());
                        newUser.setEmail(request.getUsername() + "@gmail.com");
                        return userRepository.save(newUser);
                    });

        String token = jwtService.generateToken(user.getId());
        return ResponseEntity.ok(new LoginResponse(token));
    }

}
