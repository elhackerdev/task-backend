package com.prueba.tareas.security;

import com.prueba.tareas.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    // Clave secreta para firmar los tokens (debe estar en variables de entorno en producción)
    private final String SECRET_KEY = "yhd3sk29vK/t5fsr7Jw9s4vP7QpTjHdPiY8t7v3cwKo=";

    /**
     * Genera un token JWT para un usuario.
     * @param user El usuario para el que se genera el token
     * @return Token JWT como String
     */
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())  // Establece el sujeto (usualmente el username)
                .claim("role", user.getRole().name())  // Añade un claim personalizado para el rol
                .setIssuedAt(new Date())  // Fecha de emisión
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Expira en 1 hora
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)  // Firma con algoritmo HS256
                .compact();  // Convierte a String compacto
    }

    /**
     * Extrae el username del token JWT.
     * @param token Token JWT
     * @return Username extraído del token
     */
    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)  // Usa la misma clave para verificar
                .parseClaimsJws(token)  // Parsea y verifica el token
                .getBody()  // Obtiene el cuerpo (claims)
                .getSubject();  // Extrae el subject (username)
    }

    /**
     * Valida si un token JWT es válido para un UserDetails dado.
     * @param token Token JWT a validar
     * @param userDetails UserDetails contra el que validar
     * @return true si el token es válido, false en caso contrario
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername());  // Compara usernames
    }
}
