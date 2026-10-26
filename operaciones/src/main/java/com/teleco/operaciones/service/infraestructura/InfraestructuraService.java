package com.teleco.operaciones.service.infraestructura;

import com.teleco.operaciones.model.empleados.Empleado;
import com.teleco.operaciones.model.empleados.Sede;
import com.teleco.operaciones.model.infraestructura.Infraestructura;
import com.teleco.operaciones.model.infraestructura.InfraestructuraRequest;
import com.teleco.operaciones.model.infraestructura.InfraestructuraResponse;
import com.teleco.operaciones.repository.empleados.EmpleadoRepository;
import com.teleco.operaciones.repository.empleados.SedeRepository;
import com.teleco.operaciones.repository.infraestructura.InfraestructuraRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class InfraestructuraService {

    private final InfraestructuraRepository infraestructuraRepo;
    private final SedeRepository sedeRepo;
    private final EmpleadoRepository empleadoRepo;

    public InfraestructuraService(
            InfraestructuraRepository infraestructuraRepo,
            SedeRepository sedeRepo,
            EmpleadoRepository empleadoRepo
    ) {
        this.infraestructuraRepo = infraestructuraRepo;
        this.sedeRepo = sedeRepo;
        this.empleadoRepo = empleadoRepo;
    }

    @Transactional(readOnly = true)
    public List<InfraestructuraResponse> listar() {
        return infraestructuraRepo.findAll()
                .stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    public InfraestructuraResponse crear(InfraestructuraRequest request) {
        Infraestructura infraestructura = new Infraestructura();
        asignarDatos(infraestructura, request);

        return convertirRespuesta(infraestructuraRepo.save(infraestructura));
    }

    public InfraestructuraResponse actualizar(
            UUID id,
            InfraestructuraRequest request
    ) {
        Infraestructura infraestructura = buscarEntidad(id);
        asignarDatos(infraestructura, request);

        return convertirRespuesta(infraestructuraRepo.save(infraestructura));
    }

    public void eliminar(UUID id) {
        infraestructuraRepo.delete(buscarEntidad(id));
    }

    private Infraestructura buscarEntidad(UUID id) {
        return infraestructuraRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Infrastructure asset not found"
                ));
    }

    private void asignarDatos(
            Infraestructura infraestructura,
            InfraestructuraRequest request
    ) {
        if (request.nombre() == null || request.nombre().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The infrastructure name is required"
            );
        }

        if (request.tipo() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The infrastructure type is required"
            );
        }

        if (request.fechaAdquisicion() != null
                && request.fechaVencimiento() != null
                && request.fechaVencimiento()
                .isBefore(request.fechaAdquisicion())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The expiration date cannot be before the acquisition date"
            );
        }

        Sede sede = request.sedeId() == null
                ? null
                : sedeRepo.findById(request.sedeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Branch not found"
                ));

        Empleado responsable = request.responsableId() == null
                ? null
                : empleadoRepo.findById(request.responsableId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Responsible employee not found"
                ));

        infraestructura.setNombre(request.nombre());
        infraestructura.setTipo(request.tipo());
        infraestructura.setProveedor(request.proveedor());
        infraestructura.setModelo(request.modelo());
        infraestructura.setNumeroSerie(request.numeroSerie());
        infraestructura.setDireccionIp(request.direccionIp());
        infraestructura.setCapacidad(request.capacidad());
        infraestructura.setSede(sede);
        infraestructura.setResponsable(responsable);
        infraestructura.setEstado(
                request.estado() != null
                        ? request.estado()
                        : Infraestructura.EstadoInfraestructura.OPERATIVO
        );
        infraestructura.setFechaAdquisicion(request.fechaAdquisicion());
        infraestructura.setFechaVencimiento(request.fechaVencimiento());
        infraestructura.setDescripcion(request.descripcion());
    }

    private InfraestructuraResponse convertirRespuesta(
            Infraestructura infraestructura
    ) {
        Sede sede = infraestructura.getSede();
        Empleado responsable = infraestructura.getResponsable();

        return new InfraestructuraResponse(
                infraestructura.getId(),
                infraestructura.getNombre(),
                infraestructura.getTipo().name(),
                infraestructura.getProveedor(),
                infraestructura.getModelo(),
                infraestructura.getNumeroSerie(),
                infraestructura.getDireccionIp(),
                infraestructura.getCapacidad(),
                sede != null ? sede.getId() : null,
                sede != null ? sede.getNombre() : null,
                responsable != null ? responsable.getId() : null,
                responsable != null
                        ? responsable.getNombres() + " "
                        + responsable.getApellidos()
                        : null,
                infraestructura.getEstado().name(),
                infraestructura.getFechaAdquisicion(),
                infraestructura.getFechaVencimiento(),
                infraestructura.getDescripcion(),
                infraestructura.isActivo()
        );
    }
}