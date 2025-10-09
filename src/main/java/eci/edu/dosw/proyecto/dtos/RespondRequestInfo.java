package eci.edu.dosw.proyecto.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespondRequestInfo {
    private RequestDecisionDTO decision;
    private RequestDatesDTO dates;
}
