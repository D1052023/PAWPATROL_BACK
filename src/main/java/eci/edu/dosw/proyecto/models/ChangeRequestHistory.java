package eci.edu.dosw.proyecto.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Clase encargada del historial de una solicitud realizada
 */
@Data
@Document(collection = "changeRequestHistory")
public class ChangeRequestHistory {
    @Id
    private String id;
    private UUID requestId;
    private LocalDateTime timestamp;
    private String actor;
    private String action;
    private String note;
    private String processedBy;
}
