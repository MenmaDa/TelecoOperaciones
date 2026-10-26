package com.teleco.operaciones.model.reportes;

import java.math.BigDecimal;
import java.util.UUID;

public record RentabilidadProyectoResponse(
        UUID proyectoId,
        String proyecto,
        String cliente,
        BigDecimal presupuesto,
        BigDecimal facturado,
        BigDecimal costoManoObra,
        BigDecimal margen,
        Integer horasReales
) {
}