package eci.edu.dosw.proyecto.dtos;

import java.util.ArrayList;
import java.util.List;

import eci.edu.dosw.proyecto.enums.Curriculum;
import eci.edu.dosw.proyecto.enums.GroupStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase DTO que maneja la información de los grupos.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupDTO {
    private String groupId;
    private String name;
    private String subjectId;               
    private int teacher;
    private int maximumCapacity;
    private int currentCapacity;
    private Curriculum curriculum; 
    private List<ScheduleEntryDTO> schedule; 
    private GroupStatus groupStatus;  
    private List<Integer> waitlist = new ArrayList<>();
}
