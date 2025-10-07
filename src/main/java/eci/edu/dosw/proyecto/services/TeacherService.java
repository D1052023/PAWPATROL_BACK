package eci.edu.dosw.proyecto.services;

import java.util.List;
import java.util.Optional;

import eci.edu.dosw.proyecto.dtos.TeacherDTO;

public interface TeacherService {

    TeacherDTO getTeacherById(int id);
    List<TeacherDTO> getAllTeachers();
    TeacherDTO createTeacher(TeacherDTO teacherDTO);
    void deleteTeacher(int id);
    TeacherDTO updateTeacher(int id, TeacherDTO teacherDTO);
    Optional<TeacherDTO> getTeacherByEmail(String email);
    TeacherDTO partialUpdateTeacher(Integer id, TeacherDTO teacherDTO);
}