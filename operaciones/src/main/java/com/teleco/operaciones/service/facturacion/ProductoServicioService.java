package com.teleco.operaciones.service.facturacion;

import com.teleco.operaciones.model.facturacion.ProductoServicio;
import com.teleco.operaciones.repository.facturacion.ProductoServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductoServicioService {

    private final ProductoServicioRepository productoServicioRepo;

    @Autowired

    public ProductoServicioService(ProductoServicioRepository productoServicioRepo) {
        this.productoServicioRepo = productoServicioRepo;
    }


    public List<ProductoServicio> listar() {
        return productoServicioRepo.findAll();
    }

    public ProductoServicio buscarPorId(UUID id) {
        return productoServicioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Producto o servicio no encontrado"
                ));
    }

    public ProductoServicio crear(ProductoServicio productoServicio) {
        return productoServicioRepo.save(productoServicio);
    }

    public ProductoServicio actualizar(UUID id, ProductoServicio productoServicio) {
        ProductoServicio productoExistente = productoServicioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Producto o servicio no encontrado"
                ));

        productoExistente.setNombre(productoServicio.getNombre());
        productoExistente.setCategoria(productoServicio.getCategoria());
        productoExistente.setTipo(productoServicio.getTipo());
        productoExistente.setPrecio(productoServicio.getPrecio());
        productoExistente.setPuntuacionCalidad(
                productoServicio.getPuntuacionCalidad()
        );
        productoExistente.setStock(productoServicio.getStock());
        productoExistente.setDescripcion(productoServicio.getDescripcion());
        productoExistente.setActivo(productoServicio.isActivo());

        return productoServicioRepo.save(productoExistente);
    }

    public void eliminar(UUID id) {
        if (!productoServicioRepo.existsById(id)) {
            throw new RuntimeException("Producto o servicio no encontrado");
        }

        productoServicioRepo.deleteById(id);
    }
}
