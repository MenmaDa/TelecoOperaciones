package com.teleco.operaciones.repository.facturacion;

import com.teleco.operaciones.model.facturacion.ProductoServicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductoServicioRepository
        extends JpaRepository<ProductoServicio, UUID> {

    List<ProductoServicio> findByActivoTrue();

    List<ProductoServicio> findByCategoria(String categoria);
}