package eci.edu.dosw.proyecto.repositories;

import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.models.Deanery;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Interfaz que maneja el repositorio de decanatura.
 */
@Repository
public interface DeaneryRepository extends MongoRepository<Deanery, Integer> {
    Deanery findByEmail(String email);
    Optional<Deanery> findByFaculty(Faculty faculty);
}
