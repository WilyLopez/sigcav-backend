package com.jmcavel.sigcav.mapper;

import com.jmcavel.sigcav.dto.response.AnulacionComprobanteResponse;
import com.jmcavel.sigcav.dto.response.ComprobanteResponse;
import com.jmcavel.sigcav.entity.AnulacionComprobante;
import com.jmcavel.sigcav.entity.Comprobante;
import org.springframework.stereotype.Component;

@Component
public class ComprobanteMapper {

    public ComprobanteResponse toResponse(Comprobante c) {
        return ComprobanteResponse.builder()
                .id(c.getId())
                .pedidoId(c.getPedido().getId())
                .numeroPedido(c.getPedido().getNumeroPedido())
                .tipoComprobante(c.getTipoComprobante())
                .serie(c.getSerie())
                .correlativo(c.getCorrelativo())
                .numeroCompleto(c.getNumeroCompleto())
                .fechaEmision(c.getFechaEmision())
                .clienteId(c.getCliente().getId())
                .clienteNombreRazonSocial(c.getCliente().getNombreRazonSocial())
                .clienteNumeroDocumento(c.getCliente().getNumeroDocumento())
                .descripcionServicio(c.getDescripcionServicio())
                .subtotal(c.getSubtotal())
                .igvPorcentaje(c.getIgvPorcentaje())
                .igvMonto(c.getIgvMonto())
                .total(c.getTotal())
                .formaPago(c.getFormaPago())
                .anulado(c.isAnulado())
                .emitidoPorNombre(c.getEmitidoPor() != null
                        ? c.getEmitidoPor().getNombreCompleto() : null)
                .creadoEn(c.getCreadoEn())
                .build();
    }

    public AnulacionComprobanteResponse toAnulacionResponse(AnulacionComprobante a) {
        return AnulacionComprobanteResponse.builder()
                .id(a.getId())
                .comprobanteId(a.getComprobante().getId())
                .numeroCompleto(a.getComprobante().getNumeroCompleto())
                .justificacion(a.getJustificacion())
                .usuarioNombre(a.getUsuario() != null
                        ? a.getUsuario().getNombreCompleto() : null)
                .creadoEn(a.getCreadoEn())
                .build();
    }
}