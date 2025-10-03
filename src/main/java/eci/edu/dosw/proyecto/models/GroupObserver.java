package eci.edu.dosw.proyecto.models;

/**
 * Clase observador que "escucha" cuando un grupo es actualizado.
 */
public interface GroupObserver {
    void update(Group group);
}
