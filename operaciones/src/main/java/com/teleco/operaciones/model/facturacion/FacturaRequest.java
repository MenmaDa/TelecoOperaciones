package com.teleco.operaciones.model.facturacion;

import com.teleco.operaciones.model.facturacion.Factura.EstadoFactura;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FacturaRequest {

    private String numero;
    private UUID clienteId;
    private UUID proyectoId;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private EstadoFactura estado;
    private List<DetalleFacturaRequest> detalles = new ArrayList<>();

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

    public UUID getProyectoId() {
        return proyectoId;
    }

    public void setProyectoId(UUID proyectoId) {
        this.proyectoId = proyectoId;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public EstadoFactura getEstado() {
        return estado;
    }

    public void setEstado(EstadoFactura estado) {
        this.estado = estado;
    }

    public List<DetalleFacturaRequest> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleFacturaRequest> detalles) {
        this.detalles = detalles;
    }
}