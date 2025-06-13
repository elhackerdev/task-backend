package com.prueba.tareas.configuration;

import com.prueba.tareas.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@EnableWebSecurity
@Configuration // Indica que esta clase es una configuración de Spring
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    // Inyecta el filtro JWT para autenticación basada en tokens
    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * Configura la cadena de filtros de seguridad para Spring Security.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Habilita CORS con configuración por defecto
                .cors(Customizer.withDefaults())

                // Desactiva CSRF (no es necesario para APIs REST + JWT)
                .csrf(csrf -> csrf.disable())

                // Configura las reglas de autorización
                .authorizeHttpRequests(auth -> auth
                        // Permite acceso público a endpoints de autenticación
                        .requestMatchers("/auth/**").permitAll()

                        // Protege endpoints bajo /api para usuarios autenticados con rol USER o ADMIN
                        .requestMatchers("/api/**").hasAnyRole("USER", "ADMIN")

                        // Permite acceso público a Swagger (documentación API)
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api-docs/**"
                        ).permitAll()

                        // Cualquier otra solicitud requiere autenticación
                        .anyRequest().authenticated()
                )

                // Configura la política de sesión como STATELESS (sin sesiones HTTP)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Añade el filtro JWT antes del filtro de autenticación estándar
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    /**
     * Configura el codificador de contraseñas (usando BCrypt).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Algoritmo de hashing seguro
    }

    /**
     * Configura CORS para permitir solicitudes desde el frontend (Angular en este caso).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Orígenes permitidos (solo localhost:4200 en este caso)
        config.setAllowedOrigins(List.of("http://localhost:4200"));

        // Métodos HTTP permitidos
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Cabeceras permitidas
        config.setAllowedHeaders(List.of("*"));

        // Permite enviar cookies/tokens de autenticación
        config.setAllowCredentials(true);

        // Aplica esta configuración a todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
