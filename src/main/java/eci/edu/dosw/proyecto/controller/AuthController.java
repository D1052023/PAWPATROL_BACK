package eci.edu.dosw.proyecto.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import eci.edu.dosw.proyecto.dtos.LoginDTO;
import eci.edu.dosw.proyecto.dtos.LoginResponse;
import eci.edu.dosw.proyecto.dtos.RegisterDTO;
import eci.edu.dosw.proyecto.dtos.UserResponseDTO;
import eci.edu.dosw.proyecto.enums.Role;
import eci.edu.dosw.proyecto.models.AuthUser;
import eci.edu.dosw.proyecto.services.JwtService;
import eci.edu.dosw.proyecto.services.AuthUserService;

/**
 * Clase controlador para autenticación y registro de usuarios en SIRHA.
 */
@Tag(name = "Autenticación", description = "Endpoints para el inicio de sesión y registro de usuarios en SIRHA")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final AuthUserService authUserService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Operation(summary = "Iniciar sesión", description = "Permite a un usuario autenticarse en el sistema proporcionando su correo electrónico y contraseña.")
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginDTO request) {
        AuthUser user = authUserService.getByEmail(request.getEmail());

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new LoginResponse(token);
    }

    @Operation(summary = "Registrar nuevo usuario", description = "Registra un nuevo usuario en el sistema con un rol determinado.")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO register(@RequestBody RegisterDTO request) {
        if (authUserService.getByEmail(request.getEmail()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }

        if (request.getRole() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar un rol válido");
        }
        Role role = request.getRole();

        String email = request.getEmail().toLowerCase();

        if (role == Role.STUDENT && !email.endsWith("@mail.escuelaing.edu.co")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe usar un correo institucional");
        }
        if ((role == Role.DEANERY || role == Role.SECRETARIAT)
                && !email.endsWith("@escuelaing.edu.co")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe usar un correo institucional");
        }

        AuthUser newUser = new AuthUser();
        newUser.setEmail(email);
        newUser.setRole(role);
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        AuthUser saved = authUserService.saveUser(newUser);

        return new UserResponseDTO(saved.getId(), saved.getEmail(), saved.getRole());
    }
}