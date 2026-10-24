package com.teleco.operaciones.controller;

import com.teleco.operaciones.model.empleados.Sede;
import com.teleco.operaciones.service.empleados.SedeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sedes")
@CrossOrigin(origins = "http://localhost:3000")
public class SedeController {

    private final SedeService sedeService;

    public SedeController(SedeService sedeService) {
        this.sedeService = sedeService;
    }

    @GetMapping
    public List<Sede> listar() {
        return sedeService.listar();
    }

    @GetMapping("/{id}")
    public Sede buscarPorId(@PathVariable UUID id) {
        return sedeService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Sede crear(@RequestBody Sede sede) {
        return sedeService.crear(sede);
    }

    @PutMapping("/{id}")
    public Sede actualizar(
            @PathVariable UUID id,
            @RequestBody Sede sede
    ) {
        return sedeService.actualizar(id, sede);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        sedeService.eliminar(id);
    }
}