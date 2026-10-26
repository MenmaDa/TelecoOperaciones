package com.teleco.operaciones.model.reportes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CarteraResponse(
        UUID facturaId,
        String numeroFactura,
        String cliente,
        LocalDate fechaEmision,
        LocalDate fechaVencimiento,
        BigDecimal totalFactura,
        BigDecimal pagosRecibidos,
        BigDecimal saldoPendiente,
        boolean vencida
) {
}