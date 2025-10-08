package eci.edu.dosw.proyecto.services;

import java.util.List;
import eci.edu.dosw.proyecto.dtos.GroupDTO;

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
    List<GroupDTO> getGroupsByTeacher(int teacherId);
    List<GroupDTO> getGroupsBySubject(String subjectId);
    int getMaxCapacity(String groupId);
    int getCurrentCapacity(String groupId);
    GroupDTO assignTeacherToGroup(String groupId, int teacherId);
    GroupDTO removeTeacherFromGroup(String groupId);

}
