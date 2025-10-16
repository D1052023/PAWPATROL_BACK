package eci.edu.dosw.proyecto.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;
import eci.edu.dosw.proyecto.models.ChangeRequestHistory;

/**
 * Interfaz para el repositorio del historial de solicitudes.
 */
@Repository
public interface ChangeRequestHistoryRepository extends MongoRepository<ChangeRequestHistory, String> {
    List<ChangeRequestHistory> findByRequestIdOrderByTimestampAsc(UUID requestId);
}