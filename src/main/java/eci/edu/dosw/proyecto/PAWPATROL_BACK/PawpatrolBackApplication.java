package eci.edu.dosw.proyecto.PAWPATROL_BACK;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "eci.edu.dosw.proyecto")  
@EnableJpaRepositories(basePackages = "eci.edu.dosw.proyecto")
@EntityScan(basePackages = "eci.edu.dosw.proyecto")
public class PawpatrolBackApplication {
    public static void main(String[] args) {
        SpringApplication.run(PawpatrolBackApplication.class, args);
    }
}
