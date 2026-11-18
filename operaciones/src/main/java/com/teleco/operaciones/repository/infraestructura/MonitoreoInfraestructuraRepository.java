package com.teleco.operaciones.repository.infraestructura;

import com.teleco.operaciones.model.infraestructura.MonitoreoInfraestructura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MonitoreoInfraestructuraRepository
        extends JpaRepository<MonitoreoInfraestructura, UUID> {

    List<MonitoreoInfraestructura>
    findTop100ByInfraestructuraIdOrderByFechaHoraDesc(
            UUID infraestructuraId
    );

    List<MonitoreoInfraestructura>
    findByInfraestructuraIdOrderByFechaHoraDesc(
            UUID infraestructuraId
    );
}