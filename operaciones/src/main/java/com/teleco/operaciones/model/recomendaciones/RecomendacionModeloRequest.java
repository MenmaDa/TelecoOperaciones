package com.teleco.operaciones.model.recomendaciones;

import java.math.BigDecimal;
import java.util.List;

public record RecomendacionModeloRequest(
        String clienteId,
        List<ProductoModeloDto> catalogo,
        List<VentaModeloDto> ventas,
        BigDecimal presupuesto,
        Integer limite
) {
}