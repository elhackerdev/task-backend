package com.prueba.tareas.controller;

import com.prueba.tareas.dto.TaskDTO;
import com.prueba.tareas.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Tasks", description = "API para gestión de tareas")
@RestController
@CrossOrigin
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Crear una nueva tarea", description = "Crea una nueva tarea con los datos proporcionados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarea creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping
    public ResponseEntity<TaskDTO> create(
            @Parameter(description = "Datos de la tarea a crear")
            @Valid @RequestBody TaskDTO taskDTO) {
        TaskDTO createdTask = taskService.create(taskDTO);
        return ResponseEntity.ok(createdTask);
    }

    @Operation(summary = "Obtener todas las tareas",
            description = "Retorna todas las tareas. Puede filtrarse por estado y/o prioridad")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tareas obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAll(
            @Parameter(description = "Estado para filtrar las tareas")
            @RequestParam(required = false) String estado,

            @Parameter(description = "Prioridad para filtrar las tareas")
            @RequestParam(required = false) String prioridad) {

        List<TaskDTO> tasks;
        if (estado != null || prioridad != null) {
            tasks = taskService.findByEstadoAndPrioridad(estado, prioridad);
        } else {
            tasks = taskService.getAll();
        }
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Actualizar una tarea",
            description = "Actualiza la tarea con el ID proporcionado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarea actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> update(
            @Parameter(description = "ID de la tarea a actualizar")
            @PathVariable Long id,

            @Parameter(description = "Datos actualizados de la tarea")
            @Valid @RequestBody TaskDTO taskDTO) {
        TaskDTO updatedTask = taskService.update(id, taskDTO);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(summary = "Eliminar una tarea",
            description = "Elimina la tarea con el ID proporcionado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarea eliminada exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la tarea a eliminar")
            @PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

