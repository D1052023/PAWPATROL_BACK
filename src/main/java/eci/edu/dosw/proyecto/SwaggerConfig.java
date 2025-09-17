package eci.edu.dosw.proyecto;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
    
    /**
     * Define un bean de tipo OpenAPI que será utilizado por Springdoc OpenAPI
     * para generar la documentación de la API automáticamente.
     *
     * @return un objeto OpenAPI con información básica de la API.
     */
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
            .info(new Info()
                .title("SIRHA API")
                .description("API para la gestión de reasignación de horarios académicos")
                .version("1.0"));
    }
}
