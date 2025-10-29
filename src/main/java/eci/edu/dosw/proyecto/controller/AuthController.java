package eci.edu.dosw.proyecto.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;

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

    @Operation(summary = "Registrar nuevo usuario", description = "Registra un nuevo usuario en el sistema asignando el rol automáticamente según el correo.")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO register(@RequestBody RegisterDTO request) {
        String email = request.getEmail().toLowerCase();

        if (authUserService.getByEmail(email) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }

        Role role;

        if (email.startsWith("ad@") && email.endsWith("@mail.escuelaing.edu.co")) {
            role = Role.ADMIN;
        } else if (email.startsWith("se@") && email.endsWith("@escuelaing.edu.co")) {
            role = Role.SECRETARIAT;
        } else if (email.endsWith("@mail.escuelaing.edu.co")) {
            role = Role.STUDENT;
        } else if (email.endsWith("@escuelaing.edu.co")) {
            role = Role.DEANERY;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe usar un correo institucional válido");
        }

        AuthUser newUser = new AuthUser();
        newUser.setEmail(email);
        newUser.setRole(role);
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        AuthUser saved = authUserService.saveUser(newUser);

        return new UserResponseDTO(saved.getId(), saved.getEmail(), saved.getRole());
    }
}
