package com.teleco.operaciones.controller;

import com.teleco.operaciones.model.shared.Notificacion;
import com.teleco.operaciones.service.shared.NotificacionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(
            NotificacionService notificacionService
    ) {
        this.notificacionService = notificacionService;
    }

    @GetMapping("/mis-notificaciones")
    public List<Notificacion> listarMisNotificaciones(
            Authentication authentication
    ) {
        return notificacionService.listarMisNotificaciones(
                authentication.getName()
        );
    }

    @GetMapping("/no-leidas/count")
    public Map<String, Long> contarNoLeidas(
            Authentication authentication
    ) {
        long cantidad = notificacionService.contarNoLeidas(
                authentication.getName()
        );

        return Map.of("cantidad", cantidad);
    }

    @PatchMapping("/{id}/leida")
    public Notificacion marcarComoLeida(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        return notificacionService.marcarComoLeida(
                id,
                authentication.getName()
        );
    }
}