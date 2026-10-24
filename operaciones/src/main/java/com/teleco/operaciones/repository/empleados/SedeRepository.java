package com.teleco.operaciones.repository.empleados;

import com.teleco.operaciones.model.empleados.Sede;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SedeRepository extends JpaRepository<Sede, UUID> {

    boolean existsByNombre(String nombre);
}