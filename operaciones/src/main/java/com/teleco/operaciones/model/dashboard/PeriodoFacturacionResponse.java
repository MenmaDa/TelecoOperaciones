package com.teleco.operaciones.model.dashboard;

import java.math.BigDecimal;

public record PeriodoFacturacionResponse(
        String etiqueta,
        BigDecimal total
) {
}