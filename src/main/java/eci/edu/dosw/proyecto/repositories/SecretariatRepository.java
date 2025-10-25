package eci.edu.dosw.proyecto.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import eci.edu.dosw.proyecto.enums.Faculty;
import org.springframework.data.mongodb.repository.MongoRepository;

import eci.edu.dosw.proyecto.models.Secretariat;

/**
 * Interfaz para el repositorio de la secretaria académica.
 */
public interface SecretariatRepository extends MongoRepository<Secretariat, Integer> {
    Optional<Secretariat> findByFaculty(Faculty faculty);
    Optional<Secretariat> findFirstByOrderByRequestStartDateDesc();
    Optional<Secretariat> findFirstByRequestStartDateBeforeAndRequestEndDateAfter(LocalDateTime now1, LocalDateTime now2);
}
