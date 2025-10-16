package eci.edu.dosw.proyecto.repositories;

import java.util.List;
import java.util.Optional;

import eci.edu.dosw.proyecto.enums.Curriculum;
import org.springframework.data.mongodb.repository.MongoRepository;

import eci.edu.dosw.proyecto.models.Subject;

/**
 * Interfaz para el repositorio de las materias.
 */
public interface SubjectRepository extends MongoRepository<Subject, String> {
    Optional<Subject> findBySubjectId(String subjectId);
    boolean existsBySubjectId(String subjectId);
    List<Subject> findByTeacherId(int teacherId);
    List<Subject> findByCurriculum(Curriculum curriculum);
}