package com.teleco.operaciones.service.facturacion;

import com.teleco.operaciones.repository.facturacion.DetalleFacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DetalleFacturaService {

    private final DetalleFacturaRepository detalleFacturaRepo;

    @Autowired

    public DetalleFacturaService(DetalleFacturaRepository detalleFacturaRepo) {
        this.detalleFacturaRepo = detalleFacturaRepo;
    }


}
