package com.teleco.operaciones.controller;

import com.teleco.operaciones.model.infraestructura.ResultadoImportacionInfraestructura;
import com.teleco.operaciones.service.infraestructura.ImportacionInfraestructuraService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/infraestructura/importacion")
public class ImportacionInfraestructuraController {

    private final ImportacionInfraestructuraService importacionService;

    public ImportacionInfraestructuraController(
            ImportacionInfraestructuraService importacionService
    ) {
        this.importacionService = importacionService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultadoImportacionInfraestructura importar(
            @RequestParam("archivo") MultipartFile archivo
    ) {
        return importacionService.importar(archivo);
    }
}