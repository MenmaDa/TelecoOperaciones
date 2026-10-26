package com.teleco.operaciones.model.dashboard;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResumenResponse(
        long clientesActivos,
        long proyectosEnProgreso,
        long tareasPendientes,
        long tareasBloqueadas,
        long tareasVencidas,
        long cantidadProductosStockBajo,
        BigDecimal facturacionTotal,
        BigDecimal pagosRecibidos,
        BigDecimal carteraPendiente,
        List<FacturaDashboardResponse> facturasRecientes,
        List<ProductoStockBajoResponse> productosStockBajo,
        List<PeriodoFacturacionResponse> facturacionPorMes,
        List<PeriodoFacturacionResponse> facturacionPorSemana
) {
}