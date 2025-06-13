package com.prueba.tareas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class TaskDTO {

    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 255)
    private String title;

    @Size(max = 1000)
    private String description;

    @NotNull
    @Pattern(regexp = "pendiente|en progreso|completada")
    private String status;

    private LocalDate dueDate;

    @NotNull
    @Pattern(regexp = "alta|media|baja")
    private String priority;



}
