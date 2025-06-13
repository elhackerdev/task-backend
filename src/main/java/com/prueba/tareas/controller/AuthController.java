package com.prueba.tareas.controller;

import com.prueba.tareas.dto.AuthRequest;
import com.prueba.tareas.dto.AuthResponse;
import com.prueba.tareas.dto.RegisterRequest;
import com.prueba.tareas.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticación", description = "API para registro y autenticación de usuarios")
@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @Operation(summary = "Registrar nuevo usuario",
            description = "Crea una nueva cuenta de usuario con los datos proporcionados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos"),
            @ApiResponse(responseCode = "409", description = "El usuario ya existe")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Parameter(description = "Datos de registro del usuario", required = true)
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @Operation(summary = "Iniciar sesión",
            description = "Autentica al usuario y devuelve un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de autenticación inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Parameter(description = "Credenciales de autenticación", required = true)
            @Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(service.login(request));
    }
}
