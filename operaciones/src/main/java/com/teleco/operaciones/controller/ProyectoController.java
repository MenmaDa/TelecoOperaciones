package com.teleco.operaciones.controller;

import com.teleco.operaciones.model.proyectos.ProyectoRequest;
import com.teleco.operaciones.model.proyectos.Proyecto;
import com.teleco.operaciones.service.proyectos.ProyectoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/proyectos")
@CrossOrigin(origins = "http://localhost:3000")
public class ProyectoController {

    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    @GetMapping
    public List<Proyecto> listar() {
        return proyectoService.listar();
    }

    @GetMapping("/{id}")
    public Proyecto buscarPorId(@PathVariable UUID id) {
        return proyectoService.buscarPorId(id);
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Proyecto> listarPorCliente(@PathVariable UUID clienteId) {
        return proyectoService.listarPorCliente(clienteId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Proyecto crear(@RequestBody ProyectoRequest request) {
        return proyectoService.crear(request);
    }

    @PutMapping("/{id}")
    public Proyecto actualizar(
            @PathVariable UUID id,
            @RequestBody ProyectoRequest request
    ) {
        return proyectoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        proyectoService.eliminar(id);
    }
}