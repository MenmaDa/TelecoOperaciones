package com.teleco.operaciones.repository.facturacion;

import com.teleco.operaciones.model.facturacion.DetalleFactura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DetalleFacturaRepository
        extends JpaRepository<DetalleFactura, UUID> {

    List<DetalleFactura> findByFacturaId(UUID facturaId);
}