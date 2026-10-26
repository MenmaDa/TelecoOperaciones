package com.teleco.operaciones.repository.shared;

import com.teleco.operaciones.model.shared.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificacionRepository
        extends JpaRepository<Notificacion, UUID> {

    List<Notificacion> findByUsuarioIdOrderByFechaDesc(UUID usuarioId);

    long countByUsuarioIdAndLeidaFalse(UUID usuarioId);
}