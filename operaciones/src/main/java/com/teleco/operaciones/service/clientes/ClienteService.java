package com.teleco.operaciones.service.clientes;

import com.teleco.operaciones.model.clientes.Cliente;
import com.teleco.operaciones.repository.clientes.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepo;

    public ClienteService(ClienteRepository clienteRepo) {
        this.clienteRepo = clienteRepo;
    }

    public List<Cliente> listar() {
        return clienteRepo.findAll();
    }

    public Cliente buscarPorId(UUID id) {
        return clienteRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Client not found"
                ));
    }

    public Cliente crear(Cliente cliente) {
        if (cliente.getNit() != null &&
                clienteRepo.existsByNit(cliente.getNit())) {

            throw new RuntimeException(
                    "A client with this NIT already exists"
            );
        }

        return clienteRepo.save(cliente);
    }

    public Cliente actualizar(UUID id, Cliente cliente) {
        Cliente clienteExistente = buscarPorId(id);

        boolean nitCambio = !Objects.equals(
                clienteExistente.getNit(),
                cliente.getNit()
        );

        if (nitCambio &&
                cliente.getNit() != null &&
                clienteRepo.existsByNit(cliente.getNit())) {

            throw new RuntimeException(
                    "A client with this NIT already exists"
            );
        }

        clienteExistente.setRazonSocial(cliente.getRazonSocial());
        clienteExistente.setNit(cliente.getNit());
        clienteExistente.setEmail(cliente.getEmail());
        clienteExistente.setTelefono(cliente.getTelefono());
        clienteExistente.setDireccion(cliente.getDireccion());
        clienteExistente.setActivo(cliente.isActivo());

        return clienteRepo.save(clienteExistente);
    }

    public void eliminar(UUID id) {
        if (!clienteRepo.existsById(id)) {
            throw new RuntimeException("Client not found");
        }

        clienteRepo.deleteById(id);
    }
}