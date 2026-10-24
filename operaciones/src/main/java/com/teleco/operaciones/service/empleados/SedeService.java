package com.teleco.operaciones.service.empleados;

import com.teleco.operaciones.model.empleados.Sede;
import com.teleco.operaciones.repository.empleados.SedeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SedeService {

    private final SedeRepository sedeRepo;

    public SedeService(SedeRepository sedeRepo) {
        this.sedeRepo = sedeRepo;
    }

    public List<Sede> listar() {
        return sedeRepo.findAll();
    }

    public Sede buscarPorId(UUID id) {
        return sedeRepo.findById(id).orElse(null);
    }

    public Sede crear(Sede sede) {
        return sedeRepo.save(sede);
    }

    public Sede actualizar(UUID id, Sede sede) {
        Sede sedeActual = buscarPorId(id);

        if (sedeActual == null) {
            return null;
        }

        sedeActual.setNombre(sede.getNombre());
        sedeActual.setCiudad(sede.getCiudad());
        sedeActual.setDireccion(sede.getDireccion());
        sedeActual.setTelefono(sede.getTelefono());

        return sedeRepo.save(sedeActual);
    }

    public void eliminar(UUID id) {
        sedeRepo.deleteById(id);
    }
}