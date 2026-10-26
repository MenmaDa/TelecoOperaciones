package com.teleco.operaciones.controller;

import com.teleco.operaciones.model.proyectos.AsignacionRecursoRequest;
import com.teleco.operaciones.model.proyectos.AsignacionRecursoResponse;
import com.teleco.operaciones.service.proyectos.AsignacionRecursoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/asignaciones-recursos")
@CrossOrigin(origins = "http://localhost:3000")
public class AsignacionRecursoController {

    private final AsignacionRecursoService asignacionService;

    public AsignacionRecursoController(
            AsignacionRecursoService asignacionService
    ) {
        this.asignacionService = asignacionService;
    }

    @GetMapping
    public List<AsignacionRecursoResponse> listar() {
        return asignacionService.listar();
    }

    @GetMapping("/proyecto/{proyectoId}")
    public List<AsignacionRecursoResponse> listarPorProyecto(
            @PathVariable UUID proyectoId
    ) {
        return asignacionService.listarPorProyecto(proyectoId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AsignacionRecursoResponse crear(
            @RequestBody AsignacionRecursoRequest request
    ) {
        return asignacionService.crear(request);
    }

    @PutMapping("/{id}")
    public AsignacionRecursoResponse actualizar(
            @PathVariable UUID id,
            @RequestBody AsignacionRecursoRequest request
    ) {
        return asignacionService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        asignacionService.eliminar(id);
    }
}