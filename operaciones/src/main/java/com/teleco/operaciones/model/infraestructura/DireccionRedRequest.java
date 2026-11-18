package com.teleco.operaciones.model.infraestructura;

public record DireccionRedRequest(
        String direccionIp,
        String mascara,
        String gateway,
        String dns,
        String direccionMac,
        String servidorDhcp
) {
}