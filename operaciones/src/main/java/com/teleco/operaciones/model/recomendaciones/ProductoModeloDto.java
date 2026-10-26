package com.teleco.operaciones.model.recomendaciones;

public record ProductoModeloDto(
        String id,
        String nombre,
        String categoria,
        String tipo,
        double precio,
        int puntuacionCalidad,
        Integer stock,
        boolean activo
) {
}