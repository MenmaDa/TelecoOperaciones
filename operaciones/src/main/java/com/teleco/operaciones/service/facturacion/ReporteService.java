package com.teleco.operaciones.service.facturacion;

import com.teleco.operaciones.model.facturacion.Factura;
import com.teleco.operaciones.model.facturacion.Pago;
import com.teleco.operaciones.model.proyectos.Proyecto;
import com.teleco.operaciones.model.proyectos.Tarea;
import com.teleco.operaciones.model.reportes.CarteraResponse;
import com.teleco.operaciones.model.reportes.RentabilidadProyectoResponse;
import com.teleco.operaciones.model.reportes.ResumenFinancieroResponse;
import com.teleco.operaciones.repository.facturacion.FacturaRepository;
import com.teleco.operaciones.repository.facturacion.PagoRepository;
import com.teleco.operaciones.repository.proyectos.ProyectoRepository;
import com.teleco.operaciones.repository.proyectos.TareaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReporteService {

    private final FacturaRepository facturaRepo;
    private final PagoRepository pagoRepo;
    private final ProyectoRepository proyectoRepo;
    private final TareaRepository tareaRepo;

    public ReporteService(
            FacturaRepository facturaRepo,
            PagoRepository pagoRepo,
            ProyectoRepository proyectoRepo,
            TareaRepository tareaRepo
    ) {
        this.facturaRepo = facturaRepo;
        this.pagoRepo = pagoRepo;
        this.proyectoRepo = proyectoRepo;
        this.tareaRepo = tareaRepo;
    }

    public ResumenFinancieroResponse obtenerResumenFinanciero() {
        List<Factura> facturas = facturasValidas();

        BigDecimal facturado = facturas.stream()
                .map(Factura::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal recibido = pagoRepo.findAll()
                .stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long facturasConSaldo = facturas.stream()
                .filter(factura -> calcularSaldo(factura)
                        .compareTo(BigDecimal.ZERO) > 0)
                .count();

        return new ResumenFinancieroResponse(
                facturado,
                recibido,
                facturado.subtract(recibido).max(BigDecimal.ZERO),
                facturas.size(),
                facturasConSaldo
        );
    }

    public List<CarteraResponse> obtenerCartera() {
        return facturasValidas().stream()
                .map(factura -> {
                    BigDecimal pagos = calcularPagosFactura(factura);
                    BigDecimal saldo = factura.getTotal()
                            .subtract(pagos)
                            .max(BigDecimal.ZERO);

                    boolean vencida = saldo.compareTo(BigDecimal.ZERO) > 0
                            && factura.getFechaVencimiento() != null
                            && factura.getFechaVencimiento()
                            .isBefore(LocalDate.now());

                    return new CarteraResponse(
                            factura.getId(),
                            factura.getNumero(),
                            factura.getCliente().getRazonSocial(),
                            factura.getFechaEmision(),
                            factura.getFechaVencimiento(),
                            factura.getTotal(),
                            pagos,
                            saldo,
                            vencida
                    );
                })
                .filter(cartera ->
                        cartera.saldoPendiente().compareTo(BigDecimal.ZERO) > 0
                )
                .sorted(
                        Comparator.comparing(
                                CarteraResponse::fechaVencimiento,
                                Comparator.nullsLast(Comparator.naturalOrder())
                        )
                )
                .toList();
    }

    public List<RentabilidadProyectoResponse> obtenerRentabilidadProyectos() {
        List<Factura> facturas = facturasValidas();
        List<Tarea> tareas = tareaRepo.findAll();

        return proyectoRepo.findAll()
                .stream()
                .map(proyecto -> crearRentabilidad(proyecto, facturas, tareas))
                .sorted(
                        Comparator.comparing(
                                RentabilidadProyectoResponse::margen
                        ).reversed()
                )
                .toList();
    }

    private RentabilidadProyectoResponse crearRentabilidad(
            Proyecto proyecto,
            List<Factura> facturas,
            List<Tarea> tareas
    ) {
        BigDecimal facturado = facturas.stream()
                .filter(factura ->
                        factura.getProyecto() != null
                                && factura.getProyecto().getId().equals(proyecto.getId())
                )
                .map(Factura::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Tarea> tareasProyecto = tareas.stream()
                .filter(tarea ->
                        tarea.getProyecto().getId().equals(proyecto.getId())
                )
                .toList();

        int horasReales = tareasProyecto.stream()
                .map(Tarea::getHorasReales)
                .filter(horas -> horas != null)
                .mapToInt(Integer::intValue)
                .sum();

        BigDecimal costoManoObra = tareasProyecto.stream()
                .filter(tarea ->
                        tarea.getHorasReales() != null
                                && tarea.getResponsable() != null
                                && tarea.getResponsable().getCostoHora() != null
                )
                .map(tarea -> tarea.getResponsable()
                        .getCostoHora()
                        .multiply(BigDecimal.valueOf(tarea.getHorasReales()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RentabilidadProyectoResponse(
                proyecto.getId(),
                proyecto.getNombre(),
                proyecto.getCliente().getRazonSocial(),
                proyecto.getPresupuesto(),
                facturado,
                costoManoObra,
                facturado.subtract(costoManoObra),
                horasReales
        );
    }

    private List<Factura> facturasValidas() {
        return facturaRepo.findAll()
                .stream()
                .filter(factura ->
                        factura.getEstado() != Factura.EstadoFactura.BORRADOR
                                && factura.getEstado() != Factura.EstadoFactura.ANULADA
                )
                .toList();
    }

    private BigDecimal calcularSaldo(Factura factura) {
        return factura.getTotal()
                .subtract(calcularPagosFactura(factura))
                .max(BigDecimal.ZERO);
    }

    private BigDecimal calcularPagosFactura(Factura factura) {
        return pagoRepo.findByFacturaIdOrderByFechaPagoDesc(factura.getId())
                .stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}