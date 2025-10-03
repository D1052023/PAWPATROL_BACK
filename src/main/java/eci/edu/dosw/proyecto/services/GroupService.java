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
    List<Integer> getWaitlist(String groupId);
}
