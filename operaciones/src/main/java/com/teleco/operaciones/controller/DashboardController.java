package com.teleco.operaciones.controller;

import com.teleco.operaciones.model.dashboard.DashboardResumenResponse;
import com.teleco.operaciones.service.dashboard.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/resumen")
    public DashboardResumenResponse obtenerResumen() {
        return dashboardService.obtenerResumen();
    }
}