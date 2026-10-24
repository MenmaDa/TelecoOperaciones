package com.teleco.operaciones.service.facturacion;

import com.teleco.operaciones.model.facturacion.ContratoRequest;
import com.teleco.operaciones.model.clientes.Cliente;
import com.teleco.operaciones.model.facturacion.Contrato;
import com.teleco.operaciones.repository.clientes.ClienteRepository;
import com.teleco.operaciones.repository.facturacion.ContratoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ContratoService {

    private final ContratoRepository contratoRepo;
    private final ClienteRepository clienteRepo;

    public ContratoService(
            ContratoRepository contratoRepo,
            ClienteRepository clienteRepo
    ) {
        this.contratoRepo = contratoRepo;
        this.clienteRepo = clienteRepo;
    }

    public List<Contrato> listar() {
        return contratoRepo.findAll();
    }

    public Contrato buscarPorId(UUID id) {
        return contratoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Contrato no encontrado"
                ));
    }

    public List<Contrato> listarPorCliente(UUID clienteId) {
        return contratoRepo.findByClienteId(clienteId);
    }

    public Contrato crear(ContratoRequest request) {
        if (contratoRepo.existsByNumero(request.getNumero())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un contrato con ese número"
            );
        }

        Contrato contrato = new Contrato();
        asignarDatos(contrato, request);

        return contratoRepo.save(contrato);
    }

    public Contrato actualizar(UUID id, ContratoRequest request) {
        Contrato contrato = buscarPorId(id);

        if (contratoRepo.existsByNumeroAndIdNot(request.getNumero(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un contrato con ese número"
            );
        }

        asignarDatos(contrato, request);

        return contratoRepo.save(contrato);
    }

    public void eliminar(UUID id) {
        Contrato contrato = buscarPorId(id);
        contratoRepo.delete(contrato);
    }

    private void asignarDatos(Contrato contrato, ContratoRequest request) {
        if (request.getFechaInicio() != null
                && request.getFechaFin() != null
                && request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de finalización no puede ser anterior a la fecha de inicio"
            );
        }

        Cliente cliente = clienteRepo.findById(request.getClienteId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente no encontrado"
                ));

        contrato.setNumero(request.getNumero());
        contrato.setCliente(cliente);
        contrato.setFechaInicio(request.getFechaInicio());
        contrato.setFechaFin(request.getFechaFin());
        contrato.setValor(request.getValor());
        contrato.setEstado(request.getEstado());
    }
}