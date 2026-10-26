package com.teleco.operaciones.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/archivos")
@CrossOrigin(origins = "http://localhost:3000")
public class ArchivoController {

    @Value("${app.upload.perfiles-dir:uploads/perfiles}")
    private String perfilesDir;

    @PostMapping(
            value = "/perfiles",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Map<String, String> subirFotoPerfil(
            @RequestParam("archivo") MultipartFile archivo
    ) {
        if (archivo.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debes seleccionar una imagen"
            );
        }

        String tipo = archivo.getContentType();

        if (tipo == null || !tipo.startsWith("image/")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El archivo debe ser una imagen"
            );
        }

        String extension = obtenerExtension(archivo.getOriginalFilename());
        String nombreArchivo = UUID.randomUUID() + extension;

        try {
            Path carpeta = Path.of(perfilesDir).toAbsolutePath();
            Files.createDirectories(carpeta);

            Files.copy(
                    archivo.getInputStream(),
                    carpeta.resolve(nombreArchivo),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException error) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No fue posible guardar la imagen"
            );
        }

        String url = "http://localhost:8080/uploads/perfiles/" + nombreArchivo;

        return Map.of("url", url);
    }

    private String obtenerExtension(String nombreOriginal) {
        if (nombreOriginal == null || !nombreOriginal.contains(".")) {
            return ".jpg";
        }

        return nombreOriginal.substring(
                nombreOriginal.lastIndexOf(".")
        ).toLowerCase();
    }
}