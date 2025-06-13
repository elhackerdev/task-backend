package com.prueba.tareas.service;

import com.prueba.tareas.dto.AuthRequest;
import com.prueba.tareas.dto.AuthResponse;
import com.prueba.tareas.dto.RegisterRequest;
import com.prueba.tareas.entity.Role;
import com.prueba.tareas.entity.User;
import com.prueba.tareas.repository.UserRepository;
import com.prueba.tareas.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class AuthServiceTest {

    // Mock del repositorio de usuarios (simula la base de datos)
    @Mock
    private UserRepository userRepository;

    // Mock del codificador de contraseñas (simula BCrypt)
    @Mock
    private PasswordEncoder passwordEncoder;

    // Mock del servicio JWT (simula generación de tokens)
    @Mock
    private JwtService jwtService;

    // Servicio bajo prueba, con los mocks inyectados
    @InjectMocks
    private AuthService authService;

    // Configuración inicial antes de cada test
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this); // Inicializa los mocks
    }

    @Test
    void testRegister_ReturnsToken() {
        // Configuración (Arrange)
        RegisterRequest request = new RegisterRequest("usuario", "1234", Role.USER);
        User user = new User();
        user.setUsername("usuario");
        user.setPassword("hashedPassword");
        user.setRole(Role.USER);

        // Simula comportamientos:
        when(passwordEncoder.encode("1234")).thenReturn("hashedPassword"); // Simula encriptación
        when(userRepository.save(any(User.class))).thenReturn(user); // Simula guardado
        when(jwtService.generateToken(any(User.class))).thenReturn("fake-jwt-token"); // Simula token

        // Ejecución (Act)
        AuthResponse response = authService.register(request);

        // Verificaciones (Assert)
        assertNotNull(response); // Verifica que se devolvió una respuesta
        assertEquals("fake-jwt-token", response.token()); // Verifica el token
        verify(userRepository, times(1)).save(any(User.class)); // Verifica que se guardó el usuario
    }

    @Test
    void testLogin_ReturnsToken_WhenCredentialsAreCorrect() {
        // Configuración
        User user = new User();
        user.setUsername("usuario");
        user.setPassword("hashedPassword");
        user.setRole(Role.USER);

        AuthRequest request = new AuthRequest("usuario", "1234");

        // Simula comportamientos:
        when(userRepository.findByUsername("usuario")).thenReturn(Optional.of(user)); // Usuario existe
        when(passwordEncoder.matches("1234", "hashedPassword")).thenReturn(true); // Contraseña válida
        when(jwtService.generateToken(user)).thenReturn("fake-jwt-token"); // Token simulado

        // Ejecución
        AuthResponse response = authService.login(request);

        // Verificaciones
        assertNotNull(response);
        assertEquals("fake-jwt-token", response.token()); // Token esperado
    }

    @Test
    void testLogin_ThrowsException_WhenUserNotFound() {
        // Configuración
        AuthRequest request = new AuthRequest("inexistente", "1234");

        // Simula que el usuario no existe
        when(userRepository.findByUsername("inexistente")).thenReturn(Optional.empty());

        // Ejecución y Verificación
        assertThrows(UsernameNotFoundException.class, () -> authService.login(request));
    }

    @Test
    void testLogin_ThrowsException_WhenPasswordIncorrect() {
        // Configuración
        User user = new User();
        user.setUsername("usuario");
        user.setPassword("hashedPassword");

        AuthRequest request = new AuthRequest("usuario", "wrong-password");

        // Simula comportamientos:
        when(userRepository.findByUsername("usuario")).thenReturn(Optional.of(user)); // Usuario existe
        when(passwordEncoder.matches("wrong-password", "hashedPassword")).thenReturn(false); // Contraseña inválida

        // Ejecución y Verificación
        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
}
