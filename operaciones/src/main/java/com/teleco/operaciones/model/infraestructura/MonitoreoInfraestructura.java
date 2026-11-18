package com.teleco.operaciones.model.infraestructura;

import com.teleco.operaciones.model.shared.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "monitoreos_infraestructura")
public class MonitoreoInfraestructura extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "infraestructura_id", nullable = false)
    private Infraestructura infraestructura;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false)
    private boolean disponible;

    private Integer latenciaMs;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoVerificacion tipoVerificacion;

    @Column(length = 1000)
    private String mensaje;

    public Infraestructura getInfraestructura() {
        return infraestructura;
    }

    public void setInfraestructura(Infraestructura infraestructura) {
        this.infraestructura = infraestructura;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public Integer getLatenciaMs() {
        return latenciaMs;
    }

    public void setLatenciaMs(Integer latenciaMs) {
        this.latenciaMs = latenciaMs;
    }

    public TipoVerificacion getTipoVerificacion() {
        return tipoVerificacion;
    }

    public void setTipoVerificacion(
            TipoVerificacion tipoVerificacion
    ) {
        this.tipoVerificacion = tipoVerificacion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public enum TipoVerificacion {
        IP,
        TCP
    }
}