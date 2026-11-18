package com.teleco.operaciones.service.infraestructura;

import com.teleco.operaciones.model.infraestructura.DireccionRed;
import com.teleco.operaciones.model.infraestructura.DireccionRedRequest;
import com.teleco.operaciones.model.infraestructura.DireccionRedResponse;
import com.teleco.operaciones.model.infraestructura.Infraestructura;
import com.teleco.operaciones.repository.infraestructura.DireccionRedRepository;
import com.teleco.operaciones.repository.infraestructura.InfraestructuraRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class DireccionRedService {

    private final DireccionRedRepository direccionRedRepo;
    private final InfraestructuraRepository infraestructuraRepo;

    public DireccionRedService(
            DireccionRedRepository direccionRedRepo,
            InfraestructuraRepository infraestructuraRepo
    ) {
        this.direccionRedRepo = direccionRedRepo;
        this.infraestructuraRepo = infraestructuraRepo;
    }

    public List<DireccionRedResponse> listarPorInfraestructura(
            UUID infraestructuraId
    ) {
        verificarInfraestructura(infraestructuraId);

        return direccionRedRepo
                .findByInfraestructuraIdOrderByDireccionIpAsc(infraestructuraId)
                .stream()
                .map(this::aResponse)
                .toList();
    }

    public DireccionRedResponse crear(
            UUID infraestructuraId,
            DireccionRedRequest request
    ) {
        Infraestructura infraestructura =
                verificarInfraestructura(infraestructuraId);

        validarDatos(infraestructuraId, request, null);

        DireccionRed direccion = new DireccionRed();
        direccion.setInfraestructura(infraestructura);
        asignarDatos(direccion, request);

        return aResponse(direccionRedRepo.save(direccion));
    }

    public DireccionRedResponse actualizar(
            UUID infraestructuraId,
            UUID direccionId,
            DireccionRedRequest request
    ) {
        DireccionRed direccion = buscarDireccion(
                infraestructuraId,
                direccionId
        );

        validarDatos(infraestructuraId, request, direccionId);
        asignarDatos(direccion, request);

        return aResponse(direccionRedRepo.save(direccion));
    }

    public void eliminar(UUID infraestructuraId, UUID direccionId) {
        DireccionRed direccion = buscarDireccion(
                infraestructuraId,
                direccionId
        );

        direccionRedRepo.delete(direccion);
    }

    private Infraestructura verificarInfraestructura(UUID infraestructuraId) {
        return infraestructuraRepo.findById(infraestructuraId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Infrastructure asset was not found."
                ));
    }

    private DireccionRed buscarDireccion(
            UUID infraestructuraId,
            UUID direccionId
    ) {
        DireccionRed direccion = direccionRedRepo.findById(direccionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Network address was not found."
                ));

        if (!direccion.getInfraestructura().getId()
                .equals(infraestructuraId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "The network address does not belong to this asset."
            );
        }

        return direccion;
    }

    private void validarDatos(
            UUID infraestructuraId,
            DireccionRedRequest request,
            UUID direccionId
    ) {
        String ip = limpiar(request.direccionIp());
        String mac = limpiar(request.direccionMac());

        if (ip == null && mac == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Enter at least an IP address or a MAC address."
            );
        }

        if (ip != null) {
            boolean duplicada = direccionId == null
                    ? direccionRedRepo.existsByInfraestructuraIdAndDireccionIp(
                    infraestructuraId, ip
            )
                    : direccionRedRepo
                    .existsByInfraestructuraIdAndDireccionIpAndIdNot(
                            infraestructuraId, ip, direccionId
                    );

            if (duplicada) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "This IP address is already registered for the asset."
                );
            }
        }
    }

    private void asignarDatos(
            DireccionRed direccion,
            DireccionRedRequest request
    ) {
        direccion.setDireccionIp(limpiar(request.direccionIp()));
        direccion.setMascara(limpiar(request.mascara()));
        direccion.setGateway(limpiar(request.gateway()));
        direccion.setDns(limpiar(request.dns()));
        direccion.setDireccionMac(
                limpiar(request.direccionMac()) == null
                        ? null
                        : limpiar(request.direccionMac()).toUpperCase()
        );
        direccion.setServidorDhcp(limpiar(request.servidorDhcp()));
    }

    private DireccionRedResponse aResponse(DireccionRed direccion) {
        Infraestructura infraestructura = direccion.getInfraestructura();

        return new DireccionRedResponse(
                direccion.getId(),
                infraestructura.getId(),
                infraestructura.getNombre(),
                direccion.getDireccionIp(),
                direccion.getMascara(),
                direccion.getGateway(),
                direccion.getDns(),
                direccion.getDireccionMac(),
                direccion.getServidorDhcp(),
                direccion.isActivo()
        );
    }

    private String limpiar(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim();
    }
}