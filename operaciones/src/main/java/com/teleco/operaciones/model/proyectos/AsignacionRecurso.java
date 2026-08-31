package com.teleco.operaciones.model.proyectos;
import com.teleco.operaciones.model.empleados.Empleado;
import com.teleco.operaciones.model.shared.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

// AsignacionRecurso.java
@Entity
public class AsignacionRecurso extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    @Column(nullable = false)
    private Integer porcentajeDedicacion; // Ej.: 50 = medio tiempo

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
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

    public Integer getPorcentajeDedicacion() {
        return porcentajeDedicacion;
    }

    public void setPorcentajeDedicacion(Integer porcentajeDedicacion) {
        this.porcentajeDedicacion = porcentajeDedicacion;
    }
}
