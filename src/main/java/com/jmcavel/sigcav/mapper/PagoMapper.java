package com.jmcavel.sigcav.mapper;

import com.jmcavel.sigcav.dto.response.PagoResponse;
import com.jmcavel.sigcav.entity.Pago;
import org.springframework.stereotype.Component;

@Component
public class PagoMapper {

    public PagoResponse toResponse(Pago pago) {
        return PagoResponse.builder()
                .id(pago.getId())
                .pedidoId(pago.getPedido().getId())
                .numeroPedido(pago.getPedido().getNumeroPedido())
                .tipoPago(pago.getTipoPago())
                .monto(pago.getMonto())
                .fechaPago(pago.getFechaPago())
                .formaPago(pago.getFormaPago())
                .observacion(pago.getObservacion())
                .registradoPorNombre(pago.getRegistradoPor() != null
                        ? pago.getRegistradoPor().getNombreCompleto() : null)
                .creadoEn(pago.getCreadoEn())
                .build();
    }
}