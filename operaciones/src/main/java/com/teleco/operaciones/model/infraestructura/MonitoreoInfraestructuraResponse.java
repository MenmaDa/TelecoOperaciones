package com.teleco.operaciones.model.infraestructura;

import java.time.LocalDateTime;
import java.util.UUID;

public record MonitoreoInfraestructuraResponse(
        UUID id,
        UUID infraestructuraId,
        LocalDateTime fechaHora,
        boolean disponible,
        Integer latenciaMs,
        String tipoVerificacion,
        String mensaje
) {
}