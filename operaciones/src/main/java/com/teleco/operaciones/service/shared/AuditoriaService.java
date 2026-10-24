package com.teleco.operaciones.service.shared;

import com.teleco.operaciones.repository.shared.AuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepo;

    @Autowired
    public AuditoriaService(AuditoriaRepository auditoriaRepo) {
        this.auditoriaRepo = auditoriaRepo;
    }


}