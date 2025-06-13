package com.prueba.tareas.repository;

import com.prueba.tareas.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserUsername(String username);

    @Query("SELECT t FROM Task t WHERE " +
            "(:username IS NULL OR t.user.username = :username) AND " +
            "(:estado IS NULL OR t.status = :estado) AND " +
            "(:prioridad IS NULL OR t.priority = :prioridad)")
    List<Task> findByFilters(
            @Param("username") String username,
            @Param("estado") String estado,
            @Param("prioridad") String prioridad);
}
