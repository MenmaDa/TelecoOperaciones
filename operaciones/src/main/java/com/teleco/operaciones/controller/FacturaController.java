package com.teleco.operaciones.controller;

import com.teleco.operaciones.model.facturacion.FacturaRequest;
import com.teleco.operaciones.model.facturacion.Factura;
import com.teleco.operaciones.service.facturacion.FacturaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/facturas")
@CrossOrigin(origins = "http://localhost:3000")
public class FacturaController {

    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @GetMapping
    public List<Factura> listar() {
        return facturaService.listar();
    }

    @GetMapping("/{id}")
    public Factura buscarPorId(@PathVariable UUID id) {
        return facturaService.buscarPorId(id);
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Factura> listarPorCliente(
            @PathVariable UUID clienteId
    ) {
        return facturaService.listarPorCliente(clienteId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Factura crear(@RequestBody FacturaRequest request) {
        return facturaService.crear(request);
    }

    @PutMapping("/{id}")
    public Factura actualizar(
            @PathVariable UUID id,
            @RequestBody FacturaRequest request
    ) {
        return facturaService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        facturaService.eliminar(id);
    }
}