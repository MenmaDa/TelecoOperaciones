package com.teleco.operaciones.service.shared;

import com.teleco.operaciones.model.auth.Usuario;
import com.teleco.operaciones.model.shared.Notificacion;
import com.teleco.operaciones.repository.auth.UsuarioRepository;
import com.teleco.operaciones.repository.shared.NotificacionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificacionService {

    private final NotificacionRepository notificacionRepo;
    private final UsuarioRepository usuarioRepo;

    public NotificacionService(
            NotificacionRepository notificacionRepo,
            UsuarioRepository usuarioRepo
    ) {
        this.notificacionRepo = notificacionRepo;
        this.usuarioRepo = usuarioRepo;
    }

    public void crear(
            Usuario usuario,
            String titulo,
            String mensaje,
            Notificacion.TipoNotificacion tipo
    ) {
        if (usuario == null) {
            return;
        }

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setTipo(tipo);
        notificacion.setLeida(false);
        notificacion.setFecha(LocalDateTime.now());

        notificacionRepo.save(notificacion);
    }

    @Transactional(readOnly = true)
    public List<Notificacion> listarMisNotificaciones(String email) {
        Usuario usuario = buscarUsuarioPorEmail(email);

        return notificacionRepo.findByUsuarioIdOrderByFechaDesc(
                usuario.getId()
        );
    }

    @Transactional(readOnly = true)
    public long contarNoLeidas(String email) {
        Usuario usuario = buscarUsuarioPorEmail(email);

        return notificacionRepo.countByUsuarioIdAndLeidaFalse(
                usuario.getId()
        );
    }

    public Notificacion marcarComoLeida(UUID notificacionId, String email) {
        Usuario usuario = buscarUsuarioPorEmail(email);

        Notificacion notificacion = notificacionRepo.findById(notificacionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Notificación no encontrada"
                ));

        if (!notificacion.getUsuario().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No puedes modificar esta notificación"
            );
        }

        notificacion.setLeida(true);

        return notificacionRepo.save(notificacion);
    }

    private Usuario buscarUsuarioPorEmail(String email) {
        return usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));
    }
}