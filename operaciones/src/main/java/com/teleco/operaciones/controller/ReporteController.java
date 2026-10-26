package com.teleco.operaciones.controller;

import com.teleco.operaciones.model.reportes.CarteraResponse;
import com.teleco.operaciones.model.reportes.RentabilidadProyectoResponse;
import com.teleco.operaciones.model.reportes.ResumenFinancieroResponse;
import com.teleco.operaciones.service.facturacion.ReporteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/financiero")
    public ResumenFinancieroResponse obtenerResumenFinanciero() {
        return reporteService.obtenerResumenFinanciero();
    }

    @GetMapping("/cartera")
    public List<CarteraResponse> obtenerCartera() {
        return reporteService.obtenerCartera();
    }

    @GetMapping("/rentabilidad-proyectos")
    public List<RentabilidadProyectoResponse> obtenerRentabilidadProyectos() {
        return reporteService.obtenerRentabilidadProyectos();
    }
}