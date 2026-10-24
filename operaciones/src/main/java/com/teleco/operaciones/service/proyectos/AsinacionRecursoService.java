package com.teleco.operaciones.service.proyectos;

import com.teleco.operaciones.repository.proyectos.AsignacionRecursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AsinacionRecursoService {

    private final AsignacionRecursoRepository asignacionRecursoRepo;

    @Autowired

    public AsinacionRecursoService(AsignacionRecursoRepository asignacionRecursoRepo) {
        this.asignacionRecursoRepo = asignacionRecursoRepo;
    }


}
