package com.teleco.operaciones.service.shared;

import com.teleco.operaciones.repository.shared.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepo;

    @Autowired
    public NotificacionService(NotificacionRepository notificacionRepo) {
        this.notificacionRepo = notificacionRepo;
    }


}
