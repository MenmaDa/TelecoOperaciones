package com.teleco.operaciones.service.auth;

import com.teleco.operaciones.model.auth.Rol;
import com.teleco.operaciones.repository.auth.RolRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class RolService {

    private final RolRepository rolRepo;

    public RolService(RolRepository rolRepo) {
        this.rolRepo = rolRepo;
    }

    public List<Rol> listar() {
        return rolRepo.findAll();
    }

    public Rol buscarPorId(UUID id) {
        return rolRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Rol no encontrado"
                ));
    }

    public Rol crear(Rol rol) {
        if (rolRepo.existsByNombre(rol.getNombre())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un rol con ese nombre"
            );
        }

        return rolRepo.save(rol);
    }

    public Rol actualizar(UUID id, Rol datos) {
        Rol rol = buscarPorId(id);

        if (rolRepo.existsByNombreAndIdNot(datos.getNombre(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un rol con ese nombre"
            );
        }

        rol.setNombre(datos.getNombre());
        rol.setDescripcion(datos.getDescripcion());

        return rolRepo.save(rol);
    }

    public void eliminar(UUID id) {
        rolRepo.delete(buscarPorId(id));
    }
}