package com.teleco.operaciones.model.infraestructura;

import com.teleco.operaciones.model.empleados.Empleado;
import com.teleco.operaciones.model.empleados.Sede;
import com.teleco.operaciones.model.shared.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "infraestructuras")
public class Infraestructura extends BaseEntity {

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoInfraestructura tipo;

    private String proveedor;
    private String modelo;
    private String numeroSerie;
    private String direccionIp;
    private String capacidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_id")
    private Sede sede;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    private Empleado responsable;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoInfraestructura estado = EstadoInfraestructura.OPERATIVO;

    private LocalDate fechaAdquisicion;
    private LocalDate fechaVencimiento;

    @Column(length = 2000)
    private String descripcion;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoInfraestructura getTipo() {
        return tipo;
    }

    public void setTipo(TipoInfraestructura tipo) {
        this.tipo = tipo;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public String getDireccionIp() {
        return direccionIp;
    }

    public void setDireccionIp(String direccionIp) {
        this.direccionIp = direccionIp;
    }

    public String getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(String capacidad) {
        this.capacidad = capacidad;
    }

    public Sede getSede() {
        return sede;
    }

    public void setSede(Sede sede) {
        this.sede = sede;
    }

    public Empleado getResponsable() {
        return responsable;
    }

    public void setResponsable(Empleado responsable) {
        this.responsable = responsable;
    }

    public EstadoInfraestructura getEstado() {
        return estado;
    }

    public void setEstado(EstadoInfraestructura estado) {
        this.estado = estado;
    }

    public LocalDate getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    public void setFechaAdquisicion(LocalDate fechaAdquisicion) {
        this.fechaAdquisicion = fechaAdquisicion;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public enum TipoInfraestructura {
        SERVIDOR,
        EQUIPO_RED,
        DISPOSITIVO_SEGURIDAD,
        SERVICIO_CLOUD,
        HOSTING_DOMINIO,
        BACKUP,
        OTRO
    }

    public enum EstadoInfraestructura {
        OPERATIVO,
        MANTENIMIENTO,
        INCIDENTE,
        INACTIVO
    }
}