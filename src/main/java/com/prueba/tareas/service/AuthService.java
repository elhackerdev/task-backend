package com.prueba.tareas.service;

import com.prueba.tareas.dto.AuthRequest;
import com.prueba.tareas.dto.AuthResponse;
import com.prueba.tareas.dto.RegisterRequest;
import com.prueba.tareas.entity.User;
import com.prueba.tareas.repository.UserRepository;
import com.prueba.tareas.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    // Dependencias inyectadas
    private final UserRepository userRepository;     // Acceso a la base de datos de usuarios
    private final PasswordEncoder passwordEncoder;  // Codificador de contraseñas (BCrypt)
    private final JwtService jwtService;           // Servicio para generar tokens JWT

    // Constructor con inyección de dependencias
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * @param request Datos de registro (username, password, role)
     * @return AuthResponse con el token JWT generado
     */
    public AuthResponse register(RegisterRequest request) {
        // 1. Crear nuevo usuario
        User user = new User();
        user.setUsername(request.username());

        // 2. Codificar la contraseña antes de almacenarla
        user.setPassword(passwordEncoder.encode(request.password()));

        user.setRole(request.role());

        // 3. Guardar en base de datos
        userRepository.save(user);

        // 4. Generar token JWT para el nuevo usuario
        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }

    /**
     * Autentica a un usuario existente.
     * @param request Credenciales (username y password)
     * @return AuthResponse con el token JWT
     * @throws UsernameNotFoundException Si el usuario no existe
     * @throws BadCredentialsException Si la contraseña no coincide
     */
    public AuthResponse login(AuthRequest request) {
        // 1. Buscar usuario en la base de datos
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // 2. Verificar contraseña
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        // 3. Generar y devolver token JWT
        return new AuthResponse(jwtService.generateToken(user));
    }
}
