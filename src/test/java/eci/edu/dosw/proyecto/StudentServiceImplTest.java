package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.dtos.AcademicPlanDTO;
import eci.edu.dosw.proyecto.dtos.ChangeRequestDTO;
import eci.edu.dosw.proyecto.dtos.ScheduleEntryDTO;
import eci.edu.dosw.proyecto.dtos.StudentDTO;
import eci.edu.dosw.proyecto.enums.Career;
import eci.edu.dosw.proyecto.enums.Curriculum;
import eci.edu.dosw.proyecto.enums.RequestStatus;
import eci.edu.dosw.proyecto.mappers.AcademicPlanMapper;
import eci.edu.dosw.proyecto.mappers.ChangeRequestMapper;
import eci.edu.dosw.proyecto.mappers.ScheduleEntryMapper;
import eci.edu.dosw.proyecto.mappers.StudentMapper;
import eci.edu.dosw.proyecto.models.ChangeRequest;
import eci.edu.dosw.proyecto.models.ScheduleEntry;
import eci.edu.dosw.proyecto.models.Student;
import eci.edu.dosw.proyecto.models.Subject;
import eci.edu.dosw.proyecto.repositories.ChangeRequestRepository;
import eci.edu.dosw.proyecto.repositories.StudentRepository;
import eci.edu.dosw.proyecto.repositories.SubjectRepository;
import eci.edu.dosw.proyecto.services.impl.StudentServiceImpl;
import eci.edu.dosw.proyecto.util.MessageExceptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    StudentRepository studentRepository;

    @Mock
    StudentMapper studentMapper;

    @Mock
    ChangeRequestRepository changeRequestRepository;

    @Mock
    AcademicPlanMapper academicPlanMapper;

    @Mock
    SubjectRepository subjectRepository;

    @Mock
    ChangeRequestMapper changeRequestMapper;

    @Mock
    ScheduleEntryMapper scheduleEntryMapper;

    @Mock
    MessageExceptions message;

    @InjectMocks
    StudentServiceImpl studentService;

    @Test
    void shouldReturnAllStudents() {
        Student s1 = new Student(); s1.setId(1); s1.setName("Diego Fernando Chavarro");
        Student s2 = new Student(); s2.setId(2); s2.setName("David Santiago Palacios");
        when(studentRepository.findAll()).thenReturn(List.of(s1, s2));
        when(studentMapper.toDTOList(anyList())).thenAnswer(i -> {
            List<Student> in = i.getArgument(0);
            List<StudentDTO> out = new ArrayList<>();
            for (Student s: in) {
                StudentDTO dto = new StudentDTO();
                dto.setId(s.getId());
                dto.setName(s.getName());
                out.add(dto);
            }
            return out;
        });

        List<StudentDTO> res = studentService.getAllStudents();
        assertEquals(2, res.size());
        assertEquals("Diego Fernando Chavarro", res.get(0).getName());
    }

    @Test
    void shouldGetStudentByIdFound() {
        Student s = new Student(); s.setId(1000100575); s.setName("Robinson Steven Nuñez");
        when(message.findStudentOrThrow(1000100575)).thenReturn(s);
        when(studentMapper.toDTO(s)).thenReturn(new StudentDTO(){ { setId(1000100575); setName("Robinson Steven Nuñez"); }});
        StudentDTO res = studentService.getStudentById(1000100575);

        assertEquals(1000100575, res.getId());
        assertEquals("Robinson Steven Nuñez", res.getName());
    }

    @Test
    void shouldThrowWhenGetStudentByIdMissing() {
        when(message.findStudentOrThrow(1000100282)).thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Estudiante no encontrado"));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> studentService.getStudentById(1000100282));
        assertTrue(ex.getMessage().contains("Estudiante no encontrado"));
    }

    @Test
    void shouldCreateAndMapStudent() {
        StudentDTO in = new StudentDTO(); in.setName("Nuevo"); in.setEmail("n@e");
        Student entity = new Student();
        when(studentMapper.toEntity(in)).thenReturn(entity);
        when(studentRepository.save(entity)).thenReturn(entity);
        when(studentMapper.toDTO(entity)).thenReturn(new StudentDTO(){ { setName("Nuevo"); setEmail("n@e"); } });

        StudentDTO out = studentService.createStudent(in);
        assertNotNull(out);
        assertEquals("Nuevo", out.getName());
    }

    @Test
    void shouldUpdateStudentExisting() {
        Student existing = new Student(); existing.setId(1000100516); existing.setName("pakas"); existing.setEmail("pakas@mail.escuelaing.edu.co");
        when(message.findStudentOrThrow(1000100516)).thenReturn(existing);
        Student saved = new Student(); saved.setId(1000100516); saved.setName("Juan Pablo Caballero"); saved.setEmail("juan.ccastellanos@mail.escuelaing.edu.co");
        when(studentRepository.save(existing)).thenReturn(saved);
        when(studentMapper.toDTO(saved)).thenReturn(new StudentDTO(){ { setId(1000100516); setName("Juan Pablo Caballero"); setEmail("juan.ccastellanos@mail.escuelaing.edu.co"); }});
        StudentDTO update = new StudentDTO();
        update.setName("Juan Pablo Caballero");
        update.setEmail("juan.ccastellanos@mail.escuelaing.edu.co");
        update.setCareer(Career.SISTEMAS);
        update.setSemester(3);
        update.setCurriculum(null);
        update.setAcademicTrafficLight(null);

        StudentDTO res = studentService.updateStudent(1000100516, update);
        assertEquals("Juan Pablo Caballero", res.getName());
    }

    @Test
    void shouldDeleteStudent() {
        Student s = new Student(); s.setId(1000100575);
        when(message.findStudentOrThrow(1000100575)).thenReturn(s);
        // delete does not return - just ensure no exception
        studentService.deleteStudent(1000100575);
        verify(studentRepository).delete(s);
    }

    @Test
    void shouldPartialUpdateStudentOnlyName() {
        Student s = new Student(); s.setId(1000100516); s.setName("Juan");
        when(message.findStudentOrThrow(1000100516)).thenReturn(s);
        when(studentRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(studentMapper.toDTO(any())).thenAnswer(i -> {
            Student st = i.getArgument(0);
            StudentDTO dto = new StudentDTO();
            dto.setId(st.getId());
            dto.setName(st.getName());
            return dto;
        });
        StudentDTO patch = new StudentDTO();
        patch.setName("Juan Pablo Caballero");
        StudentDTO result = studentService.partialUpdateStudent(1000100516, patch);

        assertEquals("Juan Pablo Caballero", result.getName());
    }

    @Test
    void shouldGetStudentByEmailFoundAndNotFound() {
        Student s = new Student(); s.setId(1000100575); s.setEmail("robinson.nunez-p@mail.escuelaing.edu.co");
        when(message.findStudentByEmailOrThrow("robinson.nunez-p@mail.escuelaing.edu.co")).thenReturn(s);
        Student out = studentService.getStudentByEmail("robinson.nunez-p@mail.escuelaing.edu.co");
        assertEquals(1000100575, out.getId());

        when(message.findStudentByEmailOrThrow("missing@e")).thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Estudiante no encontrado"));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> studentService.getStudentByEmail("missing@e"));
        assertTrue(ex.getMessage().contains("Estudiante no encontrado"));
    }

    @Test
    void shouldReturnStudentRequestsAndByStatus() {
        ChangeRequest r1 = new ChangeRequest(); r1.setId(UUID.randomUUID()); r1.setStudentId(1000100575); r1.setStatus(RequestStatus.PENDING);
        ChangeRequest r2 = new ChangeRequest(); r2.setId(UUID.randomUUID()); r2.setStudentId(1000100575); r2.setStatus(RequestStatus.APPROVED);
        when(message.findStudentOrThrow(1000100575)).thenReturn(new Student());
        when(changeRequestRepository.findByStudentId(1000100575)).thenReturn(List.of(r1, r2));
        when(changeRequestMapper.toDTO(r1)).thenReturn(new ChangeRequestDTO(){{ setId(r1.getId()); }});
        when(changeRequestMapper.toDTO(r2)).thenReturn(new ChangeRequestDTO(){{ setId(r2.getId()); }});

        List<ChangeRequestDTO> all = studentService.getStudentRequests(1000100575);
        assertEquals(2, all.size());
        List<ChangeRequestDTO> pending = studentService.getStudentRequestsByStatus(1000100575, RequestStatus.PENDING);
        assertEquals(1, pending.size());
    }

    @Test
    void shouldReturnStudentScheduleFilteredBySemester() {
        Student s = new Student();
        s.setId(1000100575);
        s.setName("Juan Pablo Caballero");
        ScheduleEntry se1 = new ScheduleEntry("DOSW","DOSW-1","LUNES","08:00",1,"A","10:00", null);
        ScheduleEntry se2 = new ScheduleEntry("TPYC","TPYC-1","MARTES","10:00",2,"B","12:00", null);
        s.setSchedule(new ArrayList<>(List.of(se1, se2)));
        when(message.findStudentOrThrow(1000100575)).thenReturn(s);
        when(scheduleEntryMapper.toDTO(se1)).thenReturn(new ScheduleEntryDTO(){ { setSubject("DOSW"); setGroup("DOSW-1"); setSemester(1); }});
        StudentDTO dto = studentService.getStudentSchedule(1000100575, 1);

        assertEquals(1, dto.getSchedule().size());
        assertEquals("DOSW", dto.getSchedule().get(0).getSubject());
    }

    @Test
    void shouldSaveStudentEntity() {
        Student s = new Student(); s.setId(1000100575);
        when(studentRepository.save(s)).thenReturn(s);
        Student out = studentService.saveStudent(s);

        assertEquals(s, out);
    }

    @Test
    void shouldComputeAcademicPlanNoCurriculum() {
        int id = 1000100279;
        Student student = new Student();
        student.setId(id);
        student.setName("Oscar Porras Sanchez");
        student.setApprovedSubjects(List.of());
        student.setEnrolledSubjects(List.of());
        when(message.findStudentOrThrow(id)).thenReturn(student);
        when(academicPlanMapper.toDto(student)).thenReturn(new AcademicPlanDTO(){ { setStudentId(id); setStudentName("Oscar Porras Sanchez"); }});
        AcademicPlanDTO dto = studentService.getAcademicPlan(id);

        assertEquals(id, dto.getStudentId());
        assertEquals(0, dto.getTotalCoursesInPlan());
        assertEquals(0, dto.getApprovedCourses());
        assertEquals(0, dto.getEnrolledSubjectsCount());
        assertEquals(0.0, dto.getProgressPercent());
    }

    @Test
    void shouldComputeAcademicPlan_withCurriculum_andComputeApprovedCredits() {
        int id = 1000100575;
        Student student = new Student();
        student.setId(id);
        student.setCurriculum(Curriculum.ISIS_14);
        student.setApprovedSubjects(new ArrayList<>(List.of("DOSW","TPYC")));
        student.setEnrolledSubjects(new ArrayList<>(List.of("DOSW")));
        student.setApprovedCredits(null);
        when(message.findStudentOrThrow(id)).thenReturn(student);
        when(academicPlanMapper.toDto(student)).thenReturn(new AcademicPlanDTO(){ { setStudentId(id); }});
        Subject s1 = new Subject(); s1.setSubjectId("DOSW"); s1.setCredits(3);
        Subject s2 = new Subject(); s2.setSubjectId("TPYC"); s2.setCredits(4);
        Subject plan1 = new Subject(); plan1.setSubjectId("DOSW"); plan1.setCredits(3);
        Subject plan2 = new Subject(); plan2.setSubjectId("TPYC"); plan2.setCredits(4);
        Subject plan3 = new Subject(); plan3.setSubjectId("ODSC"); plan3.setCredits(2);
        when(subjectRepository.findByCurriculum(Curriculum.ISIS_14)).thenReturn(List.of(plan1, plan2, plan3));
        when(subjectRepository.findBySubjectId("DOSW")).thenReturn(Optional.of(s1));
        when(subjectRepository.findBySubjectId("TPYC")).thenReturn(Optional.of(s2));
        AcademicPlanDTO dto = studentService.getAcademicPlan(id);
        assertEquals(3, dto.getTotalCoursesInPlan());
        assertEquals(9.0, dto.getCreditsPlan());
        assertEquals(7.0, dto.getApprovedCredits());
        assertEquals(1, dto.getPendingCourses());
        assertEquals(2.0, dto.getPendingCredits());
        assertEquals(1, dto.getEnrolledSubjectsCount());
        assertEquals(Math.round(((double)1*100.0/3.0)*100.0)/100.0, dto.getProgressPercent());
        assertTrue(dto.getMissingSubjectIds().contains("ODSC"));
    }

    @Test
    void shouldHandleApprovedCreditsZeroAsNull() {
        int id = 1000100667;
        Student student = new Student();
        student.setId(id);
        student.setApprovedSubjects(new ArrayList<>());
        student.setEnrolledSubjects(new ArrayList<>());
        student.setApprovedCredits(0.0);
        when(message.findStudentOrThrow(id)).thenReturn(student);
        when(academicPlanMapper.toDto(student)).thenReturn(new AcademicPlanDTO(){ { setStudentId(id); }});
        AcademicPlanDTO dto = studentService.getAcademicPlan(id);

        assertEquals(0.0, dto.getApprovedCredits());
    }

    @Test
    void shouldPartialUpdateAllFields() {
        Student existing = new Student();
        existing.setId(1000100516);
        existing.setName("Old Name");
        existing.setEmail("old@mail");
        existing.setCareer(null);
        existing.setSemester(1);
        when(message.findStudentOrThrow(1000100516)).thenReturn(existing);
        when(studentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(studentMapper.toDTO(any())).thenAnswer(inv -> {
            Student s = inv.getArgument(0);
            StudentDTO dto = new StudentDTO();
            dto.setId(s.getId());
            dto.setName(s.getName());
            dto.setEmail(s.getEmail());
            dto.setCareer(s.getCareer());
            dto.setSemester(s.getSemester());
            return dto;
        });

        StudentDTO patch = new StudentDTO();
        patch.setName("New Name");
        patch.setEmail("new@mail");
        patch.setCareer(Career.SISTEMAS);
        patch.setSemester(4);
        patch.setCurriculum(null);
        patch.setAcademicTrafficLight(null);
        StudentDTO out = studentService.partialUpdateStudent(1000100516, patch);

        assertNotNull(out);
        assertEquals(1000100516, out.getId());
        assertEquals("New Name", out.getName());
        assertEquals("new@mail", out.getEmail());
        assertEquals(Career.SISTEMAS, out.getCareer());
        assertEquals(4, out.getSemester());
    }

    @Test
    void shouldPartialUpdateIgnoreSemesterWhenZero() {
        Student existing = new Student();
        existing.setId(1000100516);
        existing.setName("Before");
        existing.setSemester(2);
        when(message.findStudentOrThrow(1000100516)).thenReturn(existing);
        when(studentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(studentMapper.toDTO(any())).thenAnswer(inv -> {
            Student s = inv.getArgument(0);
            StudentDTO dto = new StudentDTO();
            dto.setId(s.getId());
            dto.setName(s.getName());
            dto.setSemester(s.getSemester());
            return dto;
        });

        StudentDTO patch = new StudentDTO();
        patch.setSemester(0); // should be ignored
        patch.setName("After");
        StudentDTO out = studentService.partialUpdateStudent(1000100516, patch);

        assertEquals("After", out.getName());
        assertEquals(2, out.getSemester());
    }

    @Test
    void shouldGetStudentScheduleWhenNoSchedule() {
        Student s = new Student();
        s.setId(1000100575);
        s.setName("No Schedule Student");
        s.setSchedule(null);
        when(message.findStudentOrThrow(1000100575)).thenReturn(s);
        StudentDTO dto = studentService.getStudentSchedule(1000100575, 1);

        assertNotNull(dto);
        assertNotNull(dto.getSchedule());
        assertTrue(dto.getSchedule().isEmpty());
        assertEquals(1, dto.getSemester()); // requested semester returned in DTO
        assertEquals("No Schedule Student", dto.getName());
    }

    @Test
    void shouldDeleteStudentNotFoundThrows() {
        when(message.findStudentOrThrow(1000100575)).thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Estudiante no encontrado"));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> studentService.deleteStudent(1000100575));

        assertTrue(ex.getMessage().contains("Estudiante no encontrado"));
    }

    @Test
    void shouldGetStudentRequestsWhenStudentMissingThrows() {
        when(message.findStudentOrThrow(1000100575)).thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Estudiante no encontrado"));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> studentService.getStudentRequests(1000100575));

        assertTrue(ex.getMessage().contains("Estudiante no encontrado"));
    }

    @Test
    void shouldUpdateStudentNotFoundThrows() {
        int id = 1000100575;
        when(message.findStudentOrThrow(id)).thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Estudiante no encontrado"));
        StudentDTO dto = new StudentDTO();
        dto.setName("David Santiago Palcios");
        dto.setEmail("david.palacios-p@mail.escuelaing.edu.co");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> studentService.updateStudent(id, dto));

        assertTrue(ex.getMessage().contains("Estudiante no encontrado"));
    }

    @Test
    void shouldPartialUpdateStudentNotFoundThrows() {
        int id = 1000100516;
        when(message.findStudentOrThrow(id)).thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Estudiante no encontrado"));
        StudentDTO patch = new StudentDTO();
        patch.setName("No importa");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> studentService.partialUpdateStudent(id, patch));

        assertTrue(ex.getMessage().contains("Estudiante no encontrado"));
    }

    @Test
    void shouldGetStudentRequestsByStatusStudentMissingThrows() {
        int id = 1000100575;
        when(message.findStudentOrThrow(id)).thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Estudiante no encontrado"));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> studentService.getStudentRequestsByStatus(id, RequestStatus.PENDING));

        assertTrue(ex.getMessage().contains("Estudiante no encontrado"));
    }


    @Test
    void shouldPartialUpdateSetAcademicTrafficLight() {
        Student existing = new Student();
        existing.setId(1000100516);
        existing.setName("Before");
        existing.setEmail("before@mail");
        existing.setSemester(2);
        when(message.findStudentOrThrow(1000100516)).thenReturn(existing);
        when(studentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(studentMapper.toDTO(any())).thenAnswer(inv -> {
            Student s = inv.getArgument(0);
            StudentDTO dto = new StudentDTO();
            dto.setId(s.getId());
            dto.setName(s.getName());
            dto.setEmail(s.getEmail());
            dto.setAcademicTrafficLight(s.getAcademicTrafficLight());
            dto.setSemester(s.getSemester());
            return dto;
        });
        StudentDTO patch = new StudentDTO();
        patch.setAcademicTrafficLight(eci.edu.dosw.proyecto.enums.AcademicTrafficLight.GREEN);
        StudentDTO out = studentService.partialUpdateStudent(1000100516, patch);

        assertNotNull(out);
        assertEquals(1000100516, out.getId());
        assertEquals("Before", out.getName());
        assertEquals("before@mail", out.getEmail());
        assertEquals(eci.edu.dosw.proyecto.enums.AcademicTrafficLight.GREEN, out.getAcademicTrafficLight());
        assertEquals(2, out.getSemester());
    }

    @Test
    void shouldPartialUpdateSetCurriculumAndSemesterPositive() {
        Student existing = new Student();
        existing.setId(1000100516);
        existing.setName("Old");
        existing.setSemester(1);
        when(message.findStudentOrThrow(1000100516)).thenReturn(existing);
        when(studentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(studentMapper.toDTO(any())).thenAnswer(inv -> {
            Student s = inv.getArgument(0);
            StudentDTO dto = new StudentDTO();
            dto.setId(s.getId());
            dto.setName(s.getName());
            dto.setSemester(s.getSemester());
            dto.setCurriculum(s.getCurriculum());
            return dto;
        });
        StudentDTO patch = new StudentDTO();
        patch.setCurriculum(eci.edu.dosw.proyecto.enums.Curriculum.ISIS_14);
        patch.setSemester(5);
        StudentDTO out = studentService.partialUpdateStudent(1000100516, patch);

        assertNotNull(out);
        assertEquals(1000100516, out.getId());
        assertEquals(5, out.getSemester());
        assertEquals(eci.edu.dosw.proyecto.enums.Curriculum.ISIS_14, out.getCurriculum());
    }

    @Test
    void shouldPartialUpdateIgnoreAllNullsAndReturnSame() {
        Student existing = new Student();
        existing.setId(1000100516);
        existing.setName("Same");
        existing.setEmail("same@mail");
        existing.setCareer(eci.edu.dosw.proyecto.enums.Career.SISTEMAS);
        existing.setSemester(3);
        when(message.findStudentOrThrow(1000100516)).thenReturn(existing);
        when(studentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(studentMapper.toDTO(any())).thenAnswer(inv -> {
            Student s = inv.getArgument(0);
            StudentDTO dto = new StudentDTO();
            dto.setId(s.getId());
            dto.setName(s.getName());
            dto.setEmail(s.getEmail());
            dto.setCareer(s.getCareer());
            dto.setSemester(s.getSemester());
            return dto;
        });
        StudentDTO patch = new StudentDTO();
        StudentDTO out = studentService.partialUpdateStudent(1000100516, patch);

        assertNotNull(out);
        assertEquals(1000100516, out.getId());
        assertEquals("Same", out.getName());
        assertEquals("same@mail", out.getEmail());
        assertEquals(eci.edu.dosw.proyecto.enums.Career.SISTEMAS, out.getCareer());
        assertEquals(3, out.getSemester());
    }

}