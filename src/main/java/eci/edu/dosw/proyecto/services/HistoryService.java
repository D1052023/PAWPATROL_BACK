package eci.edu.dosw.proyecto.services;

import eci.edu.dosw.proyecto.models.ChangeRequest;
import eci.edu.dosw.proyecto.models.ChangeRequestHistory;
import java.util.List;
import java.util.UUID;


/**
 * Interfaz que maneja el historial de una solitud desde el momento en el que se envia o modifica
 */
public interface HistoryService {
    void addHistoryEvent(UUID requestId, String actor, String action, String note, String processedBy);
    List<ChangeRequestHistory> getHistory(UUID requestId);
}