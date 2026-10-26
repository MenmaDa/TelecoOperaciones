package com.teleco.operaciones.controller;

import com.teleco.operaciones.model.infraestructura.InfraestructuraRequest;
import com.teleco.operaciones.model.infraestructura.InfraestructuraResponse;
import com.teleco.operaciones.service.infraestructura.InfraestructuraService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/infraestructura")
@CrossOrigin(origins = "http://localhost:3000")
public class InfraestructuraController {

    private final InfraestructuraService infraestructuraService;

    public InfraestructuraController(
            InfraestructuraService infraestructuraService
    ) {
        this.infraestructuraService = infraestructuraService;
    }

    @GetMapping
    public List<InfraestructuraResponse> listar() {
        return infraestructuraService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InfraestructuraResponse crear(
            @RequestBody InfraestructuraRequest request
    ) {
        return infraestructuraService.crear(request);
    }

    @PutMapping("/{id}")
    public InfraestructuraResponse actualizar(
            @PathVariable UUID id,
            @RequestBody InfraestructuraRequest request
    ) {
        return infraestructuraService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        infraestructuraService.eliminar(id);
    }
}