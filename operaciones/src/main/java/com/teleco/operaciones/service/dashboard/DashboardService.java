package com.teleco.operaciones.service.dashboard;

import com.teleco.operaciones.model.dashboard.DashboardResumenResponse;
import com.teleco.operaciones.model.dashboard.FacturaDashboardResponse;
import com.teleco.operaciones.model.dashboard.PeriodoFacturacionResponse;
import com.teleco.operaciones.model.dashboard.ProductoStockBajoResponse;
import com.teleco.operaciones.model.facturacion.Factura;
import com.teleco.operaciones.model.facturacion.ProductoServicio;
import com.teleco.operaciones.model.proyectos.Proyecto;
import com.teleco.operaciones.model.proyectos.Tarea;
import com.teleco.operaciones.repository.clientes.ClienteRepository;
import com.teleco.operaciones.repository.facturacion.FacturaRepository;
import com.teleco.operaciones.repository.facturacion.PagoRepository;
import com.teleco.operaciones.repository.facturacion.ProductoServicioRepository;
import com.teleco.operaciones.repository.proyectos.ProyectoRepository;
import com.teleco.operaciones.repository.proyectos.TareaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import java.time.YearMonth;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final ClienteRepository clienteRepo;
    private final ProyectoRepository proyectoRepo;
    private final TareaRepository tareaRepo;
    private final FacturaRepository facturaRepo;
    private final PagoRepository pagoRepo;
    private final ProductoServicioRepository productoServicioRepo;

    public DashboardService(
            ClienteRepository clienteRepo,
            ProyectoRepository proyectoRepo,
            TareaRepository tareaRepo,
            FacturaRepository facturaRepo,
            PagoRepository pagoRepo,
            ProductoServicioRepository productoServicioRepo
    ) {
        this.clienteRepo = clienteRepo;
        this.proyectoRepo = proyectoRepo;
        this.tareaRepo = tareaRepo;
        this.facturaRepo = facturaRepo;
        this.pagoRepo = pagoRepo;
        this.productoServicioRepo = productoServicioRepo;
    }

    public DashboardResumenResponse obtenerResumen() {
        List<Factura> facturas = facturaRepo.findAll();
        List<Tarea> tareas = tareaRepo.findAll();
        List<ProductoServicio> productos = productoServicioRepo.findAll();

        long clientesActivos = clienteRepo.findAll()
                .stream()
                .filter(cliente -> cliente.isActivo())
                .count();

        long proyectosEnProgreso = proyectoRepo.findAll()
                .stream()
                .filter(proyecto ->
                        proyecto.getEstado() == Proyecto.EstadoProyecto.EN_PROGRESO
                )
                .count();

        long tareasPendientes = tareas.stream()
                .filter(tarea ->
                        tarea.getEstado() == Tarea.EstadoTarea.PENDIENTE
                                || tarea.getEstado() == Tarea.EstadoTarea.EN_PROGRESO
                )
                .count();

        long tareasBloqueadas = tareas.stream()
                .filter(tarea ->
                        tarea.getEstado() == Tarea.EstadoTarea.BLOQUEADA
                )
                .count();

        long tareasVencidas = tareas.stream()
                .filter(tarea ->
                        tarea.getFechaLimite() != null
                                && tarea.getFechaLimite().isBefore(LocalDate.now())
                                && tarea.getEstado() != Tarea.EstadoTarea.TERMINADA
                )
                .count();

        long productosStockBajo = productos.stream()
                .filter(producto ->
                        producto.isActivo()
                                && producto.getTipo()
                                == ProductoServicio.TipoProductoServicio.PRODUCTO
                                && producto.getStock() != null
                                && producto.getStock() <= 5
                )
                .count();

        BigDecimal facturacionTotal = facturas.stream()
                .filter(factura ->
                        factura.getEstado() == Factura.EstadoFactura.EMITIDA
                                || factura.getEstado() == Factura.EstadoFactura.PAGADA
                )
                .map(Factura::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pagosRecibidos = pagoRepo.findAll()
                .stream()
                .map(pago -> pago.getMonto())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal carteraPendiente = facturacionTotal
                .subtract(pagosRecibidos)
                .max(BigDecimal.ZERO);

        List<FacturaDashboardResponse> facturasRecientes = facturas.stream()
                .sorted(
                        Comparator.comparing(
                                Factura::getFechaEmision,
                                Comparator.nullsLast(Comparator.reverseOrder())
                        )
                )
                .limit(5)
                .map(factura -> new FacturaDashboardResponse(
                        factura.getNumero(),
                        factura.getCliente().getRazonSocial(),
                        factura.getTotal(),
                        factura.getEstado().name(),
                        factura.getFechaEmision()
                ))
                .toList();

        List<ProductoStockBajoResponse> stockBajo = productos.stream()
                .filter(producto ->
                        producto.isActivo()
                                && producto.getTipo()
                                == ProductoServicio.TipoProductoServicio.PRODUCTO
                                && producto.getStock() != null
                                && producto.getStock() <= 5
                )
                .sorted(Comparator.comparing(ProductoServicio::getStock))
                .limit(5)
                .map(producto -> new ProductoStockBajoResponse(
                        producto.getNombre(),
                        producto.getStock()
                ))
                .toList();
        List<Factura> facturasFacturables = facturas.stream()
                .filter(factura ->
                        factura.getEstado() == Factura.EstadoFactura.EMITIDA
                                || factura.getEstado() == Factura.EstadoFactura.PAGADA
                )
                .toList();

        List<PeriodoFacturacionResponse> facturacionPorMes =
                calcularFacturacionPorMes(facturasFacturables);

        List<PeriodoFacturacionResponse> facturacionPorSemana =
                calcularFacturacionPorSemana(facturasFacturables);

        return new DashboardResumenResponse(
                clientesActivos,
                proyectosEnProgreso,
                tareasPendientes,
                tareasBloqueadas,
                tareasVencidas,
                productosStockBajo,
                facturacionTotal,
                pagosRecibidos,
                carteraPendiente,
                facturasRecientes,
                stockBajo,
                facturacionPorMes,
                facturacionPorSemana
        );
    }
    private List<PeriodoFacturacionResponse> calcularFacturacionPorMes(
            List<Factura> facturas
    ) {
        List<PeriodoFacturacionResponse> resultado = new ArrayList<>();

        DateTimeFormatter formato = DateTimeFormatter.ofPattern(
                "MMM yyyy",
                new Locale("es", "CO")
        );

        YearMonth mesInicial = YearMonth.now().minusMonths(5);

        for (int i = 0; i < 6; i++) {
            YearMonth mes = mesInicial.plusMonths(i);

            BigDecimal total = facturas.stream()
                    .filter(factura ->
                            factura.getFechaEmision() != null
                                    && YearMonth.from(factura.getFechaEmision()).equals(mes)
                    )
                    .map(Factura::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            resultado.add(new PeriodoFacturacionResponse(
                    mes.format(formato),
                    total
            ));
        }

        return resultado;
    }

    private List<PeriodoFacturacionResponse> calcularFacturacionPorSemana(
            List<Factura> facturas
    ) {
        List<PeriodoFacturacionResponse> resultado = new ArrayList<>();

        LocalDate inicioSemanaActual = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd MMM");

        for (int i = 7; i >= 0; i--) {
            LocalDate inicio = inicioSemanaActual.minusWeeks(i);
            LocalDate fin = inicio.plusDays(6);

            BigDecimal total = facturas.stream()
                    .filter(factura ->
                            factura.getFechaEmision() != null
                                    && !factura.getFechaEmision().isBefore(inicio)
                                    && !factura.getFechaEmision().isAfter(fin)
                    )
                    .map(Factura::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            resultado.add(new PeriodoFacturacionResponse(
                    inicio.format(formato),
                    total
            ));
        }

        return resultado;
    }
}