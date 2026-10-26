package com.teleco.operaciones.model.proyectos;

import java.time.LocalDate;
import java.util.UUID;

public record AsignacionRecursoResponse(
        UUID id,
        UUID proyectoId,
        String proyectoNombre,
        UUID empleadoId,
        String empleadoNombre,
        String empleadoCargo,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Integer porcentajeDedicacion
) {
}