package com.teleco.operaciones.model.proyectos;

import com.teleco.operaciones.model.proyectos.Tarea.EstadoTarea;

public class TareaProgresoRequest {

    private EstadoTarea estado;
    private Integer horasReales;

    public EstadoTarea getEstado() {
        return estado;
    }

    public void setEstado(EstadoTarea estado) {
        this.estado = estado;
    }

    public Integer getHorasReales() {
        return horasReales;
    }

    public void setHorasReales(Integer horasReales) {
        this.horasReales = horasReales;
    }
}