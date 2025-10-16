package eci.edu.dosw.proyecto.services.impl;

import eci.edu.dosw.proyecto.models.ChangeRequestHistory;
import eci.edu.dosw.proyecto.repositories.ChangeRequestHistoryRepository;
import eci.edu.dosw.proyecto.services.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


/**
 * Clase servicio que implementa la intefaz del historial para consultar a detalle el proceso de solicitud.
 */
@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {
    private final ChangeRequestHistoryRepository repo;

    @Override
    public void addHistoryEvent(UUID requestId, String actor, String action, String note, String processedBy) {
        ChangeRequestHistory h = new ChangeRequestHistory();
        h.setRequestId(requestId);
        h.setTimestamp(LocalDateTime.now());
        h.setActor(actor);
        h.setAction(action);
        h.setNote(note);
        h.setProcessedBy(processedBy);
        repo.save(h);
    }

    @Override
    public List<ChangeRequestHistory> getHistory(UUID requestId) {
        return repo.findByRequestIdOrderByTimestampAsc(requestId);
    }
}
