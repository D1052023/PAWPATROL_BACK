package eci.edu.dosw.proyecto.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import eci.edu.dosw.proyecto.dtos.TeacherDTO;
import eci.edu.dosw.proyecto.models.Teacher;
import eci.edu.dosw.proyecto.repositories.TeacherRepository;
import eci.edu.dosw.proyecto.services.TeacherService;
import eci.edu.dosw.proyecto.mappers.TeacherMapper;
import eci.edu.dosw.proyecto.util.MessageExceptions;


/**
 * Clase servicio que implementa la interfaz y maneja la lógica de los profesores.
 */
@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;
    private final MessageExceptions message;

    @Override
    public TeacherDTO getTeacherById(int id) {
        Teacher teacher = message.findTeacherOrThrow(id);
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
        message.ensureTeacherEmailNotRegisteredForCreate(teacherDTO.getEmail());
        Teacher teacher = teacherMapper.toEntity(teacherDTO);
        Teacher savedTeacher = teacherRepository.save(teacher);
        return teacherMapper.toDTO(savedTeacher);
    }

    @Override
    public void deleteTeacher(int id) {
        message.findTeacherOrThrow(id);
        teacherRepository.deleteById(id);
    }

    @Override
    public TeacherDTO updateTeacher(int id, TeacherDTO teacherDTO) {
        Teacher existingTeacher = message.findTeacherOrThrow(id);

        if (!existingTeacher.getEmail().equals(teacherDTO.getEmail())) {
            message.ensureTeacherEmailNotRegisteredForUpdate(id, teacherDTO.getEmail());
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
        Teacher teacher = message.findTeacherOrThrow(id);

        if (dto.getName() != null) {
            teacher.setName(dto.getName());
        }

        if (dto.getEmail() != null) {
            if (!teacher.getEmail().equals(dto.getEmail())) {
                message.ensureTeacherEmailNotRegisteredForUpdate(id, dto.getEmail());
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
