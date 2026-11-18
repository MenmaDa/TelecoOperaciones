package com.teleco.operaciones.model.infraestructura;

import java.util.List;
import java.util.UUID;

public record ResumenMonitoreoInfraestructuraResponse(
        UUID infraestructuraId,
        String infraestructuraNombre,
        String direccionIp,
        Integer puertoMonitoreo,
        MonitoreoInfraestructuraResponse ultimaRevision,
        long totalRevisiones,
        double disponibilidadPorcentaje,
        Double latenciaPromedioMs,
        Integer latenciaMinimaMs,
        Integer latenciaMaximaMs,
        List<MonitoreoInfraestructuraResponse> historial
) {
}