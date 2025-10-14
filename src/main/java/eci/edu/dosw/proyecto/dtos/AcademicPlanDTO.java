package eci.edu.dosw.proyecto.dtos;

import eci.edu.dosw.proyecto.enums.Curriculum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Clase DTO para el avance del plan academico y progreso del estudiante.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcademicPlanDTO {
    private Integer studentId;
    private String studentName;
    private String documentId;
    private String email;
    private Curriculum curriculum;
    private String planCode;
    private String programName;
    private Double creditsPlan;
    private Integer totalCoursesInPlan;
    private Double approvedCredits;
    private Integer approvedCourses;
    private Double pendingCredits;
    private Integer pendingCourses;
    private Double accumulativeAverage;
    private Double gradeAverage;
    private Integer semesterToTake;
    private Integer lastSemesterTaken;
    private String academicSituation;
    private Integer situationCycle;
    private Integer admissionCycle;
    private Integer enrolledSubjectsCount;
    private Double progressPercent;
    private List<String> missingSubjectIds;
    private List<String> enrolledSubjectIds;
}
