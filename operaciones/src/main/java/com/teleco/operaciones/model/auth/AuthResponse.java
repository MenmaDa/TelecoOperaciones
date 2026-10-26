package com.teleco.operaciones.model.auth;

import java.util.List;

public class AuthResponse {

    private String token;
    private String email;
    private String nombre;
    private String fotoPerfilUrl;
    private List<String> roles;

    public AuthResponse(
            String token,
            String email,
            String nombre,
            String fotoPerfilUrl,
            List<String> roles
    ) {
        this.token = token;
        this.email = email;
        this.nombre = nombre;
        this.fotoPerfilUrl = fotoPerfilUrl;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFotoPerfilUrl() {
        return fotoPerfilUrl;
    }

    public List<String> getRoles() {
        return roles;
    }
}