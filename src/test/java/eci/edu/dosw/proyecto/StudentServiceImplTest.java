package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.ScheduleEntryDTO;
import eci.edu.dosw.proyecto.dtos.StudentDTO;
import eci.edu.dosw.proyecto.enums.AcademicTrafficLight;
import eci.edu.dosw.proyecto.enums.Curriculum;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.mappers.ChangeRequestMapper;
import eci.edu.dosw.proyecto.mappers.ScheduleEntryMapper;
import eci.edu.dosw.proyecto.mappers.StudentMapper;
import eci.edu.dosw.proyecto.models.ChangeRequest;
import eci.edu.dosw.proyecto.models.ScheduleEntry;
import eci.edu.dosw.proyecto.models.Student;
import eci.edu.dosw.proyecto.repositories.ChangeRequestRepository;
import eci.edu.dosw.proyecto.repositories.StudentRepository;
import eci.edu.dosw.proyecto.services.impl.StudentServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    StudentRepository studentRepository;

    @Mock
    StudentMapper studentMapper;

    @Mock
    ScheduleEntryMapper scheduleEntryMapper;

    @Mock
    ChangeRequestRepository changeRequestRepository;

    @Mock
    ChangeRequestMapper changeRequestMapper;

    @InjectMocks
    StudentServiceImpl studentService;

    /**
    @Test
    void ShouldReturnAllStudents() {
        Student s = new Student();
        s.setId(1000100516);
        s.setName("Juan Pablo Caballero");

        StudentDTO dto = new StudentDTO();
        dto.setId(1000100516);
        dto.setName("Juan Pablo Caballero");

        when(studentRepository.findAll()).thenReturn(List.of(s));
        when(studentMapper.toDTOList(List.of(s))).thenReturn(List.of(dto));

        List<StudentDTO> out = studentService.getAllStudents();
        assertEquals(1, out.size());
        assertEquals("Juan Pablo Caballero", out.get(0).getName());
    }

    @Test
    void ShouldReturnStudentById() {
        Student s = new Student();
        s.setId(1000100575);
        s.setName("Robinson Steven Nuñez");

        StudentDTO dto = new StudentDTO();
        dto.setId(1000100575);
        dto.setName("Robinson Steven Nuñez");

        when(studentRepository.findById(1000100575)).thenReturn(Optional.of(s));
        when(studentMapper.toDTO(s)).thenReturn(dto);

        StudentDTO out = studentService.getStudentById(1000100575);
        assertEquals(1000100575, out.getId());
        assertEquals("Robinson Steven Nuñez", out.getName());
    }

    @Test
    void shouldThrowWhenStudentByIdNotFound() {
        when(studentRepository.findById(999)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> studentService.getStudentById(999));
        
        assertTrue(ex.getMessage().contains("Estudiante no encontrado"));
    }

    @Test
    void ShouldCreateStudent() {
        StudentDTO inDto = new StudentDTO();
        inDto.setName("Diego Fernando Chavarro");
        inDto.setEmail("diego.chavarro-c@mail.escuelaing.edu.co");
        inDto.setCareer("Ingenieria Civil");
        inDto.setSemester(6);

        Student entity = new Student();
        entity.setName("Diego Fernando Chavarro");
        entity.setEmail("diego.chavarro-c@mail.escuelaing.edu.co");

        Student saved = new Student();
        saved.setId(1000100667);
        saved.setName("Diego Fernando Chavarro");
        saved.setEmail("diego.chavarro-c@mail.escuelaing.edu.co");

        StudentDTO outDto = new StudentDTO();
        outDto.setId(1000100667);
        outDto.setName("Diego Fernando Chavarro");

        when(studentMapper.toEntity(inDto)).thenReturn(entity);
        when(studentRepository.save(entity)).thenReturn(saved);
        when(studentMapper.toDTO(saved)).thenReturn(outDto);

        StudentDTO res = studentService.createStudent(inDto);
        assertEquals(1000100667, res.getId());
        assertEquals("Diego Fernando Chavarro", res.getName());
    }

    @Test
    void ShouldUpdateStudent() {
        Student existing = new Student();
        existing.setId(1000100282);
        existing.setName("David Santiago Palcios");
        existing.setEmail("david.palacios-p@mail.escuelaing.edu.co");
        existing.setCareer("Enonomia");
        existing.setSemester(7);
        existing.setCurriculum(Curriculum.ECON_15);
        existing.setAcademicTrafficLight(AcademicTrafficLight.GREEN);

        Student saved = new Student();
        saved.setId(1000100282);
        saved.setName("David Santiago Palcios");
        saved.setEmail("david.palacios-p@mail.escuelaing.edu.co");
        saved.setCareer("Administracion de Empresas");
        saved.setSemester(2024);
        saved.setCurriculum(Curriculum.ADMIN_15);
        saved.setAcademicTrafficLight(AcademicTrafficLight.BLUE);

        StudentDTO inDto = new StudentDTO();
        inDto.setName("David Santiago Palcios");
        inDto.setEmail("david.palacios-p@mail.escuelaing.edu.co");
        inDto.setCareer("Administracion de Empresas");
        inDto.setSemester(2024);
        inDto.setCurriculum(Curriculum.ADMIN_15);
        inDto.setAcademicTrafficLight(AcademicTrafficLight.BLUE);

        StudentDTO outDto = new StudentDTO();
        outDto.setId(1000100282);
        outDto.setName("David Santiago Palcios");

        when(studentRepository.findById(1000100282)).thenReturn(Optional.of(existing));
        when(studentRepository.save(existing)).thenReturn(saved);
        when(studentMapper.toDTO(saved)).thenReturn(outDto);

        StudentDTO res = studentService.updateStudent(1000100282, inDto);
        assertEquals(1000100282, res.getId());
        assertEquals("David Santiago Palcios", res.getName());
    }

    @Test
    void ShouldDeleteStudent() {
        Student s = new Student();
        s.setId(1000100279);
        s.setName("Oscar Porras Sanchez");

        when(studentRepository.findById(1000100279)).thenReturn(Optional.of(s));
        studentService.deleteStudent(1000100279);
        when(studentRepository.findById(1000100279)).thenReturn(Optional.empty());

        assertTrue(studentRepository.findById(1000100279).isEmpty());
    }

    @Test
    void shouldThrowWhenDeletingStudentNotExist() {
        when(studentRepository.findById(404)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> studentService.deleteStudent(404));
    }

    @Test
    void ShouldPartialUpdateStudent() {
        Student existing = new Student();
        existing.setId(1000100667);
        existing.setName("Diego Fernando Chavarro");
        existing.setEmail("diego.chavarro-c@mail.escuelaing.edu.co");
        existing.setSemester(6);

        when(studentRepository.findById(1000100667)).thenReturn(Optional.of(existing));

        StudentDTO partial = new StudentDTO();
        partial.setName("Diego F. Chavarro");
        partial.setSemester(3);

        Student saved = new Student();
        saved.setId(1000100667);
        saved.setName("Diego F. Chavarro");
        saved.setSemester(3);

        StudentDTO outDto = new StudentDTO();
        outDto.setId(1000100667);
        outDto.setName("Diego F. Chavarro");
        outDto.setSemester(3);

        when(studentRepository.save(existing)).thenReturn(saved);
        when(studentMapper.toDTO(saved)).thenReturn(outDto);

        StudentDTO res = studentService.partialUpdateStudent(1000100667, partial);
        assertEquals("Diego F. Chavarro", res.getName());
        assertEquals(3, res.getSemester());
    }

    @Test
    void ShouldReturnStudentByEmail() {
        Student s = new Student();
        s.setId(1000100575);
        s.setEmail("robinson.nunez-p@mail.escuelaing.edu.co");

        when(studentRepository.findByEmail("robinson.nunez-p@mail.escuelaing.edu.co")).thenReturn(s);

        Student res = studentService.getStudentByEmail("robinson.nunez-p@mail.escuelaing.edu.co");
        assertEquals(1000100575, res.getId());
    }

    @Test
    void ShouldGetStudentSchedule() {
        Student s = new Student();
        s.setId(1000100516);
        s.setName("Juan Pablo Caballero");
        
        ScheduleEntry se1 = new ScheduleEntry();
        se1.setSemester(6); 
        se1.setStatus(AcademicTrafficLight.BLUE);

        ScheduleEntry se2 = new ScheduleEntry();
        se2.setSemester(6);
        se2.setStatus(AcademicTrafficLight.GREEN);

        s.setSchedule(List.of(se1, se2));

        ScheduleEntryDTO seDto1 = new ScheduleEntryDTO();
        seDto1.setSemester(6);
        seDto1.setStatus(AcademicTrafficLight.BLUE);

        ScheduleEntryDTO seDto2 = new ScheduleEntryDTO();
        seDto2.setSemester(6);
        seDto2.setStatus(AcademicTrafficLight.GREEN);

        when(studentRepository.findById(1000100516)).thenReturn(Optional.of(s));
        when(scheduleEntryMapper.toDTO(se1)).thenReturn(seDto1);
        when(scheduleEntryMapper.toDTO(se2)).thenReturn(seDto2);

        StudentDTO res = studentService.getStudentSchedule(1000100516, 6);

        assertNotNull(res);
        assertEquals(1000100516, res.getId());
        assertEquals(AcademicTrafficLight.BLUE, res.getAcademicTrafficLight());
        assertEquals(2, res.getSchedule().size());
    }

    @Test
    void ShouldGetStudentRequests() {
        Student s = new Student();
        s.setId(1000100667);
        when(studentRepository.findById(1000100667)).thenReturn(Optional.of(s));

        ChangeRequest cr1 = new ChangeRequest();
        cr1.setId(UUID.randomUUID());
        cr1.setStudentId(1000100667);
        cr1.setStatus(RequestStatus.PENDING);

        ChangeRequest cr2 = new ChangeRequest();
        cr2.setId(UUID.randomUUID());
        cr2.setStudentId(1000100667);
        cr2.setStatus(RequestStatus.APPROVED);

        ChangeRequestDTO dto1 = new ChangeRequestDTO();
        dto1.setId(UUID.randomUUID());

        ChangeRequestDTO dto2 = new ChangeRequestDTO();
        dto2.setId(UUID.randomUUID());

        when(changeRequestRepository.findByStudentId(1000100667)).thenReturn(List.of(cr1, cr2));
        when(changeRequestMapper.toDTO(cr1)).thenReturn(dto1);
        when(changeRequestMapper.toDTO(cr2)).thenReturn(dto2);

        List<ChangeRequestDTO> res = studentService.getStudentRequests(1000100667);
        assertEquals(2, res.size());
    }

    @Test
    void ShouldGetStudentScheduleByTrafficLightr() {
        Student s = new Student();
        s.setId(1000100282);

        ScheduleEntry seR = new ScheduleEntry();
        seR.setSemester(6);
        seR.setStatus(AcademicTrafficLight.RED);

        ScheduleEntry seG = new ScheduleEntry();
        seG.setSemester(6);
        seG.setStatus(AcademicTrafficLight.GREEN);
        s.setSchedule(List.of(seR, seG));

        ScheduleEntryDTO dtoR = new ScheduleEntryDTO();
        dtoR.setSemester(6);
        dtoR.setStatus(AcademicTrafficLight.RED);

        ScheduleEntryDTO dtoG = new ScheduleEntryDTO();
        dtoG.setSemester(6);
        dtoG.setStatus(AcademicTrafficLight.GREEN);

        when(studentRepository.findById(1000100282)).thenReturn(Optional.of(s));
        when(scheduleEntryMapper.toDTO(seR)).thenReturn(dtoR);
        when(scheduleEntryMapper.toDTO(seG)).thenReturn(dtoG);
        
        List<ScheduleEntryDTO> reds = studentService.getStudentScheduleByTrafficLight(1000100282, 6, AcademicTrafficLight.RED);
        assertEquals(1, reds.size());
        assertEquals(AcademicTrafficLight.RED, reds.get(0).getStatus());
    }


    @Test
    void shouldThrowWhenGettingSchedule() {
        when(studentRepository.findById(9999)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> studentService.getStudentSchedule(9999, 2022));
    }
**/
}
