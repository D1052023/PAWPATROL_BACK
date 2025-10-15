package eci.edu.dosw.proyecto.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import eci.edu.dosw.proyecto.dtos.TeacherDTO;
import eci.edu.dosw.proyecto.models.Teacher;
import eci.edu.dosw.proyecto.repositories.TeacherRepository;
import eci.edu.dosw.proyecto.services.TeacherService;
import eci.edu.dosw.proyecto.mappers.TeacherMapper;

import lombok.RequiredArgsConstructor;

/**
 * Clase servicio que implementa la interfaz y maneja la lógica de los profesores.
 */
@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;

    @Override
    public TeacherDTO getTeacherById(int id) {
        Teacher teacher = teacherRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Profesor no encontrado con id: " + id));
        return teacherMapper.toDTO(teacher);
    }

    @Override
    public List<TeacherDTO> getAllTeachers() {
        return teacherRepository.findAll()
                .stream()
                .map(teacherMapper::toDTO)
                .toList();
    }

    @Override
    public TeacherDTO createTeacher(TeacherDTO teacherDTO) {
        if (teacherRepository.existsByEmail(teacherDTO.getEmail())) {
            throw new RuntimeException("Email ya existe: " + teacherDTO.getEmail());
        }

        Teacher teacher = teacherMapper.toEntity(teacherDTO);
        Teacher savedTeacher = teacherRepository.save(teacher);
        return teacherMapper.toDTO(savedTeacher);
    }

    @Override
    public void deleteTeacher(int id) {
        if (!teacherRepository.existsById(id)) {
            throw new RuntimeException("Profesor no encontrado con id: " + id);
        }
        teacherRepository.deleteById(id);
    }

    @Override
    public TeacherDTO updateTeacher(int id, TeacherDTO teacherDTO) {
        Teacher existingTeacher = teacherRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Profesor no encontrado con id: " + id));
        
        if (!existingTeacher.getEmail().equals(teacherDTO.getEmail()) && teacherRepository.existsByEmail(teacherDTO.getEmail())) {
            throw new RuntimeException("Email ya existe: " + teacherDTO.getEmail());
        }

        existingTeacher.setName(teacherDTO.getName());
        existingTeacher.setEmail(teacherDTO.getEmail());

        Teacher updatedTeacher = teacherRepository.save(existingTeacher);
        return teacherMapper.toDTO(updatedTeacher);
    }

    @Override
    public Optional<TeacherDTO> getTeacherByEmail(String email) {
        return teacherRepository.findByEmail(email).map(teacherMapper::toDTO);
    }


    @Override
    public TeacherDTO partialUpdateTeacher(Integer id, TeacherDTO dto) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Profesor no encontrado con id: " + id));

        if (dto.getName() != null) {
            teacher.setName(dto.getName());
        }

        if (dto.getEmail() != null) {
            if (!teacher.getEmail().equals(dto.getEmail()) && teacherRepository.existsByEmail(dto.getEmail())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ya registrado: " + dto.getEmail());
            }
            teacher.setEmail(dto.getEmail());
        }


        Teacher updatedTeacher = teacherRepository.save(teacher);

        TeacherDTO updatedDTO = new TeacherDTO();
        updatedDTO.setId(updatedTeacher.getId());
        updatedDTO.setName(updatedTeacher.getName());
        updatedDTO.setEmail(updatedTeacher.getEmail());
        return updatedDTO;
    }


}
