package com.teleco.operaciones.repository.facturacion;

import com.teleco.operaciones.model.facturacion.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FacturaRepository extends JpaRepository<Factura, UUID> {

    boolean existsByNumero(String numero);

    boolean existsByNumeroAndIdNot(String numero, UUID id);

    List<Factura> findByClienteId(UUID clienteId);

    List<Factura> findByEstado(Factura.EstadoFactura estado);
}