package com.teleco.operaciones.repository.proyectos;

import com.teleco.operaciones.model.proyectos.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TareaRepository extends JpaRepository<Tarea, UUID> {

    List<Tarea> findByProyectoId(UUID proyectoId);

    List<Tarea> findByResponsableUsuarioId(UUID usuarioId);
}