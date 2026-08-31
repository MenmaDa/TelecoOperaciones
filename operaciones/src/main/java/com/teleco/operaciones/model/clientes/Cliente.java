package com.teleco.operaciones.model.clientes;
import com.teleco.operaciones.model.shared.BaseEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity

public class Cliente extends BaseEntity {

    @Column(nullable = false)
    private String razonSocial;

    @Column(unique = true)
    private String nit;

    private String email;
    private String telefono;
    private String direccion;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private List<ContactoCliente> contactos = new ArrayList<>();

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<ContactoCliente> getContactos() {
        return contactos;
    }

    public void setContactos(List<ContactoCliente> contactos) {
        this.contactos = contactos;
    }
}
