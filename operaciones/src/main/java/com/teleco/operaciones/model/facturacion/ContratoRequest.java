package com.teleco.operaciones.model.facturacion;

import com.teleco.operaciones.model.facturacion.Contrato.EstadoContrato;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class ContratoRequest {

    private String numero;
    private UUID clienteId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal valor;
    private EstadoContrato estado;

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public void setClienteId(UUID clienteId) {
        this.clienteId = clienteId;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public EstadoContrato getEstado() {
        return estado;
    }

    public void setEstado(EstadoContrato estado) {
        this.estado = estado;
    }
}