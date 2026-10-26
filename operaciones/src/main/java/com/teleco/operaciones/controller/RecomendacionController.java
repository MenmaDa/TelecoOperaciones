package com.teleco.operaciones.controller;

import com.teleco.operaciones.model.recomendaciones.RecomendacionResponse;
import com.teleco.operaciones.service.recomendaciones.RecomendacionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recomendaciones")
public class RecomendacionController {

    private final RecomendacionService recomendacionService;

    public RecomendacionController(
            RecomendacionService recomendacionService
    ) {
        this.recomendacionService = recomendacionService;
    }

    @GetMapping("/clientes/{clienteId}")
    public List<RecomendacionResponse> recomendar(
            @PathVariable UUID clienteId,
            @RequestParam(required = false) BigDecimal presupuesto,
            @RequestParam(defaultValue = "5") Integer limite
    ) {
        return recomendacionService.recomendar(
                clienteId,
                presupuesto,
                limite
        );
    }
}