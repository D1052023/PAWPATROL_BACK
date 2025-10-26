package eci.edu.dosw.proyecto.configs;

/**
 * Clase para las rutas para manejar la seguridad.
 */
public final class ApiPaths {

    private ApiPaths() {}

    public static final String DEANERY = "/deaneries";
    public static final String DEANERY_BY_ID = "/deaneries/{deaneryId}";
    public static final String DEANERY_REQUEST = "/deaneries/{deaneryId}/requests/{requestId}";
    public static final String DEANERY_FACULTY = "/deaneries/faculty/{faculty}";
    public static final String DEANERY_REQUEST_HISTORY = "/deaneries/requests/{requestId}/history";
    public static final String DEANERY_REQUESTS_EXCEPTIONAL = "/deaneries/{deaneryId}/requests/exceptional";
    public static final String DEANERY_STUDENT_REQUESTS_EXCEPTIONAL = "/deaneries/{deaneryId}/students/{studentId}/requests/exceptional";
    public static final String DEANERY_REQUESTS_EXCEPTIONAL_ALL = "/deaneries/requests/exceptional";
    public static final String DEANERY_STUDENT = "/deaneries/students/{studentId}";
    public static final String DEANERY_GROUP_MAX = "/deaneries/groups/{groupId}/MaxCapacity";
    public static final String DEANERY_GROUP_CURRENT = "/deaneries/groups/{groupId}/CurrentCapacity";
    public static final String DEANERY_GROUP_WAITING = "/deaneries/groups/{groupId}/waitingList";

    public static final String GROUPS = "/groups";
    public static final String GROUP_BY_ID = "/groups/{id}";
    public static final String GROUP_ID_CAPACITY = "/groups/{id}/capacity";
    public static final String GROUP_GROUPID_MAX = "/groups/{groupId}/MaxCapacity";
    public static final String GROUP_GROUPID_CURRENT = "/groups/{groupId}/CurrentCapacity";
    public static final String GROUP_GROUPID_ENROLLED = "/groups/{groupId}/enrolled";
    public static final String GROUP_ASSIGN_TEACHER = "/groups/{groupId}/AssignTeacher/{teacherId}";
    public static final String GROUP_REMOVE_TEACHER = "/groups/{groupId}/RemoveTeacher";
    public static final String GROUP_TEACHER = "/groups/teacher/{teacherId}";
    public static final String GROUP_SUBJECT = "/groups/subject/{subjectId}";
    public static final String GROUP_ADD_STUDENT = "/groups/{groupId}/students/{studentId}";
    public static final String GROUP_WAITLIST = "/groups/{id}/waitlist";
    public static final String GROUP_WAITLIST_GROUPID = "/groups/{groupId}/waitlist";
    public static final String GROUP_SCHEDULE = "/groups/{groupId}/schedule";
    public static final String GROUP_ADD_SCHEDULE = "/groups/{groupId}/AddSchedule";
    public static final String GROUP_UPDATE_DAY = "/groups/{groupId}/schedule/day/{day}";
    public static final String GROUP_DELETE_SCHEDULE = "/groups/{groupId}/schedule";
    public static final String GROUP_DELETE_DAY = "/groups/{groupId}/schedule/day/{day}";

    public static final String SECRETARIAT = "/secretariat";
    public static final String SECRETARIAT_BY_ID = "/secretariat/{id}";
    public static final String SECRETARIAT_REQUESTS = "/secretariat/requests/{requestId}";
    public static final String SECRETARIAT_REQUESTS_FACULTY_STATUS = "/secretariat/requests/faculty/{faculty}/status/{status}";
    public static final String SECRETARIAT_REQUESTS_FACULTY_PRIORITY = "/secretariat/requests/faculty/{faculty}/priority";
    public static final String SECRETARIAT_GROUP_MAX = "/secretariat/groups/{groupId}/MaxCapacity";
    public static final String SECRETARIAT_GROUP_CURRENT = "/secretariat/groups/{groupId}/CurrentCapacity";
    public static final String SECRETARIAT_GROUP_WAIT = "/secretariat/groups/{groupId}/waitingList";
    public static final String SECRETARIAT_STUDENT = "/secretariat/students/{studentId}";

    public static final String SUBJECTS = "/subjects";
    public static final String SUBJECT_BY_ID = "/subjects/{id}";
    public static final String SUBJECT_TEACHER = "/subjects/teacher/{teacherId}";
    public static final String SUBJECT_CAPACITY = "/subjects/{subjectId}/capacity";
    public static final String SUBJECT_ASSIGN = "/subjects/{subjectId}/assign/{studentId}";
    public static final String SUBJECT_REMOVE = "/subjects/{subjectId}/remove/{studentId}";

    public static final String STUDENTS = "/students";
    public static final String STUDENT_BY_ID = "/students/{id}";
    public static final String STUDENT_REQUESTS = "/students/{id}/requests";
    public static final String STUDENT_REQUESTS_STATUS = "/students/{id}/requestsStatus";
    public static final String STUDENT_ACADEMIC_PLAN = "/students/{studentId}/academic-plan";

    public static final String TEACHERS = "/teachers";
    public static final String TEACHER_BY_ID = "/teachers/{id}";
    public static final String TEACHER_EMAIL = "/teachers/email/{email}";

    public static final String REPORT_SUBJECTS = "/reports/subjects";
    public static final String REPORT_GROUPS = "/reports/groups";
    public static final String REPORT_DEANERIES = "/reports/deaneries";
    public static final String REPORT_GLOBAL = "/reports/global";

    public static final String CHANGEREQUEST = "/requests/students/{studentId}";
    public static final String EXCEPTIONALREQUEST_BY_STUDENT = "/requests/students/{studentId}/exceptionalRequest";
    public static final String CHANGEREQUEST_STATUS = "/requests/students/{studentId}/status";
    public static final String CHANGEREQUEST_SCHEDULE = "/requests/students/{studentId}/schedule/current";
    public static final String CHANGEREQUEST_PREVIOUSSCHEDULE = "/requests/students/{studentId}/schedule/previous";
    public static final String CHANGERQUEST_EXCEPTIONAL = "/request/{requestId}/students/{studentId}/requestExceptional";
    public static final String CHANGEREQUEST_HISTORY = "/requests/{requestId}/students/{studentId}/history";
    public static final String CHANGEREQUEST_STUDENT_ID = "/requests/{requestId}/students/{studentId}";

}
