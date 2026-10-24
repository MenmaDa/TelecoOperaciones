package com.teleco.operaciones.controller;

import com.teleco.operaciones.model.facturacion.ProductoServicio;
import com.teleco.operaciones.service.facturacion.ProductoServicioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/productos-servicios")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductoServicioController {

    private final ProductoServicioService productoServicioService;

    public ProductoServicioController(
            ProductoServicioService productoServicioService
    ) {
        this.productoServicioService = productoServicioService;
    }

    @GetMapping
    public List<ProductoServicio> listar() {
        return productoServicioService.listar();
    }

    @GetMapping("/{id}")
    public ProductoServicio buscarPorId(@PathVariable UUID id) {
        return productoServicioService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoServicio crear(
            @RequestBody ProductoServicio productoServicio
    ) {
        return productoServicioService.crear(productoServicio);
    }

    @PutMapping("/{id}")
    public ProductoServicio actualizar(
            @PathVariable UUID id,
            @RequestBody ProductoServicio productoServicio
    ) {
        return productoServicioService.actualizar(id, productoServicio);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        productoServicioService.eliminar(id);
    }
}