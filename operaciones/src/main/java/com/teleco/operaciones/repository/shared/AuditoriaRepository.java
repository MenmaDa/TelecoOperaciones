package com.teleco.operaciones.repository.shared;

import com.teleco.operaciones.model.shared.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AuditoriaRepository extends JpaRepository<Auditoria, UUID> {

    List<Auditoria> findByUsuarioId(UUID usuarioId);

    List<Auditoria> findByFechaBetween(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
}