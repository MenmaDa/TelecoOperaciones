package com.teleco.operaciones.service.empleados;

import com.teleco.operaciones.model.empleados.EmpleadoRequest;
import com.teleco.operaciones.model.empleados.Empleado;
import com.teleco.operaciones.model.empleados.Sede;
import com.teleco.operaciones.repository.empleados.EmpleadoRepository;
import com.teleco.operaciones.repository.empleados.SedeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepo;
    private final SedeRepository sedeRepo;

    public EmpleadoService(
            EmpleadoRepository empleadoRepo,
            SedeRepository sedeRepo
    ) {
        this.empleadoRepo = empleadoRepo;
        this.sedeRepo = sedeRepo;
    }

    public List<Empleado> listar() {
        return empleadoRepo.findAll();
    }

    public Empleado buscarPorId(UUID id) {
        return empleadoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Empleado no encontrado"
                ));
    }

    public Empleado crear(EmpleadoRequest request) {
        if (empleadoRepo.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un empleado con ese correo"
            );
        }

        Empleado empleado = new Empleado();
        asignarDatos(empleado, request);

        return empleadoRepo.save(empleado);
    }

    public Empleado actualizar(UUID id, EmpleadoRequest request) {
        Empleado empleado = buscarPorId(id);

        if (empleadoRepo.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un empleado con ese correo"
            );
        }

        asignarDatos(empleado, request);

        return empleadoRepo.save(empleado);
    }

    public void eliminar(UUID id) {
        empleadoRepo.delete(buscarPorId(id));
    }

    private void asignarDatos(
            Empleado empleado,
            EmpleadoRequest request
    ) {
        if (request.getNombres() == null || request.getNombres().isBlank()
                || request.getApellidos() == null || request.getApellidos().isBlank()
                || request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nombres, apellidos y correo son obligatorios"
            );
        }

        if (request.getCostoHora() != null
                && request.getCostoHora().signum() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El costo por hora no puede ser negativo"
            );
        }

        Sede sede = null;

        if (request.getSedeId() != null) {
            sede = sedeRepo.findById(request.getSedeId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Sede no encontrada"
                    ));
        }

        empleado.setNombres(request.getNombres());
        empleado.setApellidos(request.getApellidos());
        empleado.setEmail(request.getEmail());
        empleado.setCargo(request.getCargo());
        empleado.setCostoHora(request.getCostoHora());
        empleado.setSede(sede);
    }
}