package com.teleco.operaciones.model.infraestructura;

import com.teleco.operaciones.model.shared.BaseEntity;
import jakarta.persistence.*;

@Entity
public class VlanRed extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String nombre; // VLAN15_CCTV

    private Integer numeroVlan;
    private String red;
    private String mascara;
    private String gateway;
    private String dns;
}
