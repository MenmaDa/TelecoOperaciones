package com.teleco.operaciones.service.auth;

import com.teleco.operaciones.repository.auth.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolService {

    private final RolRepository rolRepo;

    @Autowired
    public RolService(RolRepository rolRepo) {
        this.rolRepo = rolRepo;
    }
}
