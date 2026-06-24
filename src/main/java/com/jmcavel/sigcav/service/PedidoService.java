package com.jmcavel.sigcav.service;

import com.jmcavel.sigcav.dto.request.ActualizarPedidoRequest;
import com.jmcavel.sigcav.dto.request.CambiarEstadoPedidoRequest;
import com.jmcavel.sigcav.dto.request.CrearNotaInternaRequest;
import com.jmcavel.sigcav.dto.request.CrearPedidoRequest;
import com.jmcavel.sigcav.dto.response.NotaInternaPedidoResponse;
import com.jmcavel.sigcav.dto.response.PedidoFichaResponse;
import com.jmcavel.sigcav.dto.response.PedidoResponse;
import com.jmcavel.sigcav.entity.*;
import com.jmcavel.sigcav.enums.AccionAuditoria;
import com.jmcavel.sigcav.enums.EntidadAuditoria;
import com.jmcavel.sigcav.enums.EstadoPagoPedido;
import com.jmcavel.sigcav.enums.EstadoPedido;
import com.jmcavel.sigcav.exception.EstadoInvalidoException;
import com.jmcavel.sigcav.exception.RecursoNoEncontradoException;
import com.jmcavel.sigcav.exception.ReglaDeNegocioException;
import com.jmcavel.sigcav.mapper.PedidoMapper;
import com.jmcavel.sigcav.repository.*;
import com.jmcavel.sigcav.util.NumeracionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CotizacionRepository cotizacionRepository;
    private final ClienteRepository clienteRepository;
    private final CategoriaProductoRepository categoriaProductoRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialEstadoPedidoRepository historialEstadoPedidoRepository;
    private final NotaInternaPedidoRepository notaInternaPedidoRepository;
    private final PedidoMapper pedidoMapper;
    private final NumeracionUtil numeracionUtil;
    private final AuditoriaService auditoriaService;

    private static final Map<EstadoPedido, Set<EstadoPedido>> TRANSICIONES_PERMITIDAS =
            new EnumMap<>(EstadoPedido.class);

    static {
        TRANSICIONES_PERMITIDAS.put(EstadoPedido.PENDIENTE,
                Set.of(EstadoPedido.EN_PRODUCCION, EstadoPedido.ANULADO));
        TRANSICIONES_PERMITIDAS.put(EstadoPedido.EN_PRODUCCION,
                Set.of(EstadoPedido.CONTROL_CALIDAD, EstadoPedido.ANULADO));
        TRANSICIONES_PERMITIDAS.put(EstadoPedido.CONTROL_CALIDAD,
                Set.of(EstadoPedido.LISTO_ENTREGA, EstadoPedido.EN_PRODUCCION, EstadoPedido.ANULADO));
        TRANSICIONES_PERMITIDAS.put(EstadoPedido.LISTO_ENTREGA,
                Set.of(EstadoPedido.ENTREGADO, EstadoPedido.ANULADO));
        TRANSICIONES_PERMITIDAS.put(EstadoPedido.ENTREGADO,
                Set.of(EstadoPedido.FACTURADO));
        TRANSICIONES_PERMITIDAS.put(EstadoPedido.FACTURADO, Set.of());
        TRANSICIONES_PERMITIDAS.put(EstadoPedido.ANULADO, Set.of());
    }

    @Transactional
    public PedidoResponse crear(CrearPedidoRequest request, Long usuarioId) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con id: " + request.getClienteId()
                ));

        CategoriaProducto categoria = categoriaProductoRepository.findById(request.getCategoriaProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Categoría no encontrada con id: " + request.getCategoriaProductoId()
                ));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        if (!request.getFechaEntregaComprometida().isAfter(LocalDate.now().minusDays(1))) {
            throw new ReglaDeNegocioException(
                    "La fecha de entrega comprometida debe ser igual o posterior a hoy"
            );
        }

        Cotizacion cotizacion = null;
        if (request.getCotizacionId() != null) {
            cotizacion = cotizacionRepository.findById(request.getCotizacionId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Cotización no encontrada con id: " + request.getCotizacionId()
                    ));
            if (cotizacion.isConvertidaEnPedido()) {
                throw new ReglaDeNegocioException(
                        "La cotización ya fue convertida en un pedido anteriormente"
                );
            }
        }

        String numeroPedido = numeracionUtil.generarNumeroPedido();

        Pedido pedido = Pedido.builder()
                .numeroPedido(numeroPedido)
                .cotizacion(cotizacion)
                .cliente(cliente)
                .categoriaProducto(categoria)
                .descripcion(request.getDescripcion())
                .especificaciones(request.getEspecificaciones())
                .cantidad(request.getCantidad())
                .precioVenta(request.getPrecioVenta())
                .fechaIngreso(LocalDate.now())
                .fechaEntregaComprometida(request.getFechaEntregaComprometida())
                .estado(EstadoPedido.PENDIENTE)
                .estadoPago(EstadoPagoPedido.SIN_ADELANTO)
                .costoTotal(BigDecimal.ZERO)
                .creadoPor(usuario)
                .build();

        Pedido guardado = pedidoRepository.save(pedido);

        registrarHistorialEstado(guardado, null, EstadoPedido.PENDIENTE, "Pedido creado", usuario);

        if (cotizacion != null) {
            cotizacion.setConvertidaEnPedido(true);
            cotizacionRepository.save(cotizacion);
        }

        auditoriaService.registrar(
                usuarioId,
                AccionAuditoria.CREAR,
                EntidadAuditoria.PEDIDO,
                guardado.getId(),
                "Pedido creado: " + numeroPedido
        );

        return pedidoMapper.toResponse(guardado);
    }

    @Transactional
    public PedidoResponse convertirDeCotizacion(Long cotizacionId, Long usuarioId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cotización no encontrada con id: " + cotizacionId
                ));

        if (cotizacion.getEstado() != com.jmcavel.sigcav.enums.EstadoCotizacion.APROBADA) {
            throw new ReglaDeNegocioException(
                    "Solo se pueden convertir cotizaciones en estado APROBADA. Estado actual: "
                            + cotizacion.getEstado()
            );
        }

        if (cotizacion.isConvertidaEnPedido()) {
            throw new ReglaDeNegocioException(
                    "Esta cotización ya fue convertida en un pedido anteriormente"
            );
        }

        CategoriaProducto categoria = categoriaProductoRepository.findByNombre("Otros")
                .orElseGet(() -> categoriaProductoRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new RecursoNoEncontradoException(
                                "No existe ninguna categoría de producto configurada"
                        )));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        String numeroPedido = numeracionUtil.generarNumeroPedido();

        Pedido pedido = Pedido.builder()
                .numeroPedido(numeroPedido)
                .cotizacion(cotizacion)
                .cliente(cotizacion.getCliente())
                .categoriaProducto(categoria)
                .descripcion(cotizacion.getDescripcionProducto())
                .especificaciones(construirEspecificaciones(cotizacion))
                .cantidad(cotizacion.getCantidad())
                .precioVenta(cotizacion.getTotal())
                .fechaIngreso(LocalDate.now())
                .fechaEntregaComprometida(LocalDate.now().plusDays(7))
                .estado(EstadoPedido.PENDIENTE)
                .estadoPago(EstadoPagoPedido.SIN_ADELANTO)
                .costoTotal(BigDecimal.ZERO)
                .creadoPor(usuario)
                .build();

        Pedido guardado = pedidoRepository.save(pedido);

        registrarHistorialEstado(guardado, null, EstadoPedido.PENDIENTE,
                "Pedido generado desde cotización: " + cotizacion.getNumeroCotizacion(), usuario);

        cotizacion.setConvertidaEnPedido(true);
        cotizacionRepository.save(cotizacion);

        auditoriaService.registrar(
                usuarioId,
                AccionAuditoria.CREAR,
                EntidadAuditoria.PEDIDO,
                guardado.getId(),
                "Pedido creado desde cotización: " + cotizacion.getNumeroCotizacion()
        );

        return pedidoMapper.toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public PedidoFichaResponse obtenerFicha(Long id) {
        Pedido pedido = buscarPorId(id);

        return PedidoFichaResponse.builder()
                .datosgenerales(pedidoMapper.toResponse(pedido))
                .resumenFinanciero(pedidoMapper.toResumenFinanciero(pedido))
                .historialEstados(
                        historialEstadoPedidoRepository
                                .findByPedidoIdOrderByCreadoEnAsc(id)
                                .stream()
                                .map(pedidoMapper::toHistorialResponse)
                                .toList()
                )
                .notasInternas(
                        notaInternaPedidoRepository
                                .findByPedidoIdOrderByCreadoEnDesc(id)
                                .stream()
                                .map(pedidoMapper::toNotaResponse)
                                .toList()
                )
                .build();
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> buscarConFiltros(
            Long clienteId,
            EstadoPedido estado,
            Long categoriaId,
            LocalDate desdeIngreso,
            LocalDate hastaIngreso,
            LocalDate desdeEntrega,
            LocalDate hastaEntrega,
            Pageable pageable
    ) {
        return pedidoRepository
                .buscarConFiltros(clienteId, estado, categoriaId,
                        desdeIngreso, hastaIngreso, desdeEntrega, hastaEntrega, pageable)
                .map(pedidoMapper::toResponse);
    }

    @Transactional
    public PedidoResponse actualizar(Long id, ActualizarPedidoRequest request, Long usuarioId) {
        Pedido pedido = buscarPorId(id);

        if (pedido.getEstado() == EstadoPedido.FACTURADO
                || pedido.getEstado() == EstadoPedido.ANULADO) {
            throw new ReglaDeNegocioException(
                    "No se puede editar un pedido en estado: " + pedido.getEstado()
            );
        }

        CategoriaProducto categoria = categoriaProductoRepository.findById(request.getCategoriaProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Categoría no encontrada con id: " + request.getCategoriaProductoId()
                ));

        BigDecimal precioAnterior = pedido.getPrecioVenta();

        pedido.setCategoriaProducto(categoria);
        pedido.setDescripcion(request.getDescripcion());
        pedido.setEspecificaciones(request.getEspecificaciones());
        pedido.setCantidad(request.getCantidad());
        pedido.setPrecioVenta(request.getPrecioVenta());
        pedido.setFechaEntregaComprometida(request.getFechaEntregaComprometida());

        Pedido guardado = pedidoRepository.save(pedido);

        String detalle = "Pedido actualizado: " + pedido.getNumeroPedido();
        if (precioAnterior.compareTo(request.getPrecioVenta()) != 0) {
            detalle += " | Precio anterior: " + precioAnterior + " → Precio nuevo: " + request.getPrecioVenta();
        }

        auditoriaService.registrar(
                usuarioId,
                AccionAuditoria.EDITAR,
                EntidadAuditoria.PEDIDO,
                guardado.getId(),
                detalle
        );

        return pedidoMapper.toResponse(guardado);
    }

    @Transactional
    public PedidoResponse cambiarEstado(Long id, CambiarEstadoPedidoRequest request, Long usuarioId) {
        Pedido pedido = buscarPorId(id);
        EstadoPedido estadoActual = pedido.getEstado();
        EstadoPedido estadoDestino = request.getEstadoDestino();

        validarTransicion(estadoActual, estadoDestino);
        validarCondicionesTransicion(pedido, estadoDestino, request.getJustificacion());

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        pedido.setEstado(estadoDestino);
        Pedido guardado = pedidoRepository.save(pedido);

        registrarHistorialEstado(guardado, estadoActual, estadoDestino, request.getJustificacion(), usuario);

        auditoriaService.registrar(
                usuarioId,
                AccionAuditoria.CAMBIO_ESTADO,
                EntidadAuditoria.PEDIDO,
                guardado.getId(),
                "Estado: " + estadoActual + " → " + estadoDestino
        );

        return pedidoMapper.toResponse(guardado);
    }

    @Transactional
    public NotaInternaPedidoResponse agregarNota(Long id, CrearNotaInternaRequest request, Long usuarioId) {
        Pedido pedido = buscarPorId(id);

        if (pedido.getEstado() == EstadoPedido.ANULADO) {
            throw new ReglaDeNegocioException("No se pueden agregar notas a un pedido anulado");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        NotaInternaPedido nota = NotaInternaPedido.builder()
                .pedido(pedido)
                .contenido(request.getContenido())
                .usuario(usuario)
                .build();

        NotaInternaPedido guardada = notaInternaPedidoRepository.save(nota);
        return pedidoMapper.toNotaResponse(guardada);
    }

    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Pedido no encontrado con id: " + id
                ));
    }

    private void validarTransicion(EstadoPedido actual, EstadoPedido destino) {
        Set<EstadoPedido> permitidos = TRANSICIONES_PERMITIDAS.getOrDefault(actual, Set.of());
        if (!permitidos.contains(destino)) {
            throw new EstadoInvalidoException(actual.name(), destino.name());
        }
    }

    private void validarCondicionesTransicion(
            Pedido pedido,
            EstadoPedido destino,
            String justificacion
    ) {
        switch (destino) {
            case EN_PRODUCCION -> {
                if (pedido.getEstadoPago() == EstadoPagoPedido.SIN_ADELANTO) {
                    throw new ReglaDeNegocioException(
                            "El pedido no puede pasar a EN_PRODUCCION sin registrar el adelanto"
                    );
                }
            }
            case ANULADO -> {
                if (justificacion == null || justificacion.isBlank()) {
                    throw new ReglaDeNegocioException(
                            "La justificación es obligatoria para anular un pedido"
                    );
                }
                if (pedido.getEstado() == EstadoPedido.FACTURADO) {
                    throw new ReglaDeNegocioException(
                            "No se puede anular un pedido que ya fue facturado"
                    );
                }
            }
            default -> {
            }
        }
    }

    private void registrarHistorialEstado(
            Pedido pedido,
            EstadoPedido estadoAnterior,
            EstadoPedido estadoNuevo,
            String justificacion,
            Usuario usuario
    ) {
        HistorialEstadoPedido historial = HistorialEstadoPedido.builder()
                .pedido(pedido)
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(estadoNuevo)
                .justificacion(justificacion)
                .usuario(usuario)
                .build();
        historialEstadoPedidoRepository.save(historial);
    }

    private String construirEspecificaciones(Cotizacion cotizacion) {
        StringBuilder sb = new StringBuilder();
        if (cotizacion.getMaterial() != null) sb.append("Material: ").append(cotizacion.getMaterial()).append(" | ");
        if (cotizacion.getDimensiones() != null) sb.append("Dimensiones: ").append(cotizacion.getDimensiones()).append(" | ");
        if (cotizacion.getTipoImpresion() != null) sb.append("Tipo de impresión: ").append(cotizacion.getTipoImpresion()).append(" | ");
        if (cotizacion.getAcabados() != null) sb.append("Acabados: ").append(cotizacion.getAcabados());
        return sb.toString().isBlank() ? "Ver descripción del producto" : sb.toString();
    }
}