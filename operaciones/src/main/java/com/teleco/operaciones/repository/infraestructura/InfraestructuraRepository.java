package com.teleco.operaciones.repository.infraestructura;

import com.teleco.operaciones.model.infraestructura.Infraestructura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InfraestructuraRepository
        extends JpaRepository<Infraestructura, UUID> {
}