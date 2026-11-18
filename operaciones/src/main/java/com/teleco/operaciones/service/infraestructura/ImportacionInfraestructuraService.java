package com.teleco.operaciones.service.infraestructura;

import com.teleco.operaciones.model.infraestructura.DireccionRed;
import com.teleco.operaciones.model.infraestructura.Infraestructura;
import com.teleco.operaciones.model.infraestructura.ResultadoImportacionInfraestructura;
import com.teleco.operaciones.repository.infraestructura.DireccionRedRepository;
import com.teleco.operaciones.repository.infraestructura.InfraestructuraRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

@Service
public class ImportacionInfraestructuraService {

    private final InfraestructuraRepository infraestructuraRepo;
    private final DireccionRedRepository direccionRedRepo;

    public ImportacionInfraestructuraService(
            InfraestructuraRepository infraestructuraRepo,
            DireccionRedRepository direccionRedRepo
    ) {
        this.infraestructuraRepo = infraestructuraRepo;
        this.direccionRedRepo = direccionRedRepo;
    }

    @Transactional
    public ResultadoImportacionInfraestructura importar(
            MultipartFile archivo
    ) {
        validarArchivo(archivo);

        int activosCreados = 0;
        int activosActualizados = 0;
        int direccionesCreadas = 0;
        int filasOmitidas = 0;

        List<String> advertencias = new ArrayList<>();
        Map<String, Infraestructura> activosPorNombre = new HashMap<>();

        try (
                Workbook workbook = WorkbookFactory.create(
                        archivo.getInputStream()
                )
        ) {
            Sheet inventario = workbook.getSheet("01 Inventario");
            Sheet ipsMacGateway =
                    workbook.getSheet("08 IPS MAC GATEWAY");

            if (inventario == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "The sheet '01 Inventario' was not found."
                );
            }

            if (ipsMacGateway == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "The sheet '08 IPS MAC GATEWAY' was not found."
                );
            }

            DataFormatter formatter = new DataFormatter();

            int[] resultadoActivos = importarActivos(
                    inventario,
                    formatter,
                    activosPorNombre,
                    advertencias
            );

            activosCreados = resultadoActivos[0];
            activosActualizados = resultadoActivos[1];
            filasOmitidas += resultadoActivos[2];

            int[] resultadoDirecciones = importarDirecciones(
                    ipsMacGateway,
                    formatter,
                    activosPorNombre,
                    advertencias
            );

            direccionesCreadas = resultadoDirecciones[0];
            filasOmitidas += resultadoDirecciones[1];

            return new ResultadoImportacionInfraestructura(
                    activosCreados,
                    activosActualizados,
                    direccionesCreadas,
                    filasOmitidas,
                    advertencias
            );

        } catch (IOException error) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The Excel file could not be read.",
                    error
            );
        }
    }

    private int[] importarActivos(
            Sheet inventario,
            DataFormatter formatter,
            Map<String, Infraestructura> activosPorNombre,
            List<String> advertencias
    ) {
        int creados = 0;
        int actualizados = 0;
        int omitidos = 0;

        for (int fila = 1; fila <= inventario.getLastRowNum(); fila++) {
            Row row = inventario.getRow(fila);

            if (row == null) {
                continue;
            }

            String nombre = texto(row, 1, formatter);
            String tipoPacketTracer = texto(row, 2, formatter);
            String modelo = texto(row, 3, formatter);
            String categoria = texto(row, 4, formatter);
            String rol = texto(row, 5, formatter);
            String criticidad = texto(row, 6, formatter);
            String serial = texto(row, 7, formatter);
            String ips = texto(row, 10, formatter);

            if (nombre == null) {
                omitidos++;
                advertencias.add(
                        "Row " + (fila + 1)
                                + " in 01 Inventario was skipped: "
                                + "asset name is missing."
                );
                continue;
            }

            Optional<Infraestructura> existente =
                    buscarActivo(nombre, serial);

            boolean esNuevo = existente.isEmpty();

            Infraestructura activo = existente.orElseGet(
                    Infraestructura::new
            );

            activo.setNombre(nombre);
            activo.setTipo(mapearTipo(tipoPacketTracer, categoria));
            activo.setModelo(modelo);
            activo.setNumeroSerie(serial);
            activo.setDireccionIp(primeroDeLista(ips));
            activo.setDescripcion(
                    construirDescripcion(
                            tipoPacketTracer,
                            categoria,
                            rol,
                            criticidad
                    )
            );

            if (esNuevo) {
                activo.setEstado(
                        Infraestructura.EstadoInfraestructura.OPERATIVO
                );
                activo.setMonitoreoHabilitado(true);
            }

            Infraestructura guardado = infraestructuraRepo.save(activo);

            activosPorNombre.put(
                    normalizarClave(nombre),
                    guardado
            );

            if (esNuevo) {
                creados++;
            } else {
                actualizados++;
            }
        }

        return new int[]{creados, actualizados, omitidos};
    }

    private int[] importarDirecciones(
            Sheet ipsMacGateway,
            DataFormatter formatter,
            Map<String, Infraestructura> activosPorNombre,
            List<String> advertencias
    ) {
        int creadas = 0;
        int omitidas = 0;

        for (int fila = 1; fila <= ipsMacGateway.getLastRowNum(); fila++) {
            Row row = ipsMacGateway.getRow(fila);

            if (row == null) {
                continue;
            }

            String nombreEquipo = texto(row, 1, formatter);
            String ip = texto(row, 4, formatter);
            String mascara = texto(row, 5, formatter);
            String gateway = texto(row, 6, formatter);
            String dns = texto(row, 7, formatter);
            String mac = texto(row, 8, formatter);
            String dhcp = texto(row, 9, formatter);

            if (nombreEquipo == null || (ip == null && mac == null)) {
                omitidas++;
                continue;
            }

            Infraestructura activo = activosPorNombre.get(
                    normalizarClave(nombreEquipo)
            );

            if (activo == null) {
                activo = infraestructuraRepo
                        .findFirstByNombre(nombreEquipo)
                        .orElse(null);
            }

            if (activo == null) {
                omitidas++;
                advertencias.add(
                        "Row " + (fila + 1)
                                + " in 08 IPS MAC GATEWAY was skipped: "
                                + "asset '" + nombreEquipo
                                + "' does not exist."
                );
                continue;
            }

            boolean ipYaExiste = ip != null &&
                    direccionRedRepo
                            .existsByInfraestructuraIdAndDireccionIp(
                                    activo.getId(),
                                    ip
                            );

            boolean macYaExiste = mac != null &&
                    direccionRedRepo
                            .existsByInfraestructuraIdAndDireccionMac(
                                    activo.getId(),
                                    mac.toUpperCase()
                            );

            if (ipYaExiste || macYaExiste) {
                continue;
            }

            DireccionRed direccion = new DireccionRed();
            direccion.setInfraestructura(activo);
            direccion.setDireccionIp(ip);
            direccion.setMascara(mascara);
            direccion.setGateway(gateway);
            direccion.setDns(dns);
            direccion.setDireccionMac(
                    mac == null ? null : mac.toUpperCase()
            );
            direccion.setServidorDhcp(dhcp);

            direccionRedRepo.save(direccion);
            creadas++;
        }

        return new int[]{creadas, omitidas};
    }

    private Optional<Infraestructura> buscarActivo(
            String nombre,
            String serial
    ) {
        if (serial != null) {
            Optional<Infraestructura> porSerial =
                    infraestructuraRepo.findFirstByNumeroSerie(serial);

            if (porSerial.isPresent()) {
                return porSerial;
            }
        }

        return infraestructuraRepo.findFirstByNombre(nombre);
    }

    private Infraestructura.TipoInfraestructura mapearTipo(
            String tipo,
            String categoria
    ) {
        String valor = (
                (tipo == null ? "" : tipo) + " " +
                        (categoria == null ? "" : categoria)
        ).toUpperCase(Locale.ROOT);

        if (valor.contains("SERVER")) {
            return Infraestructura.TipoInfraestructura.SERVIDOR;
        }

        if (
                valor.contains("FIREWALL") ||
                        valor.contains("ASA") ||
                        valor.contains("SEGURIDAD")
        ) {
            return Infraestructura.TipoInfraestructura
                    .DISPOSITIVO_SEGURIDAD;
        }

        if (
                valor.contains("SWITCH") ||
                        valor.contains("ROUTER") ||
                        valor.contains("HUB") ||
                        valor.contains("WIRELESS")
        ) {
            return Infraestructura.TipoInfraestructura.EQUIPO_RED;
        }

        return Infraestructura.TipoInfraestructura.OTRO;
    }

    private String construirDescripcion(
            String tipo,
            String categoria,
            String rol,
            String criticidad
    ) {
        List<String> partes = new ArrayList<>();

        if (tipo != null) {
            partes.add("Source type: " + tipo);
        }

        if (categoria != null) {
            partes.add("Category: " + categoria);
        }

        if (rol != null) {
            partes.add("Role: " + rol);
        }

        if (criticidad != null) {
            partes.add("Criticality: " + criticidad);
        }

        return String.join(". ", partes);
    }

    private void validarArchivo(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Select an Excel file."
            );
        }

        String nombre = archivo.getOriginalFilename();

        if (
                nombre == null ||
                        !nombre.toLowerCase(Locale.ROOT).endsWith(".xlsx")
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only .xlsx files are supported."
            );
        }
    }

    private String texto(
            Row row,
            int columna,
            DataFormatter formatter
    ) {
        Cell celda = row.getCell(
                columna,
                Row.MissingCellPolicy.RETURN_BLANK_AS_NULL
        );

        if (celda == null) {
            return null;
        }

        String valor = formatter.formatCellValue(celda).trim();

        return valor.isBlank() ? null : valor;
    }

    private String primeroDeLista(String valor) {
        if (valor == null) {
            return null;
        }

        return limpiar(valor.split(";")[0]);
    }

    private String limpiar(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim();
    }

    private String normalizarClave(String valor) {
        return valor.trim().toLowerCase(Locale.ROOT);
    }
}