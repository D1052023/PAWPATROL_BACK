package eci.edu.dosw.proyecto.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import eci.edu.dosw.proyecto.enums.Faculty;
import eci.edu.dosw.proyecto.enums.RequestStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Clase que maneja la información de la solicitud de cambio.
 */
@Data
@NoArgsConstructor
@Document(collection = "changeRequests")
public class ChangeRequest {

    @Id
    private UUID id;

    private String currentSubject;   
    private String currentGroup;     

    private String targetSubject;    
    private String targetGroup;      

    private int studentId;        
    private String studentName;
    private String observations;
    private Faculty faculty;

    private RequestStatus status = RequestStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    private String processedById;
    private String processedBy; 
    private int priority;

    public ChangeRequest(String currentSubject, String targetSubject, String observations) {
        this.currentSubject = currentSubject;
        this.targetSubject = targetSubject;
        this.observations = observations;
    }
}
