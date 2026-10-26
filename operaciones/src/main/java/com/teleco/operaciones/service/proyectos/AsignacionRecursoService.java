package com.teleco.operaciones.service.proyectos;

import com.teleco.operaciones.model.empleados.Empleado;
import com.teleco.operaciones.model.proyectos.AsignacionRecurso;
import com.teleco.operaciones.model.proyectos.AsignacionRecursoRequest;
import com.teleco.operaciones.model.proyectos.AsignacionRecursoResponse;
import com.teleco.operaciones.model.proyectos.Proyecto;
import com.teleco.operaciones.repository.empleados.EmpleadoRepository;
import com.teleco.operaciones.repository.proyectos.AsignacionRecursoRepository;
import com.teleco.operaciones.repository.proyectos.ProyectoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AsignacionRecursoService {

    private final AsignacionRecursoRepository asignacionRepo;
    private final ProyectoRepository proyectoRepo;
    private final EmpleadoRepository empleadoRepo;

    public AsignacionRecursoService(
            AsignacionRecursoRepository asignacionRepo,
            ProyectoRepository proyectoRepo,
            EmpleadoRepository empleadoRepo
    ) {
        this.asignacionRepo = asignacionRepo;
        this.proyectoRepo = proyectoRepo;
        this.empleadoRepo = empleadoRepo;
    }

    @Transactional(readOnly = true)
    public List<AsignacionRecursoResponse> listar() {
        return asignacionRepo.findAll()
                .stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AsignacionRecursoResponse> listarPorProyecto(UUID proyectoId) {
        return asignacionRepo.findByProyectoId(proyectoId)
                .stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    public AsignacionRecursoResponse crear(AsignacionRecursoRequest request) {
        AsignacionRecurso asignacion = new AsignacionRecurso();

        asignarDatos(asignacion, request, null);

        return convertirRespuesta(asignacionRepo.save(asignacion));
    }

    public AsignacionRecursoResponse actualizar(
            UUID id,
            AsignacionRecursoRequest request
    ) {
        AsignacionRecurso asignacion = buscarEntidad(id);

        asignarDatos(asignacion, request, id);

        return convertirRespuesta(asignacionRepo.save(asignacion));
    }

    public void eliminar(UUID id) {
        asignacionRepo.delete(buscarEntidad(id));
    }

    private AsignacionRecurso buscarEntidad(UUID id) {
        return asignacionRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Asignación de recurso no encontrada"
                ));
    }

    private void asignarDatos(
            AsignacionRecurso asignacion,
            AsignacionRecursoRequest request,
            UUID idActual
    ) {
        if (request.getProyectoId() == null || request.getEmpleadoId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El proyecto y el empleado son obligatorios"
            );
        }

        if (request.getFechaInicio() == null || request.getFechaFin() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Las fechas de inicio y fin son obligatorias"
            );
        }

        if (request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha final no puede ser anterior a la fecha inicial"
            );
        }

        if (request.getPorcentajeDedicacion() == null
                || request.getPorcentajeDedicacion() < 1
                || request.getPorcentajeDedicacion() > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La dedicación debe estar entre 1 y 100"
            );
        }

        Proyecto proyecto = proyectoRepo.findById(request.getProyectoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Proyecto no encontrado"
                ));

        Empleado empleado = empleadoRepo.findById(request.getEmpleadoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Empleado no encontrado"
                ));

        validarCapacidadEmpleado(
                empleado.getId(),
                request.getFechaInicio(),
                request.getFechaFin(),
                request.getPorcentajeDedicacion(),
                idActual
        );

        asignacion.setProyecto(proyecto);
        asignacion.setEmpleado(empleado);
        asignacion.setFechaInicio(request.getFechaInicio());
        asignacion.setFechaFin(request.getFechaFin());
        asignacion.setPorcentajeDedicacion(
                request.getPorcentajeDedicacion()
        );
    }

    private void validarCapacidadEmpleado(
            UUID empleadoId,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Integer nuevaDedicacion,
            UUID idActual
    ) {
        int dedicacionActual = asignacionRepo.findByEmpleadoId(empleadoId)
                .stream()
                .filter(asignacion ->
                        idActual == null
                                || !asignacion.getId().equals(idActual)
                )
                .filter(asignacion ->
                        !asignacion.getFechaInicio().isAfter(fechaFin)
                                && !asignacion.getFechaFin().isBefore(fechaInicio)
                )
                .mapToInt(AsignacionRecurso::getPorcentajeDedicacion)
                .sum();

        if (dedicacionActual + nuevaDedicacion > 100) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El empleado supera el 100% de dedicación "
                            + "durante el período seleccionado"
            );
        }
    }

    private AsignacionRecursoResponse convertirRespuesta(
            AsignacionRecurso asignacion
    ) {
        Empleado empleado = asignacion.getEmpleado();

        return new AsignacionRecursoResponse(
                asignacion.getId(),
                asignacion.getProyecto().getId(),
                asignacion.getProyecto().getNombre(),
                empleado.getId(),
                empleado.getNombres() + " " + empleado.getApellidos(),
                empleado.getCargo(),
                asignacion.getFechaInicio(),
                asignacion.getFechaFin(),
                asignacion.getPorcentajeDedicacion()
        );
    }
}