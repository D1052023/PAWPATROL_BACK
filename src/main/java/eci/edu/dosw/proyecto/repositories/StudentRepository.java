package eci.edu.dosw.proyecto.repositories;

import eci.edu.dosw.proyecto.models.ChangeRequest;
import eci.edu.dosw.proyecto.models.Student;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Interfaz para el repositorio de los estudiantes.
 */
public interface StudentRepository extends MongoRepository<Student, Integer> {
    Student findByEmail(String email);
    List<Student> findByRequestsContaining(ChangeRequest request);

}
