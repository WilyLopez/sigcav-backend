package com.jmcavel.sigcav.mapper;

import com.jmcavel.sigcav.dto.response.ComprobanteResumenResponse;
import com.jmcavel.sigcav.dto.response.CotizacionResumenResponse;
import com.jmcavel.sigcav.dto.response.PedidoResumenResponse;
import com.jmcavel.sigcav.entity.Comprobante;
import com.jmcavel.sigcav.entity.Cotizacion;
import com.jmcavel.sigcav.entity.Pedido;
import org.springframework.stereotype.Component;

@Component
public class ClienteFichaMapper {

    public CotizacionResumenResponse toCotizacionResumen(Cotizacion entidad) {
        return CotizacionResumenResponse.builder()
                .id(entidad.getId())
                .numeroCotizacion(entidad.getNumeroCotizacion())
                .fechaEmision(entidad.getFechaEmision())
                .fechaVencimiento(entidad.getFechaVencimiento())
                .descripcionProducto(entidad.getDescripcionProducto())
                .total(entidad.getTotal())
                .estado(entidad.getEstado().name())
                .build();
    }

    public PedidoResumenResponse toPedidoResumen(Pedido entidad) {
        return PedidoResumenResponse.builder()
                .id(entidad.getId())
                .numeroPedido(entidad.getNumeroPedido())
                .descripcion(entidad.getDescripcion())
                .cantidad(entidad.getCantidad())
                .precioVenta(entidad.getPrecioVenta())
                .fechaIngreso(entidad.getFechaIngreso())
                .fechaEntregaComprometida(entidad.getFechaEntregaComprometida())
                .estado(entidad.getEstado().name())
                .estadoPago(entidad.getEstadoPago().name())
                .build();
    }

    public ComprobanteResumenResponse toComprobanteResumen(Comprobante entidad) {
        return ComprobanteResumenResponse.builder()
                .id(entidad.getId())
                .tipoComprobante(entidad.getTipoComprobante().name())
                .numeroCompleto(entidad.getNumeroCompleto())
                .fechaEmision(entidad.getFechaEmision())
                .total(entidad.getTotal())
                .anulado(entidad.getAnulado())
                .build();
    }
}