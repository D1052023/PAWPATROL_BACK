package eci.edu.dosw.proyecto.models;


import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import eci.edu.dosw.proyecto.enums.Curriculum;
import eci.edu.dosw.proyecto.enums.GroupStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que maneja la información de los grupos.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "groups")
public class Group {
    private String groupId;         
    private String name;            
    private Subject subject;        
    private String teacher;        
    private int maximumCapacity;         
    private int currentCapacity;
    private Curriculum curriculum;           
    private List<ScheduleEntry> schedule;
    private GroupStatus groupStatus;
    private List<Integer> waitlist = new ArrayList<>();

    
    @Transient
    private List<GroupObserver> observers = new ArrayList<>();

    public void attach(GroupObserver observer) {
        observers.add(observer);
    }

    public void detach(GroupObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (GroupObserver observer : observers) {
            observer.update(this);
        }
    }

    public void enrollStudent() {
        if (currentCapacity < maximumCapacity) {
            currentCapacity++;
            if (getLoadPercentage() >= 90) {
                notifyObservers();
            }
        } else {
            throw new IllegalStateException("Grupo lleno");
        }
    }

    public double getLoadPercentage() {
        return (currentCapacity * 100.0) / maximumCapacity;
    }
}
