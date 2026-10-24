package com.teleco.operaciones.repository.clientes;

import com.teleco.operaciones.model.clientes.ContactoCliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContactoClienteRepository
        extends JpaRepository<ContactoCliente, UUID> {

    List<ContactoCliente> findByClienteId(UUID clienteId);
}