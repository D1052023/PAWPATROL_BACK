package eci.edu.dosw.proyecto.services;

import java.util.List;
import eci.edu.dosw.proyecto.dtos.GroupDTO;
import eci.edu.dosw.proyecto.dtos.ScheduleEntryDTO;
import eci.edu.dosw.proyecto.dtos.StudentDTO;

/**
 * Interfaz que maneja los métodos para implementar en GroupServiceImpl.
 */
public interface GroupService {

    GroupDTO createGroup(GroupDTO dto);
    List<GroupDTO> getAllGroups();
    GroupDTO getGroupById(String groupId);
    GroupDTO updateGroup(String groupId, GroupDTO dto);
    void deleteGroup(String groupId);
    GroupDTO updateCapacity(String groupId, int newCurrentCapacity);
    GroupDTO partialUpdateGroup(String groupId, GroupDTO dto);
    List<Integer> getWaitlist(String groupId);
    List<StudentDTO> getWaitlistDetails(String groupId);
    List<GroupDTO> getGroupsByTeacher(int teacherId);
    List<GroupDTO> getGroupsBySubject(String subjectId);
    int getMaxCapacity(String groupId);
    int getCurrentCapacity(String groupId);
    GroupDTO assignTeacherToGroup(String groupId, int teacherId);
    GroupDTO removeTeacherFromGroup(String groupId);
    int getEnrolledCount(String groupId);
    ScheduleEntryDTO addScheduleEntry(String groupId, ScheduleEntryDTO entry);
    List<ScheduleEntryDTO> getSchedule(String groupId);
    List<ScheduleEntryDTO> updateScheduleGlobal(String groupId, List<ScheduleEntryDTO> entries);
    List<ScheduleEntryDTO> updateScheduleForDay(String groupId, String day, List<ScheduleEntryDTO> entries);
    void deleteScheduleGlobal(String groupId);
    void deleteScheduleForDay(String groupId, String day);
    GroupDTO assignStudentToGroup(String groupId, int studentId);
    GroupDTO removeStudentFromGroup(String groupId, int studentId);


}
