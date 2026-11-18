package com.teleco.operaciones.model.infraestructura;

import java.time.LocalDate;
import java.util.UUID;

public record InfraestructuraResponse(
        UUID id,
        String nombre,
        String tipo,
        String proveedor,
        String modelo,
        String numeroSerie,
        String direccionIp,
        Integer puertoMonitoreo,
        boolean monitoreoHabilitado,
        String capacidad,
        UUID sedeId,
        String sedeNombre,
        UUID responsableId,
        String responsableNombre,
        String estado,
        LocalDate fechaAdquisicion,
        LocalDate fechaVencimiento,
        String descripcion,
        boolean activo
) {
}