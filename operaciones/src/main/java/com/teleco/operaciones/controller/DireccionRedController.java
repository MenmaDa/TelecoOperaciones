package com.teleco.operaciones.controller;

import com.teleco.operaciones.model.infraestructura.DireccionRedRequest;
import com.teleco.operaciones.model.infraestructura.DireccionRedResponse;
import com.teleco.operaciones.service.infraestructura.DireccionRedService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(
        "/api/infraestructura/{infraestructuraId}/direcciones-red"
)
public class DireccionRedController {

    private final DireccionRedService direccionRedService;

    public DireccionRedController(
            DireccionRedService direccionRedService
    ) {
        this.direccionRedService = direccionRedService;
    }

    @GetMapping
    public List<DireccionRedResponse> listar(
            @PathVariable UUID infraestructuraId
    ) {
        return direccionRedService.listarPorInfraestructura(
                infraestructuraId
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DireccionRedResponse crear(
            @PathVariable UUID infraestructuraId,
            @RequestBody DireccionRedRequest request
    ) {
        return direccionRedService.crear(infraestructuraId, request);
    }

    @PutMapping("/{direccionId}")
    public DireccionRedResponse actualizar(
            @PathVariable UUID infraestructuraId,
            @PathVariable UUID direccionId,
            @RequestBody DireccionRedRequest request
    ) {
        return direccionRedService.actualizar(
                infraestructuraId,
                direccionId,
                request
        );
    }

    @DeleteMapping("/{direccionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(
            @PathVariable UUID infraestructuraId,
            @PathVariable UUID direccionId
    ) {
        direccionRedService.eliminar(infraestructuraId, direccionId);
    }
}