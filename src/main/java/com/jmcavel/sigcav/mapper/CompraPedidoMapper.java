package com.jmcavel.sigcav.mapper;

import com.jmcavel.sigcav.dto.response.CompraPedidoResponse;
import com.jmcavel.sigcav.entity.CompraPedido;
import org.springframework.stereotype.Component;

@Component
public class CompraPedidoMapper {

    public CompraPedidoResponse toResponse(CompraPedido compraPedido) {
        return CompraPedidoResponse.builder()
                .id(compraPedido.getId())
                .compraId(compraPedido.getCompra().getId())
                .pedidoId(compraPedido.getPedido().getId())
                .pedidoNumero(compraPedido.getPedido().getNumeroPedido())
                .montoAsignado(compraPedido.getMontoAsignado())
                .creadoEn(compraPedido.getCreadoEn())
                .build();
    }
}