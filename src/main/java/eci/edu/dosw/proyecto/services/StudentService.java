package eci.edu.dosw.proyecto.services;


import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.StudentDTO;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.models.Student;

import java.util.List;

/**
 * Interfaz que maneja los métodos para implementar en StudentServiceImpl.
 */
public interface StudentService {
    List<StudentDTO> getAllStudents();
    StudentDTO getStudentById(Integer id);
    StudentDTO createStudent(StudentDTO studentDTO);
    StudentDTO updateStudent(Integer id, StudentDTO studentDTO);
    void deleteStudent(Integer id);
    StudentDTO partialUpdateStudent(Integer id, StudentDTO dto);
    Student getStudentByEmail(String email);
    List<ChangeRequestDTO> getStudentRequests(int studentId);
    List<ChangeRequestDTO> getStudentRequestsByStatus(int studentId, RequestStatus status);
}