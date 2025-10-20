package eci.edu.dosw.proyecto.configs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;

import eci.edu.dosw.proyecto.models.AuthUser;
import eci.edu.dosw.proyecto.enums.Role;
import eci.edu.dosw.proyecto.services.AuthUserService;


/**
 * Clase de configuración que se ejecuta al iniciar la aplicación y garantiza que exista el
 * usuario ADMIN por defecto en la base de datos.
 */
@Configuration
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final AuthUserService authUserService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;


    @Override
    public void run(String... args) {
        if (authUserService.getByEmail(adminEmail) == null) {
            AuthUser admin = new AuthUser();
            admin.setEmail(adminEmail);
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            authUserService.saveUser(admin);

            System.out.println("Usuario ADMIN creado automáticamente: " + adminEmail);
        } else {
            System.out.println("Usuario ADMIN ya existe en la base de datos.");
        }
    }
}