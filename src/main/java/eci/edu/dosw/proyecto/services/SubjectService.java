package eci.edu.dosw.proyecto.services;

import java.util.List;
import eci.edu.dosw.proyecto.dtos.SubjectDTO;


/**
 * Interfaz que maneja los métodos para implementar en StudentServiceImpl.
 */
public interface SubjectService {
    SubjectDTO createSubject(SubjectDTO dto);
    List<SubjectDTO> getAllSubjects();
    SubjectDTO getSubjectById(String subjectId);
    SubjectDTO updateSubject(String subjectId, SubjectDTO dto);
    void deleteSubject(String subjectId);
    SubjectDTO partialUpdateSubject(String subjectId, SubjectDTO dto);
}
