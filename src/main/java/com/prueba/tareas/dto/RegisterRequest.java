package com.prueba.tareas.dto;

import com.prueba.tareas.entity.Role;

public record RegisterRequest(String username, String password, Role role) {}
