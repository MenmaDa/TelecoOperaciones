package com.teleco.operaciones.model.infraestructura;

import java.time.LocalDate;
import java.util.UUID;

public record InfraestructuraRequest(
        String nombre,
        Infraestructura.TipoInfraestructura tipo,
        String proveedor,
        String modelo,
        String numeroSerie,
        String direccionIp,
        String capacidad,
        UUID sedeId,
        UUID responsableId,
        Infraestructura.EstadoInfraestructura estado,
        LocalDate fechaAdquisicion,
        LocalDate fechaVencimiento,
        String descripcion
) {
}