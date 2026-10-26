package com.teleco.operaciones.service.facturacion;

import com.teleco.operaciones.model.facturacion.DetalleFacturaRequest;
import com.teleco.operaciones.model.facturacion.FacturaRequest;
import com.teleco.operaciones.model.clientes.Cliente;
import com.teleco.operaciones.model.facturacion.DetalleFactura;
import com.teleco.operaciones.model.facturacion.Factura;
import com.teleco.operaciones.model.facturacion.ProductoServicio;
import com.teleco.operaciones.model.proyectos.Proyecto;
import com.teleco.operaciones.repository.clientes.ClienteRepository;
import com.teleco.operaciones.repository.facturacion.FacturaRepository;
import com.teleco.operaciones.repository.facturacion.ProductoServicioRepository;
import com.teleco.operaciones.repository.proyectos.ProyectoRepository;
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
public class FacturaService {

    private final FacturaRepository facturaRepo;
    private final ClienteRepository clienteRepo;
    private final ProyectoRepository proyectoRepo;
    private final ProductoServicioRepository productoServicioRepo;

    public FacturaService(
            FacturaRepository facturaRepo,
            ClienteRepository clienteRepo,
            ProyectoRepository proyectoRepo,
            ProductoServicioRepository productoServicioRepo
    ) {
        this.facturaRepo = facturaRepo;
        this.clienteRepo = clienteRepo;
        this.proyectoRepo = proyectoRepo;
        this.productoServicioRepo = productoServicioRepo;
    }

    public List<Factura> listar() {
        return facturaRepo.findAll();
    }

    public Factura buscarPorId(UUID id) {
        return facturaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Factura no encontrada"
                ));
    }

    public List<Factura> listarPorCliente(UUID clienteId) {
        return facturaRepo.findByClienteId(clienteId);
    }

    public Factura crear(FacturaRequest request) {
        if (facturaRepo.existsByNumero(request.getNumero())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una factura con ese número"
            );
        }

        Factura factura = new Factura();
        asignarDatos(factura, request);

        if (factura.getEstado() == Factura.EstadoFactura.EMITIDA) {
            descontarStock(factura);
        }

        return facturaRepo.save(factura);
    }

    public Factura actualizar(UUID id, FacturaRequest request) {
        Factura factura = buscarPorId(id);

        if (facturaRepo.existsByNumeroAndIdNot(request.getNumero(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una factura con ese número"
            );
        }

        Factura.EstadoFactura estadoAnterior = factura.getEstado();
        Factura.EstadoFactura estadoNuevo = request.getEstado() != null
                ? request.getEstado()
                : Factura.EstadoFactura.BORRADOR;

        // Una factura emitida solo puede anularse.
        if (estadoAnterior == Factura.EstadoFactura.EMITIDA) {
            if (estadoNuevo != Factura.EstadoFactura.ANULADA) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Una factura emitida solo puede anularse."
                );
            }

            // Conserva los detalles originales y devuelve el inventario.
            devolverStock(factura);
            factura.setEstado(Factura.EstadoFactura.ANULADA);

            return facturaRepo.save(factura);
        }

        // Una factura anulada no debe volver a modificarse ni emitirse.
        if (estadoAnterior == Factura.EstadoFactura.ANULADA) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Una factura anulada no puede modificarse."
            );
        }

        // Si era borrador, sí puede cambiar datos, detalles y estado.
        asignarDatos(factura, request);

        // BORRADOR -> EMITIDA: descuenta una sola vez.
        if (estadoAnterior == Factura.EstadoFactura.BORRADOR
                && factura.getEstado() == Factura.EstadoFactura.EMITIDA) {
            descontarStock(factura);
        }

        return facturaRepo.save(factura);
    }

    public void eliminar(UUID id) {
        Factura factura = buscarPorId(id);

        if (factura.getEstado() == Factura.EstadoFactura.EMITIDA) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Una factura emitida no se elimina. Debe anularse para devolver el stock."
            );
        }

        facturaRepo.delete(factura);
    }

    private void asignarDatos(
            Factura factura,
            FacturaRequest request
    ) {
        if (request.getNumero() == null || request.getNumero().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El número de factura es obligatorio"
            );
        }

        if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La factura debe tener al menos un detalle"
            );
        }

        LocalDate fechaEmision = request.getFechaEmision() != null
                ? request.getFechaEmision()
                : LocalDate.now();

        if (request.getFechaVencimiento() != null
                && request.getFechaVencimiento().isBefore(fechaEmision)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de vencimiento no puede ser anterior a la emisión"
            );
        }

        Cliente cliente = clienteRepo.findById(request.getClienteId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente no encontrado"
                ));

        Proyecto proyecto = obtenerProyectoValido(
                request.getProyectoId(),
                cliente
        );

        factura.setNumero(request.getNumero());
        factura.setCliente(cliente);
        factura.setProyecto(proyecto);
        factura.setFechaEmision(fechaEmision);
        factura.setFechaVencimiento(request.getFechaVencimiento());
        factura.setEstado(
                request.getEstado() != null
                        ? request.getEstado()
                        : Factura.EstadoFactura.BORRADOR
        );

        factura.getDetalles().clear();

        BigDecimal total = BigDecimal.ZERO;

        for (DetalleFacturaRequest item : request.getDetalles()) {
            DetalleFactura detalle = crearDetalle(item, factura);

            factura.getDetalles().add(detalle);

            total = total.add(detalle.getSubtotal());
        }

        factura.setTotal(total);
    }

    private Proyecto obtenerProyectoValido(
            UUID proyectoId,
            Cliente cliente
    ) {
        if (proyectoId == null) {
            return null;
        }

        Proyecto proyecto = proyectoRepo.findById(proyectoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Proyecto no encontrado"
                ));

        if (!proyecto.getCliente().getId().equals(cliente.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El proyecto seleccionado no pertenece al cliente"
            );
        }

        return proyecto;
    }

    private DetalleFactura crearDetalle(
            DetalleFacturaRequest item,
            Factura factura
    ) {
        if (item.getProductoServicioId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cada detalle debe tener un producto o servicio"
            );
        }

        if (item.getCantidad() == null || item.getCantidad() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La cantidad debe ser mayor a cero"
            );
        }

        ProductoServicio productoServicio =
                productoServicioRepo.findById(item.getProductoServicioId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Producto o servicio no encontrado"
                        ));

        if (!productoServicio.isActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No puedes facturar un producto o servicio inactivo"
            );
        }

        DetalleFactura detalle = new DetalleFactura();

        detalle.setFactura(factura);
        detalle.setProductoServicio(productoServicio);

        detalle.setDescripcion(
                item.getDescripcion() != null
                        && !item.getDescripcion().isBlank()
                        ? item.getDescripcion()
                        : productoServicio.getNombre()
        );

        detalle.setCantidad(item.getCantidad());
        detalle.setValorUnitario(productoServicio.getPrecio());

        return detalle;
    }
    private void descontarStock(Factura factura) {
        for (DetalleFactura detalle : factura.getDetalles()) {
            ProductoServicio producto = detalle.getProductoServicio();

            // Los servicios no manejan inventario.
            if (producto.getTipo() == ProductoServicio.TipoProductoServicio.SERVICIO) {
                continue;
            }

            Integer stockActual = producto.getStock();

            if (stockActual == null || stockActual < detalle.getCantidad()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Stock insuficiente para el producto: " + producto.getNombre()
                );
            }

            producto.setStock(stockActual - detalle.getCantidad());
        }
    }

    private void devolverStock(Factura factura) {
        for (DetalleFactura detalle : factura.getDetalles()) {
            ProductoServicio producto = detalle.getProductoServicio();

            if (producto.getTipo() == ProductoServicio.TipoProductoServicio.SERVICIO) {
                continue;
            }

            int stockActual = producto.getStock() == null ? 0 : producto.getStock();
            producto.setStock(stockActual + detalle.getCantidad());
        }
    }
}