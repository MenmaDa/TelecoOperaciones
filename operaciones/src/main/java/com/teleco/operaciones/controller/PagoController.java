package com.teleco.operaciones.controller;

import com.teleco.operaciones.model.facturacion.PagoRequest;
import com.teleco.operaciones.model.facturacion.Pago;
import com.teleco.operaciones.service.facturacion.PagoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "http://localhost:3000")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    public List<Pago> listar() {
        return pagoService.listar();
    }

    @GetMapping("/factura/{facturaId}")
    public List<Pago> listarPorFactura(
            @PathVariable UUID facturaId
    ) {
        return pagoService.listarPorFactura(facturaId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pago crear(@RequestBody PagoRequest request) {
        return pagoService.crear(request);
    }

    @PutMapping("/{id}")
    public Pago actualizar(
            @PathVariable UUID id,
            @RequestBody PagoRequest request
    ) {
        return pagoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        pagoService.eliminar(id);
    }
}