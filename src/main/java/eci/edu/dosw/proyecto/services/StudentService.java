package eci.edu.dosw.proyecto.services;


import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.ScheduleEntryDTO;
import eci.edu.dosw.proyecto.dtos.StudentDTO;
import eci.edu.dosw.proyecto.enums.AcademicTrafficLight;
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
    StudentDTO getStudentSchedule(int studentId, int semester);
    List<ChangeRequestDTO> getStudentRequests(int studentId);
    List<ScheduleEntryDTO> getStudentScheduleByTrafficLight(int studentId, int semester, AcademicTrafficLight light);
    List<ChangeRequestDTO> getStudentRequestsByStatus(int studentId, RequestStatus status);
}