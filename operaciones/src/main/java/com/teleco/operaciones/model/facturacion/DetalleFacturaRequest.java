package com.teleco.operaciones.model.facturacion;

import java.util.UUID;

public class DetalleFacturaRequest {

    private UUID productoServicioId;
    private Integer cantidad;
    private String descripcion;

    public UUID getProductoServicioId() {
        return productoServicioId;
    }

    public void setProductoServicioId(UUID productoServicioId) {
        this.productoServicioId = productoServicioId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}