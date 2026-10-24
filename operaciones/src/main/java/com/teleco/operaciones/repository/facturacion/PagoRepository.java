package com.teleco.operaciones.repository.facturacion;

import com.teleco.operaciones.model.facturacion.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PagoRepository extends JpaRepository<Pago, UUID> {

    List<Pago> findByFacturaId(UUID facturaId);
}