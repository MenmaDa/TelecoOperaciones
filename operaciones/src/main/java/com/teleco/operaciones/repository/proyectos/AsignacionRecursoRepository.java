package com.teleco.operaciones.repository.proyectos;

import com.teleco.operaciones.model.proyectos.AsignacionRecurso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AsignacionRecursoRepository
        extends JpaRepository<AsignacionRecurso, UUID> {

    List<AsignacionRecurso> findByEmpleadoId(UUID empleadoId);

    List<AsignacionRecurso> findByProyectoId(UUID proyectoId);
}