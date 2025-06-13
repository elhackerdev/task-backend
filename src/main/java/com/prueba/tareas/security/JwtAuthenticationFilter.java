package com.prueba.tareas.security;

import com.prueba.tareas.entity.User;
import com.prueba.tareas.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
// Filtro que se ejecuta UNA VEZ por cada solicitud HTTP
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;      // Servicio para operaciones con JWT
    private final UserRepository userRepository; // Repositorio de usuarios

    // Constructor con dependencias inyectadas
    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Extraer el token del encabezado "Authorization"
        String authHeader = request.getHeader("Authorization");

        // Si no hay token o no comienza con "Bearer ", continuar sin autenticación
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraer el token (eliminando "Bearer ")
        String token = authHeader.substring(7);

        // 3. Extraer el nombre de usuario del token
        String username = jwtService.extractUsername(token);

        // Si hay un usuario y no hay autenticación previa en el contexto de seguridad
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 4. Buscar el usuario en la base de datos
            User user = userRepository.findByUsername(username).orElse(null);

            // 5. Validar el token contra el usuario encontrado
            if (user != null && jwtService.isTokenValid(token, (UserDetails) user)) {

                // 6. Crear objeto de autenticación Spring Security
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user,                   // Principal (usuario autenticado)
                        null,                   // Credenciales (null porque JWT no las necesita)
                        // Convertir el rol del usuario a GrantedAuthority (ej: "ADMIN" -> "ROLE_ADMIN")
                        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
                );

                // 7. Establecer la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 8. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
