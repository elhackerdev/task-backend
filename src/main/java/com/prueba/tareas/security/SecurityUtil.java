package com.prueba.tareas.security;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    /**
     * Obtiene el nombre de usuario (username) del usuario actualmente autenticado.
     *
     * @return String con el username del usuario autenticado
     * @throws IllegalStateException si no hay usuario autenticado o el contexto de seguridad es nulo
     */
    public static String getCurrentUsername() {
        return SecurityContextHolder.getContext()  // Accede al contexto de seguridad de Spring
                .getAuthentication()              // Obtiene el objeto de autenticación actual
                .getName();                      // Extrae el nombre de usuario (username)
    }
}
