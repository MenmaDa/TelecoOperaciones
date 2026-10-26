package com.teleco.operaciones.model.shared;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditoriaResponse(
        UUID id,
        String accion,
        String entidad,
        String entidadId,
        String detalle,
        LocalDateTime fecha,
        String usuarioEmail
) {
}