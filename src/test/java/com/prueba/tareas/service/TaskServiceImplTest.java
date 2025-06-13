package com.prueba.tareas.service;

import com.prueba.tareas.dto.TaskDTO;
import com.prueba.tareas.entity.Task;
import com.prueba.tareas.entity.User;
import com.prueba.tareas.exception.ResourceNotFoundException;
import com.prueba.tareas.repository.TaskRepository;
import com.prueba.tareas.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
// Clase de prueba para TaskServiceImpl que verifica el comportamiento del servicio de tareas
class TaskServiceImplTest {

    // Mock del repositorio de tareas para simular su comportamiento
    @Mock
    private TaskRepository taskRepository;

    // Mock del repositorio de usuarios para simular su comportamiento
    @Mock
    private UserRepository userRepository;

    // Mock del contexto de seguridad para simular la autenticación
    @Mock
    private SecurityContext securityContext;

    // Mock de autenticación para simular el usuario logueado
    @Mock
    private Authentication authentication;

    // Inyecta los mocks en el servicio real que se va a probar
    @InjectMocks
    private TaskServiceImpl taskService;

    // Objetos de prueba que se usarán en múltiples test cases
    private Task task;
    private TaskDTO taskDTO;
    private User user;

    // Método que se ejecuta antes de cada test para configurar el entorno
    @BeforeEach
    void setUp() {
        // Configuración del usuario de prueba
        user = new User();
        user.setId(1L);  // ID del usuario
        user.setUsername("testuser");  // Nombre de usuario

        // Configuración de la tarea de prueba
        task = new Task();
        task.setId(1L);  // ID de la tarea
        task.setTitle("Test Task");  // Título de la tarea
        task.setDescription("Test Description");  // Descripción
        task.setStatus("PENDING");  // Estado inicial
        task.setDueDate(null);  // Sin fecha de vencimiento
        task.setPriority("MEDIUM");  // Prioridad media
        task.setUser(user);  // Asignación al usuario

        // Configuración del DTO de tarea para pruebas
        taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        taskDTO.setTitle("Test Task");
        taskDTO.setDescription("Test Description");
        taskDTO.setStatus("PENDING");
        taskDTO.setDueDate(null);
        taskDTO.setPriority("MEDIUM");

        // Establece el contexto de seguridad para las pruebas
        SecurityContextHolder.setContext(securityContext);
    }

    /**
     * Prueba para verificar la creación exitosa de una tarea.
     * Debe retornar un DTO de tarea con los datos correctos.
     */
    @Test
    void createTask_ShouldReturnTaskDTO() {
        // Arrange (Preparación)
        // Configura el mock del contexto de seguridad
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        // Configura los mocks para simular el comportamiento esperado
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act (Actuación)
        // Llama al método que se está probando
        TaskDTO result = taskService.create(taskDTO);

        // Assert (Verificación)
        // Verifica que el resultado no sea nulo
        assertNotNull(result);
        // Verifica que el título coincida con el esperado
        assertEquals(taskDTO.getTitle(), result.getTitle());
        // Verifica que el método save del repositorio se llamó exactamente una vez
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    /**
     * Prueba para verificar la actualización exitosa de una tarea.
     * Debe retornar el DTO actualizado con los nuevos valores.
     */
    @Test
    void updateTask_ShouldReturnUpdatedTaskDTO() {
        // Arrange
        // Crea un DTO con datos actualizados
        TaskDTO updatedDTO = new TaskDTO();
        updatedDTO.setTitle("Updated Task");
        updatedDTO.setDescription("Updated Description");
        updatedDTO.setStatus("COMPLETED");

        // Configura los mocks
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        TaskDTO result = taskService.update(1L, updatedDTO);

        // Assert
        assertNotNull(result);
        // Verifica que los campos se actualizaron correctamente
        assertEquals("Updated Task", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals("COMPLETED", result.getStatus());
        // Verifica que se llamó al método save
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    /**
     * Prueba para verificar que se lance una excepción cuando se intenta
     * actualizar una tarea que no existe.
     */
    @Test
    void updateTask_WhenTaskNotFound_ShouldThrowException() {
        // Arrange
        // Simula que no se encuentra la tarea
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        // Verifica que se lance la excepción esperada
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.update(1L, taskDTO);
        });
    }

    /**
     * Prueba para verificar la eliminación exitosa de una tarea.
     * Debe llamar al método delete del repositorio.
     */
    @Test
    void deleteTask_ShouldCallRepositoryDelete() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // Act
        taskService.delete(1L);

        // Assert
        // Verifica que se llamó al método delete con la tarea correcta
        verify(taskRepository, times(1)).delete(task);
    }

    /**
     * Prueba para verificar que se lance una excepción cuando se intenta
     * eliminar una tarea que no existe.
     */
    @Test
    void deleteTask_WhenTaskNotFound_ShouldThrowException() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.delete(1L);
        });
    }

    /**
     * Prueba para verificar que un usuario administrador puede obtener
     * todas las tareas del sistema.
     */
    @Test
    void getAllTasks_ForAdminUser_ShouldReturnAllTasks() {
        // Arrange
        // Configura un usuario con rol ADMIN
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_ADMIN"));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task));

        // Act
        List<TaskDTO> result = taskService.getAll();

        // Assert
        assertEquals(1, result.size());  // Verifica que se retornó una tarea
        // Verifica que se llamó a findAll() y no a findByUserUsername()
        verify(taskRepository, times(1)).findAll();
        verify(taskRepository, never()).findByUserUsername(anyString());
    }

    /**
     * Prueba para verificar que un usuario regular solo puede obtener
     * sus propias tareas.
     */
    @Test
    void getAllTasks_ForRegularUser_ShouldReturnUserTasks() {
        // Arrange
        // Configura un usuario con rol USER
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER"));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(taskRepository.findByUserUsername("testuser")).thenReturn(Arrays.asList(task));

        // Act
        List<TaskDTO> result = taskService.getAll();

        // Assert
        assertEquals(1, result.size());  // Verifica que se retornó una tarea
        // Verifica que se llamó al método correcto para usuarios regulares
        verify(taskRepository, never()).findAll();
        verify(taskRepository, times(1)).findByUserUsername("testuser");
    }

    /**
     * Prueba para verificar que un administrador puede filtrar tareas
     * por estado y prioridad.
     */
    @Test
    void findByEstadoAndPrioridad_ForAdminUser_ShouldReturnFilteredTasks() {
        // Arrange
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_ADMIN"));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        // Simula el filtrado sin restricción de usuario
        when(taskRepository.findByFilters(null, "PENDING", "MEDIUM"))
                .thenReturn(Arrays.asList(task));

        // Act
        List<TaskDTO> result = taskService.findByEstadoAndPrioridad("PENDING", "MEDIUM");

        // Assert
        assertEquals(1, result.size());
        // Verifica que se usaron los filtros correctos para admin
        verify(taskRepository, times(1)).findByFilters(null, "PENDING", "MEDIUM");
    }

    /**
     * Prueba para verificar que un usuario regular puede filtrar sus tareas
     * por estado y prioridad.
     */
    @Test
    void findByEstadoAndPrioridad_ForRegularUser_ShouldReturnUserFilteredTasks() {
        // Arrange
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER"));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        // Simula el filtrado restringido al usuario
        when(taskRepository.findByFilters("testuser", "PENDING", "MEDIUM"))
                .thenReturn(Arrays.asList(task));

        // Act
        List<TaskDTO> result = taskService.findByEstadoAndPrioridad("PENDING", "MEDIUM");

        // Assert
        assertEquals(1, result.size());
        // Verifica que se usaron los filtros correctos para usuario regular
        verify(taskRepository, times(1)).findByFilters("testuser", "PENDING", "MEDIUM");
    }

    /**
     * Prueba para verificar la conversión de Entidad a DTO.
     * Todos los campos deben mapearse correctamente.
     */
    @Test
    void mapToDTO_ShouldConvertEntityToDTO() {
        // Act
        TaskDTO result = taskService.mapToDTO(task);

        // Assert
        // Verifica que todos los campos se mapearon correctamente
        assertEquals(task.getId(), result.getId());
        assertEquals(task.getTitle(), result.getTitle());
        assertEquals(task.getDescription(), result.getDescription());
        assertEquals(task.getStatus(), result.getStatus());
        assertEquals(task.getPriority(), result.getPriority());
    }

    /**
     * Prueba para verificar la conversión de DTO a Entidad.
     * Todos los campos deben mapearse correctamente.
     */
    @Test
    void mapToEntity_ShouldConvertDTOToEntity() {
        // Act
        Task result = taskService.mapToEntity(taskDTO);

        // Assert
        // Verifica que todos los campos se mapearon correctamente
        assertEquals(taskDTO.getId(), result.getId());
        assertEquals(taskDTO.getTitle(), result.getTitle());
        assertEquals(taskDTO.getDescription(), result.getDescription());
        assertEquals(taskDTO.getStatus(), result.getStatus());
        assertEquals(taskDTO.getPriority(), result.getPriority());
    }
}