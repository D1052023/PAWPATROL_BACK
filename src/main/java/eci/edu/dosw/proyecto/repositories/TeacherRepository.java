package eci.edu.dosw.proyecto.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import eci.edu.dosw.proyecto.models.Teacher;


/**
 * Interfaz para el repositorio de los profesores.
 */
public interface TeacherRepository extends MongoRepository<Teacher, Integer> {
    Optional<Teacher> findByEmail(String email);
    boolean existsByEmail(String email);
}
