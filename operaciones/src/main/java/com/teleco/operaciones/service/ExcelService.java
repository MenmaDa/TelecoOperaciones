
package com.teleco.operaciones.service;

import com.teleco.operaciones.model.ExcelResponse;
import org.apache.poi.ss.usermodel.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

@Service
public class ExcelService {

    public ExcelResponse leerExcel(MultipartFile archivo) {

        if (archivo == null || archivo.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El archivo está vacío"
            );
        }

        String nombre = archivo.getOriginalFilename();

        if (nombre == null ||
            !nombre.toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Solo se permiten archivos .xlsx"
            );
        }

        try (Workbook workbook =
                 WorkbookFactory.create(archivo.getInputStream())) {

            if (workbook.getNumberOfSheets() == 0) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El Excel no contiene hojas"
                );
            }

            Sheet hoja = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            Row encabezado = hoja.getRow(0);

            if (encabezado == null || encabezado.getLastCellNum() <= 0) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El Excel no tiene encabezados"
                );
            }

            List<String> columnas = new ArrayList<>();

            for (int i = 0; i < encabezado.getLastCellNum(); i++) {
                Cell celda = encabezado.getCell(i);
                String titulo = formatter.formatCellValue(celda).trim();

                if (titulo.isEmpty() || columnas.contains(titulo)) {
                    throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Los encabezados deben ser únicos y no estar vacíos"
                    );
                }

                columnas.add(titulo);
            }

            List<Map<String, Object>> registros = new ArrayList<>();

            for (int i = 1; i <= hoja.getLastRowNum(); i++) {

                Row fila = hoja.getRow(i);

                if (fila == null) {
                    continue;
                }

                Map<String, Object> registro = new LinkedHashMap<>();
                boolean tieneDatos = false;

                for (int j = 0; j < columnas.size(); j++) {

                    Cell celda = fila.getCell(j);
                    String valor = formatter.formatCellValue(celda).trim();

                    if (!valor.isEmpty()) {
                        tieneDatos = true;
                    }

                    registro.put(columnas.get(j), valor);
                }

                if (tieneDatos) {
                    registros.add(registro);
                }
            }

            return new ExcelResponse(
                columnas,
                registros,
                registros.size()
            );

        } catch (IOException e) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No se pudo leer el archivo Excel",
                e
            );
        } catch (RuntimeException e) {
            if (e instanceof ResponseStatusException) {
                throw e;
            }
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El archivo Excel no tiene un formato válido",
                e
            );
        }
    }
}
