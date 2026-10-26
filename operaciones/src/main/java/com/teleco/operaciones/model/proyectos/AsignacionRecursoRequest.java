package com.teleco.operaciones.model.proyectos;

import java.time.LocalDate;
import java.util.UUID;

public class AsignacionRecursoRequest {

    private UUID proyectoId;
    private UUID empleadoId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer porcentajeDedicacion;

    public UUID getProyectoId() {
        return proyectoId;
    }

    public void setProyectoId(UUID proyectoId) {
        this.proyectoId = proyectoId;
    }

    public UUID getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(UUID empleadoId) {
        this.empleadoId = empleadoId;
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