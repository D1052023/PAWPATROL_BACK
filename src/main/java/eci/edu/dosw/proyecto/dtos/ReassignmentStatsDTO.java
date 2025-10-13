package eci.edu.dosw.proyecto.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para las estadisticas de reasignacion
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReassignmentStatsDTO {
    private String key;
    private String label;
    private long totalRequests;
    private long approved;
    private long rejected;
    private long pending;
    private long exceptionalRequested;
    private Double avgResolutionHours;

    
}
