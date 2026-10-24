package com.teleco.operaciones.repository.clientes;

import com.teleco.operaciones.model.clientes.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    Optional<Cliente> findByNit(String nit);

    boolean existsByNit(String nit);
}