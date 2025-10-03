package eci.edu.dosw.proyecto.repositories;

import eci.edu.dosw.proyecto.models.ChangeRequest;
import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.enums.RequestStatus;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Interfaz para el repositorio de solicitudes de cambio.
 */
@Repository
public interface ChangeRequestRepository extends MongoRepository<ChangeRequest, UUID> {
    List<ChangeRequest> findByStudentId(Integer studentId);
    List<ChangeRequest> findByFacultyAndStatus(Faculty faculty, RequestStatus status);
}
