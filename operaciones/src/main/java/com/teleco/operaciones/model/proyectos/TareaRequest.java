package com.teleco.operaciones.model.proyectos;

import com.teleco.operaciones.model.proyectos.Tarea.EstadoTarea;

import java.time.LocalDate;
import java.util.UUID;

public class TareaRequest {

    private String titulo;
    private String descripcion;
    private UUID proyectoId;
    private UUID responsableId;
    private LocalDate fechaLimite;
    private Integer horasEstimadas;
    private Integer horasReales;
    private EstadoTarea estado;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public UUID getProyectoId() {
        return proyectoId;
    }

    public void setProyectoId(UUID proyectoId) {
        this.proyectoId = proyectoId;
    }

    public UUID getResponsableId() {
        return responsableId;
    }

    public void setResponsableId(UUID responsableId) {
        this.responsableId = responsableId;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public Integer getHorasEstimadas() {
        return horasEstimadas;
    }

    public void setHorasEstimadas(Integer horasEstimadas) {
        this.horasEstimadas = horasEstimadas;
    }

    public Integer getHorasReales() {
        return horasReales;
    }

    public void setHorasReales(Integer horasReales) {
        this.horasReales = horasReales;
    }

    public EstadoTarea getEstado() {
        return estado;
    }

    public void setEstado(EstadoTarea estado) {
        this.estado = estado;
    }
}