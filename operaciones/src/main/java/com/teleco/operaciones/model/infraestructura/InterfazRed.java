package com.teleco.operaciones.model.infraestructura;

import com.teleco.operaciones.model.shared.BaseEntity;
import jakarta.persistence.*;

@Entity
public class InterfazRed extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "infraestructura_id", nullable = false)
    private Infraestructura infraestructura;

    @Column(nullable = false)
    private String nombre; // FastEthernet0/1

    private String tipo; // ACCESS, TRUNK, ROUTED
    private String direccionIp;
    private String mascara;
    private String detalleVlans;
    private boolean activa;
}