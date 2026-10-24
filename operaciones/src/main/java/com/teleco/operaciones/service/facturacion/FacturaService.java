package com.teleco.operaciones.service.facturacion;

import com.teleco.operaciones.repository.facturacion.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepo;

    @Autowired

    public FacturaService(FacturaRepository facturaRepo) {
        this.facturaRepo = facturaRepo;
    }
}
