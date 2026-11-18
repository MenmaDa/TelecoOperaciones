package com.teleco.operaciones.service.infraestructura;

import com.teleco.operaciones.model.infraestructura.Infraestructura;
import com.teleco.operaciones.model.infraestructura.MonitoreoInfraestructura;
import com.teleco.operaciones.model.infraestructura.MonitoreoInfraestructuraResponse;
import com.teleco.operaciones.model.infraestructura.ResumenMonitoreoInfraestructuraResponse;
import com.teleco.operaciones.repository.infraestructura.InfraestructuraRepository;
import com.teleco.operaciones.repository.infraestructura.MonitoreoInfraestructuraRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MonitoreoInfraestructuraService {

    private final InfraestructuraRepository infraestructuraRepo;
    private final MonitoreoInfraestructuraRepository monitoreoRepo;
    private final int timeoutMs;

    public MonitoreoInfraestructuraService(
            InfraestructuraRepository infraestructuraRepo,
            MonitoreoInfraestructuraRepository monitoreoRepo,
            @Value("${app.monitoreo.timeout-ms:3000}") int timeoutMs
    ) {
        this.infraestructuraRepo = infraestructuraRepo;
        this.monitoreoRepo = monitoreoRepo;
        this.timeoutMs = timeoutMs;
    }

    public MonitoreoInfraestructuraResponse verificarAhora(
            UUID infraestructuraId
    ) {
        Infraestructura infraestructura = buscarInfraestructura(
                infraestructuraId
        );

        if (!infraestructura.isMonitoreoHabilitado()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El monitoreo está deshabilitado para este activo"
            );
        }

        MonitoreoInfraestructura monitoreo = new MonitoreoInfraestructura();

        monitoreo.setInfraestructura(infraestructura);
        monitoreo.setFechaHora(LocalDateTime.now());

        realizarVerificacion(infraestructura, monitoreo);

        return convertirRespuesta(monitoreoRepo.save(monitoreo));
    }

    @Transactional(readOnly = true)
    public ResumenMonitoreoInfraestructuraResponse obtenerResumen(
            UUID infraestructuraId
    ) {
        Infraestructura infraestructura = buscarInfraestructura(
                infraestructuraId
        );

        List<MonitoreoInfraestructura> revisiones = monitoreoRepo
                .findTop100ByInfraestructuraIdOrderByFechaHoraDesc(
                        infraestructuraId
                );

        long totalRevisiones = revisiones.size();

        long revisionesExitosas = revisiones.stream()
                .filter(MonitoreoInfraestructura::isDisponible)
                .count();

        double disponibilidad = totalRevisiones == 0
                ? 0
                : (revisionesExitosas * 100.0) / totalRevisiones;

        List<Integer> latencias = revisiones.stream()
                .filter(MonitoreoInfraestructura::isDisponible)
                .map(MonitoreoInfraestructura::getLatenciaMs)
                .filter(latencia -> latencia != null)
                .toList();

        Double latenciaPromedio = latencias.isEmpty()
                ? null
                : latencias.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);

        Integer latenciaMinima = latencias.isEmpty()
                ? null
                : latencias.stream()
                .min(Integer::compareTo)
                .orElse(null);

        Integer latenciaMaxima = latencias.isEmpty()
                ? null
                : latencias.stream()
                .max(Integer::compareTo)
                .orElse(null);

        MonitoreoInfraestructuraResponse ultimaRevision = revisiones.isEmpty()
                ? null
                : convertirRespuesta(revisiones.get(0));

        // Para la gráfica: del registro más antiguo al más nuevo.
        List<MonitoreoInfraestructuraResponse> historial = revisiones.stream()
                .sorted(
                        Comparator.comparing(
                                MonitoreoInfraestructura::getFechaHora
                        )
                )
                .map(this::convertirRespuesta)
                .toList();

        return new ResumenMonitoreoInfraestructuraResponse(
                infraestructura.getId(),
                infraestructura.getNombre(),
                infraestructura.getDireccionIp(),
                infraestructura.getPuertoMonitoreo(),
                ultimaRevision,
                totalRevisiones,
                Math.round(disponibilidad * 100.0) / 100.0,
                latenciaPromedio,
                latenciaMinima,
                latenciaMaxima,
                historial
        );
    }

    private void realizarVerificacion(
            Infraestructura infraestructura,
            MonitoreoInfraestructura monitoreo
    ) {
        String direccionIp = infraestructura.getDireccionIp();

        if (direccionIp == null || direccionIp.isBlank()) {
            monitoreo.setDisponible(false);
            monitoreo.setMensaje(
                    "El activo no tiene una dirección IP configurada"
            );
            monitoreo.setTipoVerificacion(
                    MonitoreoInfraestructura.TipoVerificacion.IP
            );
            return;
        }

        Integer puerto = infraestructura.getPuertoMonitoreo();
        long inicio = System.nanoTime();

        try {
            if (puerto != null) {
                verificarPuertoTcp(direccionIp, puerto);

                monitoreo.setTipoVerificacion(
                        MonitoreoInfraestructura.TipoVerificacion.TCP
                );
                monitoreo.setMensaje(
                        "Conexión TCP realizada correctamente"
                );
            } else {
                verificarDireccionIp(direccionIp);

                monitoreo.setTipoVerificacion(
                        MonitoreoInfraestructura.TipoVerificacion.IP
                );
                monitoreo.setMensaje(
                        "Dirección IP alcanzable"
                );
            }

            long duracionMs = (System.nanoTime() - inicio) / 1_000_000;

            monitoreo.setDisponible(true);
            monitoreo.setLatenciaMs((int) Math.max(1, duracionMs));

        } catch (Exception error) {
            monitoreo.setDisponible(false);
            monitoreo.setLatenciaMs(null);

            monitoreo.setTipoVerificacion(
                    puerto != null
                            ? MonitoreoInfraestructura.TipoVerificacion.TCP
                            : MonitoreoInfraestructura.TipoVerificacion.IP
            );

            monitoreo.setMensaje(
                    error.getMessage() != null
                            ? error.getMessage()
                            : "No fue posible conectar con el activo"
            );
        }
    }

    private void verificarDireccionIp(String direccionIp) throws Exception {
        InetAddress direccion = InetAddress.getByName(direccionIp);

        if (!direccion.isReachable(timeoutMs)) {
            throw new IllegalStateException(
                    "La dirección IP no respondió en "
                            + timeoutMs + " ms"
            );
        }
    }

    private void verificarPuertoTcp(
            String direccionIp,
            Integer puerto
    ) throws Exception {
        if (puerto < 1 || puerto > 65535) {
            throw new IllegalArgumentException(
                    "El puerto debe estar entre 1 y 65535"
            );
        }

        try (Socket socket = new Socket()) {
            socket.connect(
                    new InetSocketAddress(direccionIp, puerto),
                    timeoutMs
            );
        }
    }

    private Infraestructura buscarInfraestructura(UUID id) {
        return infraestructuraRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Activo de infraestructura no encontrado"
                ));
    }

    private MonitoreoInfraestructuraResponse convertirRespuesta(
            MonitoreoInfraestructura monitoreo
    ) {
        return new MonitoreoInfraestructuraResponse(
                monitoreo.getId(),
                monitoreo.getInfraestructura().getId(),
                monitoreo.getFechaHora(),
                monitoreo.isDisponible(),
                monitoreo.getLatenciaMs(),
                monitoreo.getTipoVerificacion().name(),
                monitoreo.getMensaje()
        );
    }
}