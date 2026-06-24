package com.jmcavel.sigcav.service;

import com.jmcavel.sigcav.dto.request.AnulacionComprobanteRequest;
import com.jmcavel.sigcav.dto.request.ComprobanteRequest;
import com.jmcavel.sigcav.dto.response.AnulacionComprobanteResponse;
import com.jmcavel.sigcav.dto.response.ComprobanteResponse;
import com.jmcavel.sigcav.entity.*;
import com.jmcavel.sigcav.enums.EstadoPedido;
import com.jmcavel.sigcav.enums.TipoComprobante;
import com.jmcavel.sigcav.exception.*;
import com.jmcavel.sigcav.mapper.ComprobanteMapper;
import com.jmcavel.sigcav.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComprobanteService {

    private static final BigDecimal IGV = new BigDecimal("0.18");
    private static final BigDecimal IGV_PORCENTAJE = new BigDecimal("18.00");
    private static final BigDecimal CERO = BigDecimal.ZERO;

    private final ComprobanteRepository comprobanteRepository;
    private final AnulacionComprobanteRepository anulacionComprobanteRepository;
    private final SerieComprobanteRepository serieComprobanteRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ComprobanteMapper comprobanteMapper;

    @Transactional
    public ComprobanteResponse emitirComprobante(ComprobanteRequest request, Long usuarioId) {
        Pedido pedido = pedidoRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado"));

        validarEstadoParaEmision(pedido);

        if (comprobanteRepository.existsByPedidoId(pedido.getId())) {
            throw new ComprobanteYaExisteException("El pedido ya tiene un comprobante emitido");
        }

        if (request.getTipoComprobante() == TipoComprobante.FACTURA) {
            if (!"RUC".equals(pedido.getCliente().getTipoDocumento())) {
                throw new IllegalArgumentException("La factura solo puede emitirse a clientes con RUC");
            }
        }

        SerieComprobante serie = serieComprobanteRepository
                .findByTipoComprobanteWithLock(request.getTipoComprobante())
                .orElseThrow(() -> new RecursoNoEncontradoException("Serie de comprobante no configurada"));

        int nuevoCorrelativo = serie.getUltimoCorrelativo() + 1;
        serie.setUltimoCorrelativo(nuevoCorrelativo);
        serieComprobanteRepository.save(serie);

        String numeroCompleto = serie.getSerie() + "-" + String.format("%08d", nuevoCorrelativo);

        BigDecimal precioVenta = pedido.getPrecioVenta();
        BigDecimal subtotal;
        BigDecimal igvMonto;
        BigDecimal igvPorcentaje;
        BigDecimal total;

        if (request.getTipoComprobante() == TipoComprobante.FACTURA) {
            subtotal = precioVenta.divide(BigDecimal.ONE.add(IGV), 2, RoundingMode.HALF_UP);
            igvMonto = precioVenta.subtract(subtotal).setScale(2, RoundingMode.HALF_UP);
            igvPorcentaje = IGV_PORCENTAJE;
            total = precioVenta.setScale(2, RoundingMode.HALF_UP);
        } else {
            subtotal = precioVenta.setScale(2, RoundingMode.HALF_UP);
            igvMonto = CERO.setScale(2);
            igvPorcentaje = CERO.setScale(2);
            total = precioVenta.setScale(2, RoundingMode.HALF_UP);
        }

        Usuario emitidoPor = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Comprobante comprobante = Comprobante.builder()
                .pedido(pedido)
                .tipoComprobante(request.getTipoComprobante())
                .serie(serie.getSerie())
                .correlativo(nuevoCorrelativo)
                .numeroCompleto(numeroCompleto)
                .fechaEmision(LocalDate.now())
                .cliente(pedido.getCliente())
                .descripcionServicio(request.getDescripcionServicio())
                .subtotal(subtotal)
                .igvPorcentaje(igvPorcentaje)
                .igvMonto(igvMonto)
                .total(total)
                .formaPago(request.getFormaPago())
                .anulado(false)
                .emitidoPor(emitidoPor)
                .build();

        comprobanteRepository.save(comprobante);

        pedido.setEstado(EstadoPedido.FACTURADO);
        pedidoRepository.save(pedido);

        return comprobanteMapper.toResponse(comprobante);
    }

    @Transactional
    public AnulacionComprobanteResponse anularComprobante(AnulacionComprobanteRequest request, Long usuarioId) {
        Comprobante comprobante = comprobanteRepository.findById(request.getComprobanteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Comprobante no encontrado"));

        if (comprobante.isAnulado()) {
            throw new ComprobanteYaAnuladoException("El comprobante ya fue anulado");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        comprobante.setAnulado(true);
        comprobanteRepository.save(comprobante);

        AnulacionComprobante anulacion = AnulacionComprobante.builder()
                .comprobante(comprobante)
                .justificacion(request.getJustificacion())
                .usuario(usuario)
                .build();

        anulacionComprobanteRepository.save(anulacion);
        return comprobanteMapper.toAnulacionResponse(anulacion);
    }

    @Transactional(readOnly = true)
    public ComprobanteResponse obtenerPorId(Long id) {
        Comprobante comprobante = comprobanteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Comprobante no encontrado"));
        return comprobanteMapper.toResponse(comprobante);
    }

    @Transactional(readOnly = true)
    public ComprobanteResponse obtenerPorPedido(Long pedidoId) {
        Comprobante comprobante = comprobanteRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("El pedido no tiene comprobante emitido"));
        return comprobanteMapper.toResponse(comprobante);
    }

    @Transactional(readOnly = true)
    public List<ComprobanteResponse> listarPorFiltros(LocalDate desde, LocalDate hasta, TipoComprobante tipo) {
        return comprobanteRepository.findByFiltros(desde, hasta, tipo)
                .stream()
                .map(comprobanteMapper::toResponse)
                .toList();
    }

    private void validarEstadoParaEmision(Pedido pedido) {
        if (pedido.getEstado() != EstadoPedido.ENTREGADO
                && pedido.getEstado() != EstadoPedido.LISTO_ENTREGA) {
            throw new EstadoPedidoInvalidoException(
                    "El pedido debe estar en LISTO_ENTREGA o ENTREGADO para emitir comprobante");
        }
    }
}