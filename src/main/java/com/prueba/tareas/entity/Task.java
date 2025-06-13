package com.prueba.tareas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 255, message = "El título no puede tener más de 255 caracteres")
    private String title;

    @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
    private String description;

    @NotNull(message = "El estado es obligatorio")
    @Pattern(
            regexp = "pendiente|en progreso|completada",
            message = "El estado debe ser 'pendiente', 'en progreso' o 'completada'"
    )
    private String status;

    private LocalDate dueDate;

    @NotNull(message = "La prioridad es obligatoria")
    @Pattern(
            regexp = "alta|media|baja",
            message = "La prioridad debe ser 'alta', 'media' o 'baja'"
    )
    private String priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
