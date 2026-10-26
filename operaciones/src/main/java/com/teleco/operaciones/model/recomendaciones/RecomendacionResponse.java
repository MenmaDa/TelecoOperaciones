package com.teleco.operaciones.model.recomendaciones;

import java.math.BigDecimal;

public record RecomendacionResponse(
        String productoServicioId,
        String nombre,
        String categoria,
        String tipo,
        BigDecimal precio,
        Integer puntuacionCalidad,
        Double confianza,
        Double puntaje,
        String motivo
) {
}