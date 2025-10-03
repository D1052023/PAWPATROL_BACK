package eci.edu.dosw.proyecto.services;

import org.springframework.stereotype.Service;

import eci.edu.dosw.proyecto.models.Group;
import eci.edu.dosw.proyecto.models.GroupObserver;

@Service
public class AlertService implements GroupObserver {
    @Override
    public void update(Group group) {
        System.out.println("ALERTA: Grupo " + group.getName() + " al " + group.getLoadPercentage() + "% de su capacidad.");
    }
}
