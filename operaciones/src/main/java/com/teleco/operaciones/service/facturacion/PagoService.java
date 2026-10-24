package com.teleco.operaciones.service.facturacion;

import com.teleco.operaciones.repository.facturacion.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PagoService {

    private final PagoRepository pagoRepo;

    @Autowired
    public PagoService(PagoRepository pagoRepo) {
        this.pagoRepo = pagoRepo;
    }


}
