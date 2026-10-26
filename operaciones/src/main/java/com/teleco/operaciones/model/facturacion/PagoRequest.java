package com.teleco.operaciones.model.facturacion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class PagoRequest {

    private UUID facturaId;
    private LocalDate fechaPago;
    private BigDecimal monto;
    private String medioPago;
    private String referencia;

    public UUID getFacturaId() {
        return facturaId;
    }

    public void setFacturaId(UUID facturaId) {
        this.facturaId = facturaId;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}