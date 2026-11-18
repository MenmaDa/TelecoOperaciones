package com.teleco.operaciones.model.infraestructura;

import com.teleco.operaciones.model.shared.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(
        name = "direcciones_red",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_direccion_red_infra_ip",
                        columnNames = {"infraestructura_id", "direccion_ip"}
                )
        }
)
public class DireccionRed extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "infraestructura_id", nullable = false)
    private Infraestructura infraestructura;

    @Column(name = "direccion_ip", length = 45)
    private String direccionIp;

    @Column(length = 45)
    private String mascara;

    @Column(length = 45)
    private String gateway;

    @Column(length = 255)
    private String dns;

    @Column(name = "direccion_mac", length = 30)
    private String direccionMac;

    @Column(name = "servidor_dhcp", length = 100)
    private String servidorDhcp;

    public Infraestructura getInfraestructura() {
        return infraestructura;
    }

    public void setInfraestructura(Infraestructura infraestructura) {
        this.infraestructura = infraestructura;
    }

    public String getDireccionIp() {
        return direccionIp;
    }

    public void setDireccionIp(String direccionIp) {
        this.direccionIp = direccionIp;
    }

    public String getMascara() {
        return mascara;
    }

    public void setMascara(String mascara) {
        this.mascara = mascara;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getDns() {
        return dns;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }

    public String getDireccionMac() {
        return direccionMac;
    }

    public void setDireccionMac(String direccionMac) {
        this.direccionMac = direccionMac;
    }

    public String getServidorDhcp() {
        return servidorDhcp;
    }

    public void setServidorDhcp(String servidorDhcp) {
        this.servidorDhcp = servidorDhcp;
    }
}