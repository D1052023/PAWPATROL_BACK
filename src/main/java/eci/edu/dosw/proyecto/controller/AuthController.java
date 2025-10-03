package eci.edu.dosw.proyecto.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import eci.edu.dosw.proyecto.dtos.LoginDTO;
import eci.edu.dosw.proyecto.dtos.LoginResponse;
import eci.edu.dosw.proyecto.models.Student;
import eci.edu.dosw.proyecto.services.JwtService;
import eci.edu.dosw.proyecto.services.StudentService;

/**
 * Clase controlador que se encarga de la autenticación de los usuarios.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final StudentService studentService;

    public AuthController(JwtService jwtService, StudentService studentService) {
        this.jwtService = jwtService;
        this.studentService = studentService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginDTO request) {
        Student student = studentService.getStudentByEmail(request.getEmail());

        if (student == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
        }

        String token = jwtService.generateToken(student.getEmail(), student.getRole().name());
        return new LoginResponse(token);
    }
}
