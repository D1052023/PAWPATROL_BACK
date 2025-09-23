package eci.edu.dosw.proyecto.PAWPATROL_BACK;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "eci.edu.dosw.proyecto")  
@EnableMongoRepositories(basePackages = "eci.edu.dosw.proyecto")
public class PawpatrolBackApplication {
    public static void main(String[] args) {
        SpringApplication.run(PawpatrolBackApplication.class, args);
    }
}