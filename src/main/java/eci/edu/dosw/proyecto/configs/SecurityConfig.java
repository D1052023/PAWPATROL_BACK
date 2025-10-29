package eci.edu.dosw.proyecto.configs;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import eci.edu.dosw.proyecto.services.JwtService;
import eci.edu.dosw.proyecto.enums.Role;
import org.springframework.http.HttpMethod;
import lombok.RequiredArgsConstructor;

/**
 * Clase configuración que maneja la seguridad de la api.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtService);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "https://pawpatrol-front.vercel.app"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*")); 
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, ApiPaths.DEANERY).hasAnyRole(Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.DEANERY).hasAnyRole(Role.SECRETARIAT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.DEANERY_BY_ID).hasAnyRole(Role.SECRETARIAT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.DEANERY_FACULTY).hasAnyRole(Role.SECRETARIAT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, ApiPaths.DEANERY_BY_ID).hasAnyRole(Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, ApiPaths.DEANERY_BY_ID).hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, ApiPaths.DEANERY_REQUEST + "/respond").hasAnyRole(Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, ApiPaths.DEANERY_REQUEST + "/respondInfo").hasAnyRole(Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, ApiPaths.DEANERY_REQUEST).hasAnyRole(Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, ApiPaths.DEANERY_REQUEST).hasAnyRole(Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.DEANERY_REQUEST_HISTORY).hasAnyRole(Role.SECRETARIAT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.DEANERY_REQUESTS_EXCEPTIONAL).hasAnyRole(Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, ApiPaths.DEANERY_REQUEST).hasAnyRole(Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.DEANERY_STUDENT_REQUESTS_EXCEPTIONAL).hasAnyRole(Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.DEANERY_REQUESTS_EXCEPTIONAL_ALL).hasAnyRole(Role.SECRETARIAT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.DEANERY_STUDENT).hasAnyRole(Role.SECRETARIAT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.DEANERY_GROUP_MAX, ApiPaths.DEANERY_GROUP_CURRENT, ApiPaths.DEANERY_GROUP_WAITING).hasAnyRole(Role.SECRETARIAT.name(), Role.TEACHER.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, ApiPaths.GROUPS).hasAnyRole(Role.SECRETARIAT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.GROUPS).hasAnyRole(Role.SECRETARIAT.name(), Role.STUDENT.name(), Role.TEACHER.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.GROUP_BY_ID).hasAnyRole(Role.SECRETARIAT.name(), Role.STUDENT.name(), Role.TEACHER.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, ApiPaths.GROUP_BY_ID).hasAnyRole(Role.SECRETARIAT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PATCH, ApiPaths.GROUP_BY_ID, ApiPaths.GROUP_ID_CAPACITY).hasAnyRole(Role.SECRETARIAT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, ApiPaths.GROUP_BY_ID).hasAnyRole(Role.SECRETARIAT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.GROUP_GROUPID_MAX, ApiPaths.GROUP_GROUPID_CURRENT, ApiPaths.GROUP_GROUPID_ENROLLED, ApiPaths.GROUP_SCHEDULE, ApiPaths.GROUP_WAITLIST, ApiPaths.GROUP_WAITLIST_GROUPID)
                    .hasAnyRole(Role.SECRETARIAT.name(), Role.STUDENT.name(), Role.TEACHER.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, ApiPaths.GROUP_ASSIGN_TEACHER, ApiPaths.GROUP_REMOVE_TEACHER, ApiPaths.GROUP_SCHEDULE, ApiPaths.GROUP_UPDATE_DAY)
                    .hasAnyRole(Role.SECRETARIAT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, ApiPaths.GROUP_DELETE_SCHEDULE, ApiPaths.GROUP_DELETE_DAY)
                    .hasAnyRole(Role.SECRETARIAT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.GROUP_TEACHER, ApiPaths.GROUP_SUBJECT)
                    .hasAnyRole(Role.SECRETARIAT.name(), Role.STUDENT.name(), Role.TEACHER.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, ApiPaths.GROUP_ADD_STUDENT)
                    .hasAnyRole(Role.SECRETARIAT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, ApiPaths.GROUP_ADD_STUDENT)
                    .hasAnyRole(Role.SECRETARIAT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.REPORT_SUBJECTS, ApiPaths.REPORT_GROUPS, ApiPaths.REPORT_DEANERIES, ApiPaths.REPORT_GLOBAL)
                    .hasAnyRole(Role.SECRETARIAT.name(), Role.TEACHER.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, ApiPaths.SECRETARIAT).hasAnyRole(Role.SECRETARIAT.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.SECRETARIAT, ApiPaths.SECRETARIAT_BY_ID).hasAnyRole(Role.SECRETARIAT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, ApiPaths.SECRETARIAT_BY_ID).hasAnyRole(Role.SECRETARIAT.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, ApiPaths.SECRETARIAT_BY_ID).hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, ApiPaths.SECRETARIAT_BY_ID + "/request-dates").hasAnyRole(Role.SECRETARIAT.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, ApiPaths.SECRETARIAT_REQUESTS + "/respond", ApiPaths.SECRETARIAT_REQUESTS + "/respondInfo")
                    .hasAnyRole(Role.SECRETARIAT.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.SECRETARIAT_REQUESTS_FACULTY_STATUS, ApiPaths.SECRETARIAT_REQUESTS_FACULTY_PRIORITY)
                    .hasAnyRole(Role.SECRETARIAT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.SECRETARIAT_GROUP_MAX, ApiPaths.SECRETARIAT_GROUP_CURRENT, ApiPaths.SECRETARIAT_GROUP_WAIT)
                    .hasAnyRole(Role.SECRETARIAT.name(), Role.STUDENT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.SECRETARIAT_STUDENT).hasAnyRole(Role.SECRETARIAT.name(), Role.STUDENT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, ApiPaths.SECRETARIAT_REQUESTS).hasAnyRole(Role.SECRETARIAT.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, ApiPaths.SECRETARIAT_REQUESTS).hasAnyRole(Role.SECRETARIAT.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, ApiPaths.SUBJECTS).hasAnyRole(Role.SECRETARIAT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.SUBJECTS, ApiPaths.SUBJECT_BY_ID, ApiPaths.SUBJECT_TEACHER)
                    .hasAnyRole(Role.SECRETARIAT.name(), Role.STUDENT.name(), Role.TEACHER.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, ApiPaths.SUBJECT_BY_ID, ApiPaths.SUBJECT_CAPACITY)
                    .hasAnyRole(Role.SECRETARIAT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PATCH, ApiPaths.SUBJECT_BY_ID).hasAnyRole(Role.SECRETARIAT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, ApiPaths.SUBJECT_BY_ID).hasAnyRole(Role.SECRETARIAT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, ApiPaths.SUBJECT_ASSIGN)
                    .hasAnyRole(Role.SECRETARIAT.name(), Role.STUDENT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, ApiPaths.SUBJECT_REMOVE)
                    .hasAnyRole(Role.SECRETARIAT.name(), Role.STUDENT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, ApiPaths.STUDENTS).hasAnyRole(Role.STUDENT.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.STUDENTS, ApiPaths.STUDENT_BY_ID).hasAnyRole(Role.STUDENT.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.STUDENT_BY_ID).hasAnyRole(Role.STUDENT.name(), Role.ADMIN.name(), Role.SECRETARIAT.name(), Role.DEANERY.name(), Role.TEACHER.name())
                .requestMatchers(HttpMethod.PUT, ApiPaths.STUDENT_BY_ID).hasAnyRole(Role.STUDENT.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PATCH, ApiPaths.STUDENT_BY_ID).hasAnyRole(Role.STUDENT.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, ApiPaths.STUDENT_BY_ID).hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.STUDENT_REQUESTS, ApiPaths.STUDENT_REQUESTS_STATUS, ApiPaths.STUDENT_ACADEMIC_PLAN)
                    .hasAnyRole(Role.SECRETARIAT.name(), Role.STUDENT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, ApiPaths.TEACHERS).hasAnyRole(Role.SECRETARIAT.name(), Role.TEACHER.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.TEACHERS, ApiPaths.TEACHER_BY_ID, ApiPaths.TEACHER_EMAIL)
                    .hasAnyRole(Role.SECRETARIAT.name(), Role.TEACHER.name(), Role.STUDENT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.TEACHER_BY_ID, ApiPaths.TEACHER_BY_ID, ApiPaths.TEACHER_EMAIL)
                    .hasAnyRole(Role.SECRETARIAT.name(), Role.TEACHER.name(), Role.STUDENT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, ApiPaths.TEACHER_BY_ID).hasAnyRole(Role.SECRETARIAT.name(), Role.TEACHER.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PATCH, ApiPaths.TEACHER_BY_ID).hasAnyRole(Role.SECRETARIAT.name(), Role.TEACHER.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, ApiPaths.TEACHER_BY_ID).hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, ApiPaths.CHANGEREQUEST).hasAnyRole(Role.STUDENT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.CHANGEREQUEST).hasAnyRole(Role.SECRETARIAT.name(), Role.STUDENT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.EXCEPTIONALREQUEST_BY_STUDENT).hasAnyRole(Role.SECRETARIAT.name(), Role.STUDENT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.CHANGEREQUEST_STATUS).hasAnyRole(Role.SECRETARIAT.name(), Role.STUDENT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.CHANGEREQUEST_SCHEDULE).hasAnyRole(Role.SECRETARIAT.name(), Role.STUDENT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.CHANGEREQUEST_PREVIOUSSCHEDULE).hasAnyRole(Role.SECRETARIAT.name(), Role.STUDENT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, ApiPaths.CHANGERQUEST_EXCEPTIONAL).hasAnyRole(Role.SECRETARIAT.name(), Role.STUDENT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.CHANGEREQUEST_STUDENT_ID).hasAnyRole(Role.SECRETARIAT.name(), Role.STUDENT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, ApiPaths.CHANGEREQUEST_STUDENT_ID).hasAnyRole(Role.SECRETARIAT.name(), Role.STUDENT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, ApiPaths.CHANGEREQUEST_STUDENT_ID).hasAnyRole(Role.SECRETARIAT.name(), Role.STUDENT.name(), Role.DEANERY.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, ApiPaths.CHANGEREQUEST_HISTORY).hasAnyRole(Role.SECRETARIAT.name(), Role.STUDENT.name(), Role.DEANERY.name(), Role.ADMIN.name())

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
