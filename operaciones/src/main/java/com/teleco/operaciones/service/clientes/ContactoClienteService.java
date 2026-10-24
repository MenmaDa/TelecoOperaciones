package com.teleco.operaciones.service.clientes;

import com.teleco.operaciones.model.clientes.ContactoClienteRequest;
import com.teleco.operaciones.model.clientes.Cliente;
import com.teleco.operaciones.model.clientes.ContactoCliente;
import com.teleco.operaciones.repository.clientes.ClienteRepository;
import com.teleco.operaciones.repository.clientes.ContactoClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ContactoClienteService {

    private final ContactoClienteRepository contactoRepo;
    private final ClienteRepository clienteRepo;

    public ContactoClienteService(
            ContactoClienteRepository contactoRepo,
            ClienteRepository clienteRepo
    ) {
        this.contactoRepo = contactoRepo;
        this.clienteRepo = clienteRepo;
    }

    public List<ContactoCliente> listar() {
        return contactoRepo.findAll();
    }

    public ContactoCliente buscarPorId(UUID id) {
        return contactoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Contacto no encontrado"
                ));
    }

    public List<ContactoCliente> listarPorCliente(UUID clienteId) {
        return contactoRepo.findByClienteId(clienteId);
    }

    public ContactoCliente crear(ContactoClienteRequest request) {
        ContactoCliente contacto = new ContactoCliente();
        asignarDatos(contacto, request);

        return contactoRepo.save(contacto);
    }

    public ContactoCliente actualizar(
            UUID id,
            ContactoClienteRequest request
    ) {
        ContactoCliente contacto = buscarPorId(id);
        asignarDatos(contacto, request);

        return contactoRepo.save(contacto);
    }

    public void eliminar(UUID id) {
        contactoRepo.delete(buscarPorId(id));
    }

    private void asignarDatos(
            ContactoCliente contacto,
            ContactoClienteRequest request
    ) {
        Cliente cliente = clienteRepo.findById(request.getClienteId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente no encontrado"
                ));

        contacto.setNombres(request.getNombres());
        contacto.setApellidos(request.getApellidos());
        contacto.setCargo(request.getCargo());
        contacto.setEmail(request.getEmail());
        contacto.setTelefono(request.getTelefono());
        contacto.setCliente(cliente);
    }
}