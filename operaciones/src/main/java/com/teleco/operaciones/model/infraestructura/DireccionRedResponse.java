package com.teleco.operaciones.model.infraestructura;

import java.util.UUID;

public record DireccionRedResponse(
        UUID id,
        UUID infraestructuraId,
        String infraestructuraNombre,
        String direccionIp,
        String mascara,
        String gateway,
        String dns,
        String direccionMac,
        String servidorDhcp,
        boolean activo
) {
}