package eci.edu.dosw.proyecto.PAWPATROL_BACK;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("eci.edu.dosw.proyecto")
public class PawpatrolBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(PawpatrolBackApplication.class, args);
	}

}
