package com.teleco.operaciones.model.auth;

import java.util.Set;
import java.util.UUID;

public class UsuarioRequest {

    private String email;
    private String password;
    private boolean habilitado = true;
    private UUID empleadoId;
    private Set<UUID> rolesIds;
    private String fotoPerfilUrl;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public UUID getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(UUID empleadoId) {
        this.empleadoId = empleadoId;
    }

    public Set<UUID> getRolesIds() {
        return rolesIds;
    }

    public void setRolesIds(Set<UUID> rolesIds) {
        this.rolesIds = rolesIds;
    }

    public String getFotoPerfilUrl() {
        return fotoPerfilUrl;
    }

    public void setFotoPerfilUrl(String fotoPerfilUrl) {
        this.fotoPerfilUrl = fotoPerfilUrl;
    }
}