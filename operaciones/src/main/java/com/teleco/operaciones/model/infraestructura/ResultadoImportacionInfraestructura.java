package com.teleco.operaciones.model.infraestructura;

import java.util.List;

public record ResultadoImportacionInfraestructura(
        int activosCreados,
        int activosActualizados,
        int direccionesCreadas,
        int filasOmitidas,
        List<String> advertencias
) {
}