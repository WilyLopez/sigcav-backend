package com.jmcavel.sigcav.mapper;

import com.jmcavel.sigcav.dto.response.GastoPedidoResponse;
import com.jmcavel.sigcav.entity.GastoPedido;
import org.springframework.stereotype.Component;

@Component
public class GastoPedidoMapper {

    public GastoPedidoResponse toResponse(GastoPedido gasto) {
        return GastoPedidoResponse.builder()
                .id(gasto.getId())
                .pedidoId(gasto.getPedido().getId())
                .tipoGasto(gasto.getTipoGasto())
                .descripcion(gasto.getDescripcion())
                .proveedorId(gasto.getProveedor() != null ? gasto.getProveedor().getId() : null)
                .proveedorNombre(gasto.getProveedor() != null ? gasto.getProveedor().getNombreRazonSocial() : null)
                .fechaGasto(gasto.getFechaGasto())
                .monto(gasto.getMonto())
                .numeroComprobanteProveedor(gasto.getNumeroComprobanteProveedor())
                .observaciones(gasto.getObservaciones())
                .registradoPorId(gasto.getRegistradoPor() != null ? gasto.getRegistradoPor().getId() : null)
                .creadoEn(gasto.getCreadoEn())
                .actualizadoEn(gasto.getActualizadoEn())
                .build();
    }
}