package eci.edu.dosw.proyecto.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import eci.edu.dosw.proyecto.services.JwtService;
import org.springframework.http.HttpMethod;
import lombok.RequiredArgsConstructor;

/**
 * Clase de configuración de seguridad, donde define las reglas de autorización, autenticación.
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()

                .requestMatchers("/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/deaneries").hasAnyRole("DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/deaneries").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/deaneries/{deaneryId}").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/deaneries/faculty/{faculty}").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/deaneries/{deaneryId}").hasAnyRole("DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/deaneries/{deaneryId}").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/deaneries/{deaneryId}/requests/{requestId}/respond").hasAnyRole("DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/deaneries/{deaneryId}/requests/{requestId}/respondInfo").hasAnyRole("DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/deaneries/{deaneryId}/requests/{requestId}").hasAnyRole("DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/deaneries/{deaneryId}/requests/{requestId}").hasAnyRole("DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/deaneries/requests/faculty/{faculty}/status/{status}").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/deaneries/requests/faculty/{faculty}/priority/{priority}").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/deaneries/requests/faculty/{faculty}/priority}").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/deaneries/requests/search").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/deaneries/requests/priority").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/deaneries/requests/priority/{priority}").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/deaneries/{deaneryId}/requests/exceptional").hasAnyRole("DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/deaneries/{deaneryId}/requests/{requestId}").hasAnyRole("DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/deaneries/{deaneryId}/students/{studentId}/requests/exceptional").hasAnyRole("DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/deaneries/requests/exceptional").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/deaneries/students/{studentId}").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/deaneries/groups/{groupId}/MaxCapacity").hasAnyRole("SECRETARIAT", "TEACHER", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/deaneries/groups/{groupId}/CurrentCapacity").hasAnyRole("SECRETARIAT", "TEACHER", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/deaneries/groups/{groupId}/waitingList").hasAnyRole("SECRETARIAT", "TEACHER", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/deaneries/requests/{requestId}/history").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/groups").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/groups").hasAnyRole("SECRETARIAT", "STUDENT", "TEACHER", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/groups/{id}").hasAnyRole("SECRETARIAT", "STUDENT", "TEACHER", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/groups/{id}").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/groups/{id}").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/groups/{id}").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/groups/{id}/capacity").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/groups/{groupId}/MaxCapacity").hasAnyRole("SECRETARIAT", "STUDENT", "TEACHER", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/groups/{groupId}/CurrentCapacity").hasAnyRole("SECRETARIAT", "STUDENT", "TEACHER", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/groups/{groupId}/enrolled").hasAnyRole("SECRETARIAT", "STUDENT", "TEACHER", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/groups/{groupId}/AssignTeacher/{teacherId}").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/groups/{groupId}/RemoveTeacher").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/groups/teacher/{teacherId}").hasAnyRole("SECRETARIAT", "STUDENT", "TEACHER", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/groups/subject/{subjectId}").hasAnyRole("SECRETARIAT", "STUDENT", "TEACHER", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/groups/{groupId}/students/{studentId}").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/groups/{groupId}/students/{studentId}").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/groups/{id}/waitlist").hasAnyRole("SECRETARIAT", "STUDENT", "TEACHER", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/groups/{groupId}/waitlist").hasAnyRole("SECRETARIAT", "TEACHER", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/groups/{groupId}/schedule").hasAnyRole("SECRETARIAT", "STUDENT", "TEACHER", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/groups/{groupId}/AddSchedule").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/groups/{groupId}/schedule").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/groups/{groupId}/schedule/day/{day}").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/groups/{groupId}/schedule").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/groups/{groupId}/schedule/day/{day}").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")

                .requestMatchers(HttpMethod.GET, "/reports/subjects").hasAnyRole("SECRETARIAT", "TEACHER", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/reports/groups").hasAnyRole("SECRETARIAT", "TEACHER", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/reports/deaneries").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/reports/global").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/secretariat").hasAnyRole("SECRETARIAT", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/secretariat/{id}").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/secretariat").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/secretariat/{id}").hasAnyRole("SECRETARIAT", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/secretariat/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/secretariat/{id}/request-dates").hasAnyRole("SECRETARIAT", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/secretariat/requests/{requestId}/respond").hasAnyRole("SECRETARIAT", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/secretariat/requests/{requestId}/respondInfo").hasAnyRole("SECRETARIAT", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/secretariat/requests/faculty/{faculty}/status/{status}").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/secretariat/requests/faculty/{faculty}/priority").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/secretariat/requests/search").hasAnyRole("SECRETARIAT", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/secretariat/requests/priority").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/secretariat/requests/priority/{priority}").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/secretariat/requests/history").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/secretariat/groups/{groupId}/MaxCapacity").hasAnyRole("SECRETARIAT","STUDENT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/secretariat/groups/{groupId}/CurrentCapacity").hasAnyRole("SECRETARIAT", "STUDENT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/secretariat/groups/{groupId}/waitingList").hasAnyRole("SECRETARIAT", "STUDENT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/secretariat/students/{studentId}").hasAnyRole("SECRETARIAT", "STUDENT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/secretariat/requests/{requestId}").hasAnyRole("SECRETARIAT", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/secretariat/requests/{requestId}").hasAnyRole("SECRETARIAT", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/subjects").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/subjects").hasAnyRole("SECRETARIAT", "STUDENT", "TEACHER", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/subjects/{id}").hasAnyRole("SECRETARIAT", "STUDENT", "TEACHER", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/subjects/{id}").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/subjects/{id}").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/subjects/{id}").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/subjects/teacher/{teacherId}").hasAnyRole("SECRETARIAT", "STUDENT", "TEACHER", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.PATCH,"/subjects/{subjectId}/capacity").hasAnyRole("SECRETARIAT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.POST,"/subjects/{subjectId}/assign/{studentId}").hasAnyRole("SECRETARIAT", "STUDENT","DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.DELETE,"/subjects/{subjectId}/remove/{studentId}").hasAnyRole("SECRETARIAT", "STUDENT","DEANERY", "ADMIN")

                .requestMatchers(HttpMethod.GET, "/reports/subjects").hasAnyRole("SECRETARIAT", "STUDENT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/reports/groups").hasAnyRole("SECRETARIAT", "STUDENT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/reports/deaneries").hasAnyRole("SECRETARIAT", "STUDENT", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/reports/global").hasAnyRole("SECRETARIAT", "STUDENT", "DEANERY", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/students").hasAnyRole("STUDENT", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/students").hasAnyRole("STUDENT", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/students/{id}").hasAnyRole("STUDENT", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/students/{id}").hasAnyRole("STUDENT", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/students/{id}").hasAnyRole("STUDENT", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/students/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/students/{id}/requests").hasAnyRole("STUDENT", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/students/{id}/requestsStatus").hasAnyRole("STUDENT", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/students/{studentId}/academic-plan").hasAnyRole("SECRETARIAT", "STUDENT", "DEANERY", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/teachers").hasAnyRole("SECRETARIAT", "TEACHER", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/teachers").hasAnyRole("SECRETARIAT", "TEACHER", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/teachers/{id}").hasAnyRole("SECRETARIAT", "TEACHER", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/teachers/email/{email}").hasAnyRole("SECRETARIAT", "TEACHER", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/teachers/{id}").hasAnyRole("SECRETARIAT", "TEACHER", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/teachers/{id}").hasAnyRole("SECRETARIAT", "TEACHER", "DEANERY", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/teachers/{id}").hasRole("ADMIN")

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