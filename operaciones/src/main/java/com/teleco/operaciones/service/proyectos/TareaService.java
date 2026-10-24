package com.teleco.operaciones.service.proyectos;

import com.teleco.operaciones.model.proyectos.TareaRequest;
import com.teleco.operaciones.model.empleados.Empleado;
import com.teleco.operaciones.model.proyectos.Proyecto;
import com.teleco.operaciones.model.proyectos.Tarea;
import com.teleco.operaciones.repository.empleados.EmpleadoRepository;
import com.teleco.operaciones.repository.proyectos.ProyectoRepository;
import com.teleco.operaciones.repository.proyectos.TareaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TareaService {

    private final TareaRepository tareaRepo;
    private final ProyectoRepository proyectoRepo;
    private final EmpleadoRepository empleadoRepo;

    public TareaService(
            TareaRepository tareaRepo,
            ProyectoRepository proyectoRepo,
            EmpleadoRepository empleadoRepo
    ) {
        this.tareaRepo = tareaRepo;
        this.proyectoRepo = proyectoRepo;
        this.empleadoRepo = empleadoRepo;
    }

    public List<Tarea> listar() {
        return tareaRepo.findAll();
    }

    public Tarea buscarPorId(UUID id) {
        return tareaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tarea no encontrada"
                ));
    }

    public List<Tarea> listarPorProyecto(UUID proyectoId) {
        return tareaRepo.findByProyectoId(proyectoId);
    }

    public Tarea crear(TareaRequest request) {
        Tarea tarea = new Tarea();
        asignarDatos(tarea, request);

        return tareaRepo.save(tarea);
    }

    public Tarea actualizar(UUID id, TareaRequest request) {
        Tarea tarea = buscarPorId(id);
        asignarDatos(tarea, request);

        return tareaRepo.save(tarea);
    }

    public void eliminar(UUID id) {
        tareaRepo.delete(buscarPorId(id));
    }

    private void asignarDatos(Tarea tarea, TareaRequest request) {
        if (request.getTitulo() == null || request.getTitulo().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El título de la tarea es obligatorio"
            );
        }

        if (request.getHorasEstimadas() != null
                && request.getHorasEstimadas() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Las horas estimadas no pueden ser negativas"
            );
        }

        if (request.getHorasReales() != null
                && request.getHorasReales() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Las horas reales no pueden ser negativas"
            );
        }

        Proyecto proyecto = proyectoRepo.findById(request.getProyectoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Proyecto no encontrado"
                ));

        Empleado responsable = null;

        if (request.getResponsableId() != null) {
            responsable = empleadoRepo.findById(request.getResponsableId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Empleado responsable no encontrado"
                    ));
        }

        tarea.setTitulo(request.getTitulo());
        tarea.setDescripcion(request.getDescripcion());
        tarea.setProyecto(proyecto);
        tarea.setResponsable(responsable);
        tarea.setFechaLimite(request.getFechaLimite());
        tarea.setHorasEstimadas(request.getHorasEstimadas());
        tarea.setHorasReales(request.getHorasReales());
        tarea.setEstado(request.getEstado());
    }
}