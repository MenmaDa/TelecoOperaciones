package com.teleco.operaciones.controller;

import com.teleco.operaciones.model.clientes.Cliente;
import com.teleco.operaciones.service.clientes.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<Cliente> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    public Cliente buscarPorId(@PathVariable UUID id) {
        return clienteService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente crear(@RequestBody Cliente cliente) {
        return clienteService.crear(cliente);
    }

    @PutMapping("/{id}")
    public Cliente actualizar(
            @PathVariable UUID id,
            @RequestBody Cliente cliente
    ) {
        return clienteService.actualizar(id, cliente);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        clienteService.eliminar(id);
    }
}