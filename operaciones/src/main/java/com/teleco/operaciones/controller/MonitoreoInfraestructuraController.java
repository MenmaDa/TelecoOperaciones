package com.teleco.operaciones.controller;

import com.teleco.operaciones.model.infraestructura.MonitoreoInfraestructuraResponse;
import com.teleco.operaciones.model.infraestructura.ResumenMonitoreoInfraestructuraResponse;
import com.teleco.operaciones.service.infraestructura.MonitoreoInfraestructuraService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/infraestructura/{infraestructuraId}/monitoreo")
@CrossOrigin(origins = "http://localhost:3000")
public class MonitoreoInfraestructuraController {

    private final MonitoreoInfraestructuraService monitoreoService;

    public MonitoreoInfraestructuraController(
            MonitoreoInfraestructuraService monitoreoService
    ) {
        this.monitoreoService = monitoreoService;
    }

    @PostMapping("/verificar")
    public MonitoreoInfraestructuraResponse verificarAhora(
            @PathVariable UUID infraestructuraId
    ) {
        return monitoreoService.verificarAhora(infraestructuraId);
    }

    @GetMapping("/resumen")
    public ResumenMonitoreoInfraestructuraResponse obtenerResumen(
            @PathVariable UUID infraestructuraId
    ) {
        return monitoreoService.obtenerResumen(infraestructuraId);
    }
}