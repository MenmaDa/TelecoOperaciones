package com.teleco.operaciones.repository.infraestructura;

import com.teleco.operaciones.model.infraestructura.DireccionRed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DireccionRedRepository
        extends JpaRepository<DireccionRed, UUID> {

    List<DireccionRed> findByInfraestructuraIdOrderByDireccionIpAsc(
            UUID infraestructuraId
    );

    boolean existsByInfraestructuraIdAndDireccionIp(
            UUID infraestructuraId,
            String direccionIp
    );

    boolean existsByInfraestructuraIdAndDireccionIpAndIdNot(
            UUID infraestructuraId,
            String direccionIp,
            UUID id
    );

    boolean existsByInfraestructuraIdAndDireccionMac(
            UUID infraestructuraId,
            String direccionMac
    );
}