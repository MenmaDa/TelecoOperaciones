package com.teleco.operaciones.model.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FacturaDashboardResponse(
        String numero,
        String cliente,
        BigDecimal total,
        String estado,
        LocalDate fechaEmision
) {
}