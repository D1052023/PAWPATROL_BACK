package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.models.ChangeRequestHistory;
import eci.edu.dosw.proyecto.repositories.ChangeRequestHistoryRepository;
import eci.edu.dosw.proyecto.services.impl.HistoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import eci.edu.dosw.proyecto.util.TimeUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryServiceImplTest {

    @Mock
    private ChangeRequestHistoryRepository repo;

    @InjectMocks
    private HistoryServiceImpl historyService;

    @Test
    void shouldAddHistoryEvent() {
        UUID reqId = UUID.randomUUID();
        String actor = "STUDENT:1000100575";
        String action = "CREATED";
        String note = "Solicitud creada";
        String processedBy = "STUDENT:1000100575";
        
        when(repo.save(any(ChangeRequestHistory.class))).thenAnswer(i -> i.getArgument(0));
        historyService.addHistoryEvent(reqId, actor, action, note, processedBy);

        ArgumentCaptor<ChangeRequestHistory> captor = ArgumentCaptor.forClass(ChangeRequestHistory.class);
        verify(repo, times(1)).save(captor.capture());
        ChangeRequestHistory saved = captor.getValue();

        assertNotNull(saved);
        assertEquals(reqId, saved.getRequestId());
        assertEquals(actor, saved.getActor());
        assertEquals(action, saved.getAction());
        assertEquals(note, saved.getNote());
        assertEquals(processedBy, saved.getProcessedBy());
        assertNotNull(saved.getTimestamp());
        assertTrue(saved.getTimestamp().isBefore(TimeUtils.nowUtc().plusSeconds(2)));
    }
    @Test
    void shouldReturnHistory() {
        UUID reqId = UUID.randomUUID();

        ChangeRequestHistory h1 = new ChangeRequestHistory();
        h1.setRequestId(reqId);
        h1.setTimestamp(LocalDateTime.now().minusMinutes(1));
        h1.setActor("SYSTEM");
        h1.setAction("SENT_TO_DEANERY");
        h1.setNote("Enviada");
        h1.setProcessedBy("SYSTEM");

        ChangeRequestHistory h2 = new ChangeRequestHistory();
        h2.setRequestId(reqId);
        h2.setTimestamp(LocalDateTime.now());
        h2.setActor("DEANERY");
        h2.setAction("APPROVED");
        h2.setNote("Aprobada");
        h2.setProcessedBy("DEANERY");

        when(repo.findByRequestIdOrderByTimestampAsc(reqId)).thenReturn(List.of(h1, h2));
        List<ChangeRequestHistory> result = historyService.getHistory(reqId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertSame(h1, result.get(0));
        assertSame(h2, result.get(1));
    }
}
