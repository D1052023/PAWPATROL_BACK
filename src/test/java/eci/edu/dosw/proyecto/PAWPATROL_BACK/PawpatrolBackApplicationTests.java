package eci.edu.dosw.proyecto.PAWPATROL_BACK;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import eci.edu.dosw.proyecto.configs.TestJwtKeyConfig;
import org.springframework.test.context.ActiveProfiles;

@Import(TestJwtKeyConfig.class)
@ActiveProfiles("test")
@SpringBootTest
public class PawpatrolBackApplicationTests {

    @Test
    void contextLoads() {
    }
}
