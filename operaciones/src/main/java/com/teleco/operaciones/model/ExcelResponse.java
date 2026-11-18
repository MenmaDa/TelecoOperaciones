package com.teleco.operaciones.model;

import java.util.List;
import java.util.Map;

public record ExcelResponse(
    List<String> columnas,
    List<Map<String, Object>> registros,
    int totalRegistros
) {}