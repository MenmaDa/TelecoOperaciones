package com.teleco.operaciones.controller;

import com.teleco.operaciones.model.clientes.ContactoClienteRequest;
import com.teleco.operaciones.model.clientes.ContactoCliente;
import com.teleco.operaciones.service.clientes.ContactoClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contactos-clientes")
@CrossOrigin(origins = "http://localhost:3000")
public class ContactoClienteController {

    private final ContactoClienteService contactoService;

    public ContactoClienteController(
            ContactoClienteService contactoService
    ) {
        this.contactoService = contactoService;
    }

    @GetMapping
    public List<ContactoCliente> listar() {
        return contactoService.listar();
    }

    @GetMapping("/{id}")
    public ContactoCliente buscarPorId(@PathVariable UUID id) {
        return contactoService.buscarPorId(id);
    }

    @GetMapping("/cliente/{clienteId}")
    public List<ContactoCliente> listarPorCliente(
            @PathVariable UUID clienteId
    ) {
        return contactoService.listarPorCliente(clienteId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContactoCliente crear(
            @RequestBody ContactoClienteRequest request
    ) {
        return contactoService.crear(request);
    }

    @PutMapping("/{id}")
    public ContactoCliente actualizar(
            @PathVariable UUID id,
            @RequestBody ContactoClienteRequest request
    ) {
        return contactoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        contactoService.eliminar(id);
    }
}