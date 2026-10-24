package com.teleco.operaciones.model.shared;
import jakarta.persistence.*;

@Entity
@Table(name = "datos")
public class Datos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ruta", nullable = false, unique = true, length = 250)
    private String ruta;

    public Datos() {
    }

    public Datos(Long id, String ruta) {
        this.id = id;
        this.ruta = ruta;
    }

    public Long getId() {
        return id;
    }

    public String getRuta() {
        return ruta;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
}
