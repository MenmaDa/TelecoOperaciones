package com.teleco.operaciones.controller;

import com.teleco.operaciones.model.facturacion.ContratoRequest;
import com.teleco.operaciones.model.facturacion.Contrato;
import com.teleco.operaciones.service.facturacion.ContratoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contratos")
@CrossOrigin(origins = "http://localhost:3000")
public class ContratoController {

    private final ContratoService contratoService;

    public ContratoController(ContratoService contratoService) {
        this.contratoService = contratoService;
    }

    @GetMapping
    public List<Contrato> listar() {
        return contratoService.listar();
    }

    @GetMapping("/{id}")
    public Contrato buscarPorId(@PathVariable UUID id) {
        return contratoService.buscarPorId(id);
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Contrato> listarPorCliente(@PathVariable UUID clienteId) {
        return contratoService.listarPorCliente(clienteId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Contrato crear(@RequestBody ContratoRequest request) {
        return contratoService.crear(request);
    }

    @PutMapping("/{id}")
    public Contrato actualizar(
            @PathVariable UUID id,
            @RequestBody ContratoRequest request
    ) {
        return contratoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        contratoService.eliminar(id);
    }
}