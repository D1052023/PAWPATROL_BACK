package eci.edu.dosw.proyecto.repositories;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import eci.edu.dosw.proyecto.models.AuthUser;

/**
 * Interfaz para el repositorio de usuarios autenticados.
 */
public interface AuthUserRepository extends MongoRepository<AuthUser, String> {
    Optional<AuthUser> findByEmail(String email);
}