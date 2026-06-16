package com.jmcavel.sigcav.service;

import com.jmcavel.sigcav.dto.request.ActualizarCotizacionRequest;
import com.jmcavel.sigcav.dto.request.CambiarEstadoCotizacionRequest;
import com.jmcavel.sigcav.dto.request.CrearCotizacionRequest;
import com.jmcavel.sigcav.dto.response.CotizacionResponse;
import com.jmcavel.sigcav.entity.Cliente;
import com.jmcavel.sigcav.entity.Cotizacion;
import com.jmcavel.sigcav.entity.Usuario;
import com.jmcavel.sigcav.enums.AccionAuditoria;
import com.jmcavel.sigcav.enums.EntidadAuditoria;
import com.jmcavel.sigcav.enums.EstadoCotizacion;
import com.jmcavel.sigcav.exception.EstadoInvalidoException;
import com.jmcavel.sigcav.exception.RecursoNoEncontradoException;
import com.jmcavel.sigcav.exception.ReglaDeNegocioException;
import com.jmcavel.sigcav.mapper.CotizacionMapper;
import com.jmcavel.sigcav.repository.ClienteRepository;
import com.jmcavel.sigcav.repository.CotizacionRepository;
import com.jmcavel.sigcav.repository.UsuarioRepository;
import com.jmcavel.sigcav.util.NumeracionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final CotizacionMapper cotizacionMapper;
    private final NumeracionUtil numeracionUtil;
    private final AuditoriaService auditoriaService;
    private final ParametroSistemaService parametroSistemaService;

    private static final Set<EstadoCotizacion> ESTADOS_EDITABLES = Set.of(
            EstadoCotizacion.BORRADOR,
            EstadoCotizacion.ENVIADA
    );

    private static final Set<EstadoCotizacion> TRANSICIONES_VALIDAS_MANUAL = Set.of(
            EstadoCotizacion.ENVIADA,
            EstadoCotizacion.APROBADA,
            EstadoCotizacion.RECHAZADA
    );

    @Transactional
    public CotizacionResponse crear(CrearCotizacionRequest request, Long usuarioId) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con id: " + request.getClienteId()
                ));

        if (!cliente.getActivo()) {
            throw new ReglaDeNegocioException("No se puede crear una cotización para un cliente inactivo");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        String numeroCotizacion = numeracionUtil.generarNumeroCotizacion();

        BigDecimal igvPorcentaje = BigDecimal.valueOf(
                parametroSistemaService.obtenerValorDecimal("igv_porcentaje")
        );

        BigDecimal subtotal = calcularSubtotal(
                request.getPrecioUnitario(),
                request.getCantidad(),
                request.getDescuentoPorcentaje(),
                request.getRecargoPorcentaje()
        );

        BigDecimal igvMonto = request.isAplicaIgv()
                ? subtotal.multiply(igvPorcentaje).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal total = subtotal.add(igvMonto);

        int diasVencimiento = parametroSistemaService.obtenerValorEntero("dias_vencimiento_cotizacion");
        LocalDate fechaVencimiento = request.getFechaVencimiento() != null
                ? request.getFechaVencimiento()
                : LocalDate.now().plusDays(diasVencimiento);

        Cotizacion cotizacion = Cotizacion.builder()
                .numeroCotizacion(numeroCotizacion)
                .cliente(cliente)
                .fechaEmision(LocalDate.now())
                .fechaVencimiento(fechaVencimiento)
                .descripcionProducto(request.getDescripcionProducto())
                .tipoImpresion(request.getTipoImpresion())
                .material(request.getMaterial())
                .dimensiones(request.getDimensiones())
                .acabados(request.getAcabados())
                .cantidad(request.getCantidad())
                .precioUnitario(request.getPrecioUnitario())
                .descuentoPorcentaje(request.getDescuentoPorcentaje() != null
                        ? request.getDescuentoPorcentaje() : BigDecimal.ZERO)
                .recargoPorcentaje(request.getRecargoPorcentaje() != null
                        ? request.getRecargoPorcentaje() : BigDecimal.ZERO)
                .subtotal(subtotal)
                .aplicaIgv(request.isAplicaIgv())
                .igvMonto(igvMonto)
                .total(total)
                .tiempoEntregaEstimado(request.getTiempoEntregaEstimado())
                .condicionesPago(request.getCondicionesPago())
                .observaciones(request.getObservaciones())
                .estado(EstadoCotizacion.BORRADOR)
                .convertidaEnPedido(false)
                .creadoPor(usuario)
                .build();

        Cotizacion guardada = cotizacionRepository.save(cotizacion);

        auditoriaService.registrar(
                usuarioId,
                AccionAuditoria.CREAR,
                EntidadAuditoria.COTIZACION,
                guardada.getId(),
                "Cotización creada: " + numeroCotizacion
        );

        return cotizacionMapper.toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public CotizacionResponse obtenerPorId(Long id) {
        return cotizacionMapper.toResponse(buscarPorId(id));
    }

    @Transactional(readOnly = true)
    public Page<CotizacionResponse> buscarConFiltros(
            Long clienteId,
            EstadoCotizacion estado,
            LocalDate desde,
            LocalDate hasta,
            Pageable pageable
    ) {
        return cotizacionRepository
                .buscarConFiltros(clienteId, estado, desde, hasta, pageable)
                .map(cotizacionMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<CotizacionResponse> listarPorCliente(Long clienteId) {
        return cotizacionRepository
                .findAllByClienteIdOrderByFechaEmisionDesc(clienteId)
                .stream()
                .map(cotizacionMapper::toResponse)
                .toList();
    }

    @Transactional
    public CotizacionResponse actualizar(Long id, ActualizarCotizacionRequest request, Long usuarioId) {
        Cotizacion cotizacion = buscarPorId(id);

        if (!ESTADOS_EDITABLES.contains(cotizacion.getEstado())) {
            throw new ReglaDeNegocioException(
                    "Solo se pueden editar cotizaciones en estado BORRADOR o ENVIADA. Estado actual: "
                            + cotizacion.getEstado()
            );
        }

        BigDecimal igvPorcentaje = BigDecimal.valueOf(
                parametroSistemaService.obtenerValorDecimal("igv_porcentaje")
        );

        BigDecimal subtotal = calcularSubtotal(
                request.getPrecioUnitario(),
                request.getCantidad(),
                request.getDescuentoPorcentaje(),
                request.getRecargoPorcentaje()
        );

        BigDecimal igvMonto = request.isAplicaIgv()
                ? subtotal.multiply(igvPorcentaje).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        cotizacion.setFechaVencimiento(request.getFechaVencimiento());
        cotizacion.setDescripcionProducto(request.getDescripcionProducto());
        cotizacion.setTipoImpresion(request.getTipoImpresion());
        cotizacion.setMaterial(request.getMaterial());
        cotizacion.setDimensiones(request.getDimensiones());
        cotizacion.setAcabados(request.getAcabados());
        cotizacion.setCantidad(request.getCantidad());
        cotizacion.setPrecioUnitario(request.getPrecioUnitario());
        cotizacion.setDescuentoPorcentaje(request.getDescuentoPorcentaje());
        cotizacion.setRecargoPorcentaje(request.getRecargoPorcentaje());
        cotizacion.setSubtotal(subtotal);
        cotizacion.setAplicaIgv(request.isAplicaIgv());
        cotizacion.setIgvMonto(igvMonto);
        cotizacion.setTotal(subtotal.add(igvMonto));
        cotizacion.setTiempoEntregaEstimado(request.getTiempoEntregaEstimado());
        cotizacion.setCondicionesPago(request.getCondicionesPago());
        cotizacion.setObservaciones(request.getObservaciones());

        Cotizacion guardada = cotizacionRepository.save(cotizacion);

        auditoriaService.registrar(
                usuarioId,
                AccionAuditoria.EDITAR,
                EntidadAuditoria.COTIZACION,
                guardada.getId(),
                "Cotización actualizada: " + guardada.getNumeroCotizacion()
        );

        return cotizacionMapper.toResponse(guardada);
    }

    @Transactional
    public CotizacionResponse cambiarEstado(Long id, CambiarEstadoCotizacionRequest request, Long usuarioId) {
        Cotizacion cotizacion = buscarPorId(id);

        validarTransicionEstado(cotizacion.getEstado(), request.getEstado());

        cotizacion.setEstado(request.getEstado());
        Cotizacion guardada = cotizacionRepository.save(cotizacion);

        auditoriaService.registrar(
                usuarioId,
                AccionAuditoria.CAMBIO_ESTADO,
                EntidadAuditoria.COTIZACION,
                guardada.getId(),
                "Estado cambiado a: " + request.getEstado()
        );

        return cotizacionMapper.toResponse(guardada);
    }

    @Transactional
    public CotizacionResponse reactivarVencida(Long id, LocalDate nuevaFechaVencimiento, Long usuarioId) {
        Cotizacion cotizacion = buscarPorId(id);

        if (cotizacion.getEstado() != EstadoCotizacion.VENCIDA) {
            throw new ReglaDeNegocioException(
                    "Solo se pueden reactivar cotizaciones en estado VENCIDA"
            );
        }

        if (!nuevaFechaVencimiento.isAfter(LocalDate.now())) {
            throw new ReglaDeNegocioException(
                    "La nueva fecha de vencimiento debe ser una fecha futura"
            );
        }

        cotizacion.setFechaVencimiento(nuevaFechaVencimiento);
        cotizacion.setEstado(EstadoCotizacion.ENVIADA);
        Cotizacion guardada = cotizacionRepository.save(cotizacion);

        auditoriaService.registrar(
                usuarioId,
                AccionAuditoria.EDITAR,
                EntidadAuditoria.COTIZACION,
                guardada.getId(),
                "Cotización reactivada con nueva fecha de vencimiento: " + nuevaFechaVencimiento
        );

        return cotizacionMapper.toResponse(guardada);
    }

    @Transactional
    public CotizacionResponse duplicar(Long id, Long usuarioId) {
        Cotizacion original = buscarPorId(id);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        int diasVencimiento = parametroSistemaService.obtenerValorEntero("dias_vencimiento_cotizacion");

        Cotizacion copia = Cotizacion.builder()
                .numeroCotizacion(numeracionUtil.generarNumeroCotizacion())
                .cliente(original.getCliente())
                .fechaEmision(LocalDate.now())
                .fechaVencimiento(LocalDate.now().plusDays(diasVencimiento))
                .descripcionProducto(original.getDescripcionProducto())
                .tipoImpresion(original.getTipoImpresion())
                .material(original.getMaterial())
                .dimensiones(original.getDimensiones())
                .acabados(original.getAcabados())
                .cantidad(original.getCantidad())
                .precioUnitario(original.getPrecioUnitario())
                .descuentoPorcentaje(original.getDescuentoPorcentaje())
                .recargoPorcentaje(original.getRecargoPorcentaje())
                .subtotal(original.getSubtotal())
                .aplicaIgv(original.isAplicaIgv())
                .igvMonto(original.getIgvMonto())
                .total(original.getTotal())
                .tiempoEntregaEstimado(original.getTiempoEntregaEstimado())
                .condicionesPago(original.getCondicionesPago())
                .observaciones(original.getObservaciones())
                .estado(EstadoCotizacion.BORRADOR)
                .convertidaEnPedido(false)
                .creadoPor(usuario)
                .build();

        Cotizacion guardada = cotizacionRepository.save(copia);

        auditoriaService.registrar(
                usuarioId,
                AccionAuditoria.CREAR,
                EntidadAuditoria.COTIZACION,
                guardada.getId(),
                "Cotización duplicada desde: " + original.getNumeroCotizacion()
        );

        return cotizacionMapper.toResponse(guardada);
    }

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void actualizarCotizacionesVencidas() {
        cotizacionRepository.actualizarCotizacionesVencidas(LocalDate.now());
    }

    public Cotizacion buscarPorId(Long id) {
        return cotizacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cotización no encontrada con id: " + id
                ));
    }

    private void validarTransicionEstado(EstadoCotizacion actual, EstadoCotizacion destino) {
        if (actual == EstadoCotizacion.APROBADA) {
            throw new EstadoInvalidoException("Una cotización aprobada no puede cambiar de estado");
        }
        if (actual == EstadoCotizacion.RECHAZADA) {
            throw new EstadoInvalidoException("Una cotización rechazada no puede cambiar de estado");
        }
        if (actual == EstadoCotizacion.VENCIDA && destino != EstadoCotizacion.ENVIADA) {
            throw new EstadoInvalidoException(actual.name(), destino.name());
        }
        if (!TRANSICIONES_VALIDAS_MANUAL.contains(destino)) {
            throw new EstadoInvalidoException(actual.name(), destino.name());
        }
    }

    private BigDecimal calcularSubtotal(
            BigDecimal precioUnitario,
            Integer cantidad,
            BigDecimal descuentoPorcentaje,
            BigDecimal recargoPorcentaje
    ) {
        BigDecimal base = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        BigDecimal descuento = descuentoPorcentaje != null
                ? base.multiply(descuentoPorcentaje).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal recargo = recargoPorcentaje != null
                ? base.multiply(recargoPorcentaje).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        return base.subtract(descuento).add(recargo);
    }
}