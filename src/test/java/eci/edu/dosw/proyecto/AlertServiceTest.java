package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.models.Group;
import eci.edu.dosw.proyecto.services.AlertService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class AlertServiceTest {

    private AlertService alertService;
    private Group group;

    @BeforeEach
    void setUp() {
        alertService = new AlertService();
        group = mock(Group.class);

        when(group.getName()).thenReturn("CALD-3");
        when(group.getLoadPercentage()).thenReturn(75.0);
    }

    @Test
    void update_shouldPrintAlertMessage() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        alertService.update(group);

        String output = outContent.toString().trim();
        assertTrue(output.contains("ALERTA: Grupo CALD-3 al 75.0% de su capacidad."));
    }
}
