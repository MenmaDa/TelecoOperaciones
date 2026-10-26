package com.teleco.operaciones.controller;

import com.teleco.operaciones.model.auth.AuthResponse;
import com.teleco.operaciones.model.auth.LoginRequest;
import com.teleco.operaciones.service.auth.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse iniciarSesion(
            @RequestBody LoginRequest request
    ) {
        return authService.iniciarSesion(request);
    }
}