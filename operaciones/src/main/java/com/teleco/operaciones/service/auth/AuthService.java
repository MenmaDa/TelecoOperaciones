package com.teleco.operaciones.service.auth;

import com.teleco.operaciones.model.auth.AuthResponse;
import com.teleco.operaciones.model.auth.LoginRequest;
import com.teleco.operaciones.model.shared.Auditoria;
import com.teleco.operaciones.service.shared.AuditoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.teleco.operaciones.model.auth.Usuario;
import com.teleco.operaciones.repository.auth.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepo;
    private final AuditoriaService auditoriaService;

    public AuthService(
            AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService,
            JwtService jwtService,
            UsuarioRepository usuarioRepo,
            AuditoriaService auditoriaService
    ) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.usuarioRepo = usuarioRepo;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public AuthResponse iniciarSesion(LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El correo y la contraseña son obligatorios"
            );
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException error) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Correo o contraseña incorrectos"
            );
        }

        UserDetails userDetails = userDetailsService
                .loadUserByUsername(request.getEmail());

        Usuario usuario = usuarioRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));

        String nombre = usuario.getEmpleado() != null
                ? usuario.getEmpleado().getNombres() + " "
                + usuario.getEmpleado().getApellidos()
                : usuario.getEmail();

        String token = jwtService.generarToken(userDetails);

        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(rol -> rol.replace("ROLE_", ""))
                .toList();

        auditoriaService.registrar(
                Auditoria.AccionAuditoria.INICIAR_SESION,
                "USUARIO",
                usuario.getId().toString(),
                "Successful login",
                usuario
        );

        return new AuthResponse(
                token,
                userDetails.getUsername(),
                nombre,
                usuario.getFotoPerfilUrl(),
                roles
        );
    }
}