package com.jmcavel.sigcav.service;

import com.jmcavel.sigcav.dto.request.NotaCorreccionPagoRequest;
import com.jmcavel.sigcav.dto.request.PagoRequest;
import com.jmcavel.sigcav.dto.response.NotaCorreccionPagoResponse;
import com.jmcavel.sigcav.dto.response.PagoResponse;
import com.jmcavel.sigcav.entity.*;
import com.jmcavel.sigcav.enums.EstadoPago;
import com.jmcavel.sigcav.enums.TipoPago;
import com.jmcavel.sigcav.exception.PagoYaExisteException;
import com.jmcavel.sigcav.exception.ResourceNotFoundException;
import com.jmcavel.sigcav.mapper.NotaCorreccionPagoMapper;
import com.jmcavel.sigcav.mapper.PagoMapper;
import com.jmcavel.sigcav.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final NotaCorreccionPagoRepository notaCorreccionPagoRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PagoMapper pagoMapper;
    private final NotaCorreccionPagoMapper notaCorreccionPagoMapper;

    @Transactional
    public PagoResponse registrarPago(PagoRequest request, Long usuarioId) {
        Pedido pedido = pedidoRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        if (pagoRepository.existsByPedidoIdAndTipoPago(pedido.getId(), request.getTipoPago())) {
            throw new PagoYaExisteException("Ya existe un pago de tipo "
                    + request.getTipoPago() + " para este pedido");
        }

        if (request.getTipoPago() == TipoPago.SALDO
                && !pagoRepository.existsByPedidoIdAndTipoPago(pedido.getId(), TipoPago.ADELANTO)) {
            throw new IllegalStateException("No se puede registrar el saldo sin un adelanto previo");
        }

        Usuario registradoPor = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Pago pago = Pago.builder()
                .pedido(pedido)
                .tipoPago(request.getTipoPago())
                .monto(request.getMonto())
                .fechaPago(request.getFechaPago())
                .formaPago(request.getFormaPago())
                .observacion(request.getObservacion())
                .registradoPor(registradoPor)
                .build();

        pagoRepository.save(pago);
        actualizarEstadoPagoPedido(pedido);
        pedidoRepository.save(pedido);

        return pagoMapper.toResponse(pago);
    }

    @Transactional
    public NotaCorreccionPagoResponse corregirPago(NotaCorreccionPagoRequest request, Long usuarioId) {
        Pago pago = pagoRepository.findById(request.getPagoId())
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        NotaCorreccionPago nota = NotaCorreccionPago.builder()
                .pago(pago)
                .justificacion(request.getJustificacion())
                .usuario(usuario)
                .build();

        notaCorreccionPagoRepository.save(nota);
        return notaCorreccionPagoMapper.toResponse(nota);
    }

    @Transactional(readOnly = true)
    public List<PagoResponse> listarPorPedido(Long pedidoId) {
        return pagoRepository.findByPedidoId(pedidoId)
                .stream()
                .map(pagoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotaCorreccionPagoResponse> listarCorrencionesPorPago(Long pagoId) {
        return notaCorreccionPagoRepository.findByPagoIdOrderByCreadoEnDesc(pagoId)
                .stream()
                .map(notaCorreccionPagoMapper::toResponse)
                .toList();
    }

    private void actualizarEstadoPagoPedido(Pedido pedido) {
        boolean tieneAdelanto = pagoRepository.existsByPedidoIdAndTipoPago(pedido.getId(), TipoPago.ADELANTO);
        boolean tieneSaldo = pagoRepository.existsByPedidoIdAndTipoPago(pedido.getId(), TipoPago.SALDO);

        if (tieneAdelanto && tieneSaldo) {
            pedido.setEstadoPago(EstadoPago.PAGADO_COMPLETAMENTE);
        } else if (tieneAdelanto) {
            pedido.setEstadoPago(EstadoPago.ADELANTO_REGISTRADO);
        } else {
            pedido.setEstadoPago(EstadoPago.SIN_ADELANTO);
        }
    }
}