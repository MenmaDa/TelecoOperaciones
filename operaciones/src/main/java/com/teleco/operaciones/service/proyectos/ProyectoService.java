package com.teleco.operaciones.service.proyectos;

import com.teleco.operaciones.model.proyectos.ProyectoRequest;
import com.teleco.operaciones.model.clientes.Cliente;
import com.teleco.operaciones.model.facturacion.Contrato;
import com.teleco.operaciones.model.proyectos.Proyecto;
import com.teleco.operaciones.repository.clientes.ClienteRepository;
import com.teleco.operaciones.repository.facturacion.ContratoRepository;
import com.teleco.operaciones.repository.proyectos.ProyectoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProyectoService {

    private final ProyectoRepository proyectoRepo;
    private final ClienteRepository clienteRepo;
    private final ContratoRepository contratoRepo;

    public ProyectoService(
            ProyectoRepository proyectoRepo,
            ClienteRepository clienteRepo,
            ContratoRepository contratoRepo
    ) {
        this.proyectoRepo = proyectoRepo;
        this.clienteRepo = clienteRepo;
        this.contratoRepo = contratoRepo;
    }

    public List<Proyecto> listar() {
        return proyectoRepo.findAll();
    }

    public Proyecto buscarPorId(UUID id) {
        return proyectoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Proyecto no encontrado"
                ));
    }

    public List<Proyecto> listarPorCliente(UUID clienteId) {
        return proyectoRepo.findByClienteId(clienteId);
    }

    public Proyecto crear(ProyectoRequest request) {
        Proyecto proyecto = new Proyecto();
        asignarDatos(proyecto, request);

        return proyectoRepo.save(proyecto);
    }

    public Proyecto actualizar(UUID id, ProyectoRequest request) {
        Proyecto proyecto = buscarPorId(id);
        asignarDatos(proyecto, request);

        return proyectoRepo.save(proyecto);
    }

    public void eliminar(UUID id) {
        proyectoRepo.delete(buscarPorId(id));
    }

    private void asignarDatos(Proyecto proyecto, ProyectoRequest request) {
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre del proyecto es obligatorio"
            );
        }

        if (request.getFechaInicio() != null
                && request.getFechaFinEstimada() != null
                && request.getFechaFinEstimada()
                .isBefore(request.getFechaInicio())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha estimada de finalización no puede ser anterior a la fecha de inicio"
            );
        }

        Cliente cliente = clienteRepo.findById(request.getClienteId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente no encontrado"
                ));

        Contrato contrato = null;

        if (request.getContratoId() != null) {
            contrato = contratoRepo.findById(request.getContratoId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Contrato no encontrado"
                    ));

            if (!contrato.getCliente().getId().equals(cliente.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El contrato seleccionado no pertenece al cliente"
                );
            }
        }

        proyecto.setNombre(request.getNombre());
        proyecto.setDescripcion(request.getDescripcion());
        proyecto.setCliente(cliente);
        proyecto.setContrato(contrato);
        proyecto.setFechaInicio(request.getFechaInicio());
        proyecto.setFechaFinEstimada(request.getFechaFinEstimada());
        proyecto.setEstado(request.getEstado());
        proyecto.setPresupuesto(request.getPresupuesto());
    }
}