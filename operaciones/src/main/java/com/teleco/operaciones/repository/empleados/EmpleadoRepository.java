package com.teleco.operaciones.repository.empleados;

import com.teleco.operaciones.model.empleados.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmpleadoRepository extends JpaRepository<Empleado, UUID> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);
}