
package com.teleco.operaciones;

import com.teleco.operaciones.model.ExcelResponse;
import com.teleco.operaciones.service.ExcelService;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class ExcelServiceTest {

    @Test
    void debeLeerExcelCorrectamente() throws Exception {

        byte[] contenido;

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            Sheet hoja = workbook.createSheet("Productos");

            Row encabezados = hoja.createRow(0);
            encabezados.createCell(0).setCellValue("Codigo");
            encabezados.createCell(1).setCellValue("Producto");
            encabezados.createCell(2).setCellValue("Precio");

            Row producto = hoja.createRow(1);
            producto.createCell(0).setCellValue("P001");
            producto.createCell(1).setCellValue("Teclado");
            producto.createCell(2).setCellValue(85000);

            workbook.write(output);
            contenido = output.toByteArray();
        }

        MockMultipartFile archivo = new MockMultipartFile(
            "archivo",
            "productos.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            contenido
        );

        ExcelService servicio = new ExcelService();

        ExcelResponse resultado = servicio.leerExcel(archivo);

        assertEquals(1, resultado.totalRegistros());
        assertEquals(3, resultado.columnas().size());
        assertEquals("P001", resultado.registros().get(0).get("Codigo"));
        assertEquals("Teclado", resultado.registros().get(0).get("Producto"));
        assertEquals("85000", resultado.registros().get(0).get("Precio"));
    }
}
