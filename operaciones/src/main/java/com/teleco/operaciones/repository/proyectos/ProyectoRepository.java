package com.teleco.operaciones.repository.proyectos;

import com.teleco.operaciones.model.proyectos.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProyectoRepository extends JpaRepository<Proyecto, UUID> {

    List<Proyecto> findByClienteId(UUID clienteId);

    List<Proyecto> findByContratoId(UUID contratoId);
}