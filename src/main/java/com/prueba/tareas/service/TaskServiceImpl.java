package com.prueba.tareas.service;

import com.prueba.tareas.dto.TaskDTO;
import com.prueba.tareas.entity.Task;
import com.prueba.tareas.entity.User;
import com.prueba.tareas.exception.ResourceNotFoundException;
import com.prueba.tareas.repository.TaskRepository;
import com.prueba.tareas.repository.UserRepository;
import com.prueba.tareas.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository repository; // Repositorio para operaciones con tareas

    @Autowired
    private UserRepository userRepository; // Repositorio para operaciones con usuarios

    /**
     * Convierte una entidad Task a DTO
     */
    TaskDTO mapToDTO(Task entity) {
        TaskDTO dto = new TaskDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setDueDate(entity.getDueDate());
        dto.setPriority(entity.getPriority());
        return dto;
    }

    /**
     * Convierte un DTO a entidad Task
     */
    Task mapToEntity(TaskDTO dto) {
        Task entity = new Task();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        entity.setDueDate(dto.getDueDate());
        entity.setPriority(dto.getPriority());
        return entity;
    }

    @Override
    public TaskDTO create(TaskDTO dto) {
        // 1. Convertir DTO a entidad
        Task task = mapToEntity(dto);

        // 2. Obtener usuario actual desde el contexto de seguridad
        String username = SecurityUtil.getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. Asignar usuario a la tarea
        task.setUser(user);

        // 4. Guardar y devolver como DTO
        return mapToDTO(repository.save(task));
    }

    @Override
    public TaskDTO update(Long id, TaskDTO dto) {
        // 1. Buscar tarea existente
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task no encontrada con ID: " + id));

        // 2. Actualizar campos
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setDueDate(dto.getDueDate());
        task.setPriority(dto.getPriority());

        // 3. Guardar cambios
        return mapToDTO(repository.save(task));
    }

    @Override
    public void delete(Long id) {
        // 1. Verificar existencia
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task no encontrada con ID: " + id));

        // 2. Eliminar
        repository.delete(task);
    }

    @Override
    public List<TaskDTO> getAll() {
        // 1. Obtener información de autenticación
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // 2. Determinar si es admin
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        // 3. Obtener tareas según rol
        List<Task> tasks = isAdmin ? repository.findAll() : repository.findByUserUsername(username);

        // 4. Convertir a DTOs
        return tasks.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> findByEstadoAndPrioridad(String estado, String prioridad) {
        // 1. Determinar usuario (null si es admin)
        String username = isAdminUser() ? null : SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Buscar con filtros
        List<Task> tasks = repository.findByFilters(username, estado, prioridad);

        // 3. Convertir a DTOs
        return tasks.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Verifica si el usuario actual tiene rol ADMIN
     */
    private boolean isAdminUser() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));
    }
}
