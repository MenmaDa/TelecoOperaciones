package com.teleco.operaciones.service.auth;

import com.teleco.operaciones.model.auth.UsuarioRequest;
import com.teleco.operaciones.model.auth.Rol;
import com.teleco.operaciones.model.auth.Usuario;
import com.teleco.operaciones.model.empleados.Empleado;
import com.teleco.operaciones.repository.auth.RolRepository;
import com.teleco.operaciones.repository.auth.UsuarioRepository;
import com.teleco.operaciones.repository.empleados.EmpleadoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepo;
    private final RolRepository rolRepo;
    private final EmpleadoRepository empleadoRepo;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepo,
            RolRepository rolRepo,
            EmpleadoRepository empleadoRepo,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepo = usuarioRepo;
        this.rolRepo = rolRepo;
        this.empleadoRepo = empleadoRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listar() {
        return usuarioRepo.findAll();
    }

    public Usuario crear(UsuarioRequest request) {
        if (usuarioRepo.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un usuario con ese correo"
            );
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña es obligatoria"
            );
        }

        Usuario usuario = new Usuario();
        asignarDatos(usuario, request, true);

        Usuario usuarioGuardado = usuarioRepo.save(usuario);
        enlazarEmpleado(usuarioGuardado, request.getEmpleadoId());

        return usuarioGuardado;
    }

    public Usuario actualizar(UUID id, UsuarioRequest request) {
        Usuario usuario = buscarPorId(id);

        if (!usuario.getEmail().equals(request.getEmail())
                && usuarioRepo.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un usuario con ese correo"
            );
        }

        asignarDatos(usuario, request, false);
        Usuario usuarioGuardado = usuarioRepo.save(usuario);

        enlazarEmpleado(usuarioGuardado, request.getEmpleadoId());

        return usuarioGuardado;
    }

    public void eliminar(UUID id) {
        Usuario usuario = buscarPorId(id);

        empleadoRepo.findByUsuarioId(id).ifPresent((empleado) -> {
            empleado.setUsuario(null);
            empleadoRepo.save(empleado);
        });

        usuarioRepo.delete(usuario);
    }

    private Usuario buscarPorId(UUID id) {
        return usuarioRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));
    }

    private void asignarDatos(
            Usuario usuario,
            UsuarioRequest request,
            boolean esNuevo
    ) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El correo es obligatorio"
            );
        }

        if (request.getEmpleadoId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debes asociar el usuario a un empleado"
            );
        }

        if (request.getRolesIds() == null || request.getRolesIds().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debes asignar al menos un rol"
            );
        }

        List<Rol> rolesEncontrados =
                rolRepo.findAllById(request.getRolesIds());

        if (rolesEncontrados.size() != request.getRolesIds().size()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Uno o más roles no existen"
            );
        }

        usuario.setEmail(request.getEmail());
        usuario.setFotoPerfilUrl(request.getFotoPerfilUrl());
        usuario.setHabilitado(request.isHabilitado());
        usuario.setRoles(new HashSet<>(rolesEncontrados));

        if (esNuevo || (request.getPassword() != null
                && !request.getPassword().isBlank())) {
            usuario.setPasswordHash(
                    passwordEncoder.encode(request.getPassword())
            );
        }
    }

    private void enlazarEmpleado(Usuario usuario, UUID empleadoId) {
        Empleado empleado = empleadoRepo.findById(empleadoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Empleado no encontrado"
                ));

        empleadoRepo.findByUsuarioId(usuario.getId())
                .ifPresent((empleadoAnterior) -> {
                    if (!empleadoAnterior.getId().equals(empleado.getId())) {
                        empleadoAnterior.setUsuario(null);
                        empleadoRepo.save(empleadoAnterior);
                    }
                });

        if (empleado.getUsuario() != null
                && !empleado.getUsuario().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El empleado seleccionado ya tiene un usuario"
            );
        }

        empleado.setUsuario(usuario);
        empleadoRepo.save(empleado);
    }
}