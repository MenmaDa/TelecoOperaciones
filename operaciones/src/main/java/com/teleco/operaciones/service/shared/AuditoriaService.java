package com.teleco.operaciones.service.shared;

import com.teleco.operaciones.model.auth.Usuario;
import com.teleco.operaciones.model.shared.Auditoria;
import com.teleco.operaciones.model.shared.AuditoriaResponse;
import com.teleco.operaciones.repository.auth.UsuarioRepository;
import com.teleco.operaciones.repository.shared.AuditoriaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepo;
    private final UsuarioRepository usuarioRepo;

    public AuditoriaService(
            AuditoriaRepository auditoriaRepo,
            UsuarioRepository usuarioRepo
    ) {
        this.auditoriaRepo = auditoriaRepo;
        this.usuarioRepo = usuarioRepo;
    }

    public void registrar(
            Auditoria.AccionAuditoria accion,
            String entidad,
            String entidadId,
            String detalle
    ) {
        registrar(
                accion,
                entidad,
                entidadId,
                detalle,
                obtenerUsuarioAutenticado()
        );
    }

    public void registrar(
            Auditoria.AccionAuditoria accion,
            String entidad,
            String entidadId,
            String detalle,
            Usuario usuario
    ) {
        Auditoria auditoria = new Auditoria();

        auditoria.setAccion(accion);
        auditoria.setEntidad(entidad);
        auditoria.setEntidadId(entidadId);
        auditoria.setDetalle(detalle);
        auditoria.setFecha(LocalDateTime.now());
        auditoria.setUsuario(usuario);

        auditoriaRepo.save(auditoria);
    }

    @Transactional(readOnly = true)
    public List<AuditoriaResponse> listarRecientes() {
        return auditoriaRepo.findTop20ByOrderByFechaDesc()
                .stream()
                .map(auditoria -> new AuditoriaResponse(
                        auditoria.getId(),
                        auditoria.getAccion().name(),
                        auditoria.getEntidad(),
                        auditoria.getEntidadId(),
                        auditoria.getDetalle(),
                        auditoria.getFecha(),
                        auditoria.getUsuario() != null
                                ? auditoria.getUsuario().getEmail()
                                : "System"
                ))
                .toList();
    }

    private Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return usuarioRepo.findByEmail(authentication.getName())
                .orElse(null);
    }
}