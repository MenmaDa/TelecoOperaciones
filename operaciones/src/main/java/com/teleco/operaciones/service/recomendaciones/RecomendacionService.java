package com.teleco.operaciones.service.recomendaciones;

import com.teleco.operaciones.model.recomendaciones.ProductoModeloDto;
import com.teleco.operaciones.model.recomendaciones.RecomendacionModeloRequest;
import com.teleco.operaciones.model.recomendaciones.RecomendacionResponse;
import com.teleco.operaciones.model.recomendaciones.VentaModeloDto;
import com.teleco.operaciones.model.facturacion.Factura;
import com.teleco.operaciones.repository.clientes.ClienteRepository;
import com.teleco.operaciones.repository.facturacion.FacturaRepository;
import com.teleco.operaciones.repository.facturacion.ProductoServicioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class RecomendacionService {

    private final ClienteRepository clienteRepo;
    private final FacturaRepository facturaRepo;
    private final ProductoServicioRepository productoServicioRepo;
    private final RestClient restClient;

    public RecomendacionService(
            ClienteRepository clienteRepo,
            FacturaRepository facturaRepo,
            ProductoServicioRepository productoServicioRepo,
            @Value("${app.recomendador.url:http://localhost:8000}")
            String urlModeloPython
    ) {
        this.clienteRepo = clienteRepo;
        this.facturaRepo = facturaRepo;
        this.productoServicioRepo = productoServicioRepo;
        this.restClient = RestClient.builder()
                .baseUrl(urlModeloPython)
                .build();
    }

    public List<RecomendacionResponse> recomendar(
            UUID clienteId,
            BigDecimal presupuesto,
            Integer limite
    ) {
        if (!clienteRepo.existsById(clienteId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Cliente no encontrado"
            );
        }

        int limiteSeguro = limite == null ? 5 : Math.min(Math.max(limite, 1), 10);

        List<ProductoModeloDto> catalogo = productoServicioRepo.findAll()
                .stream()
                .map(producto -> new ProductoModeloDto(
                        producto.getId().toString(),
                        producto.getNombre(),
                        producto.getCategoria(),
                        producto.getTipo().name(),
                        producto.getPrecio().doubleValue(),
                        producto.getPuntuacionCalidad(),
                        producto.getStock(),
                        producto.isActivo()
                ))
                .toList();

        List<VentaModeloDto> ventas = facturaRepo
                .findByEstado(Factura.EstadoFactura.EMITIDA)
                .stream()
                .flatMap(factura -> factura.getDetalles().stream()
                        .map(detalle -> new VentaModeloDto(
                                factura.getId().toString(),
                                factura.getCliente().getId().toString(),
                                detalle.getProductoServicio().getId().toString()
                        )))
                .toList();

        RecomendacionModeloRequest request =
                new RecomendacionModeloRequest(
                        clienteId.toString(),
                        catalogo,
                        ventas,
                        presupuesto,
                        limiteSeguro
                );

        try {
            List<RecomendacionResponse> respuesta = restClient.post()
                    .uri("/recomendaciones")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(new ParameterizedTypeReference<
                            List<RecomendacionResponse>
                            >() {});

            return respuesta == null ? List.of() : respuesta;

        } catch (Exception error) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "El modelo de recomendaciones no está disponible. "
                            + "Verifica que Python se ejecute en el puerto 8000."
            );
        }
    }
}