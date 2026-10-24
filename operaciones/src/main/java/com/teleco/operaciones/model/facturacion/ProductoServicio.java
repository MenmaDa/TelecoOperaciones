package com.teleco.operaciones.model.facturacion;

import com.teleco.operaciones.model.shared.BaseEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "producto_servicio")
public class ProductoServicio extends BaseEntity {

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoProductoServicio tipo;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer puntuacionCalidad;

    private Integer stock;

    @Column(length = 2000)
    private String descripcion;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public TipoProductoServicio getTipo() {
        return tipo;
    }

    public void setTipo(TipoProductoServicio tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getPuntuacionCalidad() {
        return puntuacionCalidad;
    }

    public void setPuntuacionCalidad(Integer puntuacionCalidad) {
        this.puntuacionCalidad = puntuacionCalidad;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public enum TipoProductoServicio {
        PRODUCTO,
        SERVICIO
    }

}
