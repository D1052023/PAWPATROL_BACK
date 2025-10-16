package eci.edu.dosw.proyecto.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import eci.edu.dosw.proyecto.models.Group;

/**
 * Interfaz para el repositorio de los grupos.
 */
public interface GroupRepository extends MongoRepository<Group, String> {
    Optional<Group> findByGroupId(String groupId);
    List<Group> findByTeacher(int teacherId);
    List<Group> findBySubjectId(String subjectId);
    boolean existsByGroupId(String groupId);
}