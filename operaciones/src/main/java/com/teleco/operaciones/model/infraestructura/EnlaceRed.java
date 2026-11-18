package com.teleco.operaciones.model.infraestructura;

import com.teleco.operaciones.model.shared.BaseEntity;
import jakarta.persistence.*;

@Entity
public class EnlaceRed extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origen_id", nullable = false)
    private Infraestructura origen;

    private String puertoOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destino_id", nullable = false)
    private Infraestructura destino;

    private String puertoDestino;
    private String tipoEnlace;
    private String tipoCable;
    private boolean funcional;
    private String criticidad;
}
