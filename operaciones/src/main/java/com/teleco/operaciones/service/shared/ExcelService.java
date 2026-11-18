package com.teleco.operaciones.service.shared;

import com.teleco.operaciones.model.shared.ExcelResponse;
import org.apache.poi.ss.usermodel.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ExcelService {

    public ExcelResponse leerExcel(
            MultipartFile archivo,
            String nombreHoja
    ) {
        if (archivo == null || archivo.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El archivo está vacío."
            );
        }

        String nombreArchivo = archivo.getOriginalFilename();

        if (
                nombreArchivo == null ||
                        !nombreArchivo.toLowerCase(Locale.ROOT).endsWith(".xlsx")
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo se permiten archivos .xlsx."
            );
        }

        try (
                Workbook workbook = WorkbookFactory.create(
                        archivo.getInputStream()
                )
        ) {
            if (workbook.getNumberOfSheets() == 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El Excel no contiene hojas."
                );
            }

            Sheet hoja = obtenerHoja(workbook, nombreHoja);
            DataFormatter formatter = new DataFormatter();

            Row encabezado = hoja.getRow(0);

            if (encabezado == null || encabezado.getLastCellNum() <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La hoja seleccionada no tiene encabezados."
                );
            }

            List<String> columnas = leerColumnas(encabezado, formatter);
            List<Map<String, Object>> registros = new ArrayList<>();

            for (int i = 1; i <= hoja.getLastRowNum(); i++) {
                Row fila = hoja.getRow(i);

                if (fila == null) {
                    continue;
                }

                Map<String, Object> registro = new LinkedHashMap<>();
                boolean tieneDatos = false;

                for (int j = 0; j < columnas.size(); j++) {
                    Cell celda = fila.getCell(
                            j,
                            Row.MissingCellPolicy.RETURN_BLANK_AS_NULL
                    );

                    String valor = celda == null
                            ? ""
                            : formatter.formatCellValue(celda).trim();

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

        } catch (IOException error) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se pudo leer el archivo Excel.",
                    error
            );
        } catch (ResponseStatusException error) {
            throw error;
        } catch (RuntimeException error) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El archivo Excel no tiene un formato válido.",
                    error
            );
        }
    }

    private Sheet obtenerHoja(
            Workbook workbook,
            String nombreHoja
    ) {
        if (nombreHoja == null || nombreHoja.isBlank()) {
            return workbook.getSheetAt(0);
        }

        Sheet hoja = workbook.getSheet(nombreHoja.trim());

        if (hoja == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La hoja '" + nombreHoja + "' no existe en el archivo."
            );
        }

        return hoja;
    }

    private List<String> leerColumnas(
            Row encabezado,
            DataFormatter formatter
    ) {
        List<String> columnas = new ArrayList<>();

        for (int i = 0; i < encabezado.getLastCellNum(); i++) {
            Cell celda = encabezado.getCell(
                    i,
                    Row.MissingCellPolicy.RETURN_BLANK_AS_NULL
            );

            String titulo = celda == null
                    ? ""
                    : formatter.formatCellValue(celda).trim();

            if (titulo.isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Los encabezados no pueden estar vacíos."
                );
            }

            if (columnas.contains(titulo)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Los encabezados deben ser únicos. "
                                + "Se repite: " + titulo
                );
            }

            columnas.add(titulo);
        }

        return columnas;
    }
}