package com.teleco.operaciones.repository.facturacion;

import com.teleco.operaciones.model.facturacion.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContratoRepository extends JpaRepository<Contrato, UUID> {

    boolean existsByNumero(String numero);

    boolean existsByNumeroAndIdNot(String numero, UUID id);

    List<Contrato> findByClienteId(UUID clienteId);
}