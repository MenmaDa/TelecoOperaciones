package com.teleco.operaciones.model.reportes;

import java.math.BigDecimal;

public record ResumenFinancieroResponse(
        BigDecimal facturado,
        BigDecimal recibido,
        BigDecimal carteraPendiente,
        long facturasEmitidas,
        long facturasConSaldo
) {
}