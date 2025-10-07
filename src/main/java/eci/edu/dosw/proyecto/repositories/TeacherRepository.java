package eci.edu.dosw.proyecto.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import eci.edu.dosw.proyecto.models.Teacher;

@Repository
public interface TeacherRepository extends MongoRepository<Teacher, Integer> {
    Optional<Teacher> findByEmail(String email);
    boolean existsByEmail(String email);
}
