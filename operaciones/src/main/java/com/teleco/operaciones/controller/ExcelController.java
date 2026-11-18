package com.teleco.operaciones.controller;

import com.teleco.operaciones.model.shared.ExcelResponse;
import com.teleco.operaciones.service.shared.ExcelService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    private final ExcelService excelService;

    public ExcelController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @PostMapping(
            value = "/leer",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ExcelResponse leerExcel(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam(required = false) String hoja
    ) {
        return excelService.leerExcel(archivo, hoja);
    }
}