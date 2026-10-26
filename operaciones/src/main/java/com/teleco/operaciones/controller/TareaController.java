package com.teleco.operaciones.controller;

import com.teleco.operaciones.model.proyectos.TareaRequest;
import com.teleco.operaciones.model.proyectos.Tarea;
import com.teleco.operaciones.service.proyectos.TareaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.teleco.operaciones.model.proyectos.TareaProgresoRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tareas")
@CrossOrigin(origins = "http://localhost:3000")
public class TareaController {

    private final TareaService tareaService;

    public TareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    @GetMapping
    public List<Tarea> listar() {
        return tareaService.listar();
    }

    @GetMapping("/{id}")
    public Tarea buscarPorId(@PathVariable UUID id) {
        return tareaService.buscarPorId(id);
    }

    @GetMapping("/proyecto/{proyectoId}")
    public List<Tarea> listarPorProyecto(@PathVariable UUID proyectoId) {
        return tareaService.listarPorProyecto(proyectoId);
    }

    @GetMapping("/mis-tareas")
    public List<Tarea> listarMisTareas(Authentication authentication) {
        return tareaService.listarMisTareas(authentication.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tarea crear(@RequestBody TareaRequest request) {
        return tareaService.crear(request);
    }

    @PutMapping("/{id}")
    public Tarea actualizar(
            @PathVariable UUID id,
            @RequestBody TareaRequest request
    ) {
        return tareaService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        tareaService.eliminar(id);
    }

    @PatchMapping("/{id}/mi-progreso")
    public Tarea actualizarMiProgreso(
            @PathVariable UUID id,
            @RequestBody TareaProgresoRequest request,
            Authentication authentication
    ) {
        return tareaService.actualizarMiProgreso(
                id,
                request,
                authentication.getName()
        );
    }
}