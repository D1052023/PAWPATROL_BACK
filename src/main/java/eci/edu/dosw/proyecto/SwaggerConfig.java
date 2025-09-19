package eci.edu.dosw.proyecto;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;


/**
 * Clase que configura la información que aparecerá en la documentación de la API.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Define un bean de tipo OpenAPI que sera utilizado por Springdoc OpenAPI
     * para generar la documentación de la API automáticamente.
     *
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
