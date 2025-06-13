package com.prueba.tareas.service;

import com.prueba.tareas.dto.TaskDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TaskService {
    TaskDTO create(TaskDTO taskDTO);
    TaskDTO update(Long id, TaskDTO taskDTO);
    void delete(Long id);
    List<TaskDTO> getAll();
    List<TaskDTO> findByEstadoAndPrioridad(String estado, String prioridad);
}
