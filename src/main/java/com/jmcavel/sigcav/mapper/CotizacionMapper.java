package com.jmcavel.sigcav.mapper;

import com.jmcavel.sigcav.dto.response.CotizacionResponse;
import com.jmcavel.sigcav.entity.Cotizacion;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CotizacionMapper {

    public CotizacionResponse toResponse(Cotizacion cotizacion) {
        boolean vencimientoProximo = cotizacion.getFechaVencimiento() != null
                && !cotizacion.isConvertidaEnPedido()
                && cotizacion.getFechaVencimiento().isBefore(LocalDate.now().plusDays(3));

        return CotizacionResponse.builder()
                .id(cotizacion.getId())
                .numeroCotizacion(cotizacion.getNumeroCotizacion())
                .clienteId(cotizacion.getCliente().getId())
                .clienteNombre(cotizacion.getCliente().getNombreRazonSocial())
                .clienteNumeroDocumento(cotizacion.getCliente().getNumeroDocumento())
                .fechaEmision(cotizacion.getFechaEmision())
                .fechaVencimiento(cotizacion.getFechaVencimiento())
                .descripcionProducto(cotizacion.getDescripcionProducto())
                .tipoImpresion(cotizacion.getTipoImpresion())
                .material(cotizacion.getMaterial())
                .dimensiones(cotizacion.getDimensiones())
                .acabados(cotizacion.getAcabados())
                .cantidad(cotizacion.getCantidad())
                .precioUnitario(cotizacion.getPrecioUnitario())
                .descuentoPorcentaje(cotizacion.getDescuentoPorcentaje())
                .recargoPorcentaje(cotizacion.getRecargoPorcentaje())
                .subtotal(cotizacion.getSubtotal())
                .aplicaIgv(cotizacion.isAplicaIgv())
                .igvMonto(cotizacion.getIgvMonto())
                .total(cotizacion.getTotal())
                .tiempoEntregaEstimado(cotizacion.getTiempoEntregaEstimado())
                .condicionesPago(cotizacion.getCondicionesPago())
                .observaciones(cotizacion.getObservaciones())
                .estado(cotizacion.getEstado())
                .convertidaEnPedido(cotizacion.isConvertidaEnPedido())
                .creadoPorNombre(cotizacion.getCreadoPor() != null
                        ? cotizacion.getCreadoPor().getNombreCompleto()
                        : null)
                .creadoEn(cotizacion.getCreadoEn())
                .actualizadoEn(cotizacion.getActualizadoEn())
                .alertaVencimientoProximo(vencimientoProximo)
                .build();
    }
}