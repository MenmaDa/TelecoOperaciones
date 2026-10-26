package com.teleco.operaciones.service.facturacion;

import com.teleco.operaciones.model.facturacion.PagoRequest;
import com.teleco.operaciones.model.facturacion.Factura;
import com.teleco.operaciones.model.facturacion.Pago;
import com.teleco.operaciones.repository.facturacion.FacturaRepository;
import com.teleco.operaciones.repository.facturacion.PagoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PagoService {

    private final PagoRepository pagoRepo;
    private final FacturaRepository facturaRepo;

    public PagoService(
            PagoRepository pagoRepo,
            FacturaRepository facturaRepo
    ) {
        this.pagoRepo = pagoRepo;
        this.facturaRepo = facturaRepo;
    }

    public List<Pago> listar() {
        return pagoRepo.findAll();
    }

    public List<Pago> listarPorFactura(UUID facturaId) {
        return pagoRepo.findByFacturaIdOrderByFechaPagoDesc(facturaId);
    }

    public Pago crear(PagoRequest request) {
        Factura factura = obtenerFactura(request.getFacturaId());

        validarFacturaParaPago(factura);
        validarMonto(factura, request.getMonto(), null);

        Pago pago = new Pago();
        asignarDatos(pago, request, factura);

        Pago pagoGuardado = pagoRepo.save(pago);

        recalcularEstadoFactura(factura);

        return pagoGuardado;
    }

    public Pago actualizar(UUID id, PagoRequest request) {
        Pago pago = buscarPorId(id);
        Factura facturaAnterior = pago.getFactura();
        Factura facturaNueva = obtenerFactura(request.getFacturaId());

        validarFacturaParaPago(facturaNueva);
        validarMonto(facturaNueva, request.getMonto(), id);

        asignarDatos(pago, request, facturaNueva);

        Pago pagoGuardado = pagoRepo.save(pago);

        recalcularEstadoFactura(facturaAnterior);

        if (!facturaAnterior.getId().equals(facturaNueva.getId())) {
            recalcularEstadoFactura(facturaNueva);
        }

        return pagoGuardado;
    }

    public void eliminar(UUID id) {
        Pago pago = buscarPorId(id);
        Factura factura = pago.getFactura();

        pagoRepo.delete(pago);

        recalcularEstadoFactura(factura);
    }

    private Pago buscarPorId(UUID id) {
        return pagoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pago no encontrado"
                ));
    }

    private Factura obtenerFactura(UUID facturaId) {
        if (facturaId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debes seleccionar una factura"
            );
        }

        return facturaRepo.findById(facturaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Factura no encontrada"
                ));
    }

    private void validarFacturaParaPago(Factura factura) {
        if (factura.getEstado() == Factura.EstadoFactura.BORRADOR) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No puedes registrar pagos para una factura en borrador"
            );
        }

        if (factura.getEstado() == Factura.EstadoFactura.ANULADA) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No puedes registrar pagos para una factura anulada"
            );
        }
    }

    private void validarMonto(
            Factura factura,
            BigDecimal monto,
            UUID pagoExcluidoId
    ) {
        if (monto == null || monto.signum() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El monto debe ser mayor a cero"
            );
        }

        BigDecimal totalPagadoSinPagoActual =
                calcularPagado(factura.getId(), pagoExcluidoId);

        BigDecimal saldo = factura.getTotal()
                .subtract(totalPagadoSinPagoActual);

        if (monto.compareTo(saldo) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El pago excede el saldo pendiente de la factura"
            );
        }
    }

    private void asignarDatos(
            Pago pago,
            PagoRequest request,
            Factura factura
    ) {
        pago.setFactura(factura);
        pago.setFechaPago(
                request.getFechaPago() != null
                        ? request.getFechaPago()
                        : LocalDate.now()
        );
        pago.setMonto(request.getMonto());
        pago.setMedioPago(request.getMedioPago());
        pago.setReferencia(request.getReferencia());
    }

    private BigDecimal calcularPagado(
            UUID facturaId,
            UUID pagoExcluidoId
    ) {
        return pagoRepo.findByFacturaIdOrderByFechaPagoDesc(facturaId)
                .stream()
                .filter(pago -> pagoExcluidoId == null
                        || !pago.getId().equals(pagoExcluidoId))
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void recalcularEstadoFactura(Factura factura) {
        if (factura.getEstado() == Factura.EstadoFactura.BORRADOR
                || factura.getEstado() == Factura.EstadoFactura.ANULADA) {
            return;
        }

        BigDecimal totalPagado = calcularPagado(factura.getId(), null);

        if (totalPagado.compareTo(factura.getTotal()) >= 0) {
            factura.setEstado(Factura.EstadoFactura.PAGADA);
        } else if (factura.getFechaVencimiento() != null
                && factura.getFechaVencimiento().isBefore(LocalDate.now())) {
            factura.setEstado(Factura.EstadoFactura.VENCIDA);
        } else {
            factura.setEstado(Factura.EstadoFactura.EMITIDA);
        }

        facturaRepo.save(factura);
    }
}