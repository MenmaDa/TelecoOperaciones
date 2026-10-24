package com.teleco.operaciones.repository.facturacion;

import com.teleco.operaciones.model.facturacion.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FacturaRepository extends JpaRepository<Factura, UUID> {

    List<Factura> findByClienteId(UUID clienteId);

    boolean existsByNumero(String numero);
}