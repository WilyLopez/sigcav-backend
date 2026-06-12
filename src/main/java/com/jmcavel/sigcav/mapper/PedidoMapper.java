package com.jmcavel.sigcav.mapper;

import com.jmcavel.sigcav.dto.response.HistorialEstadoPedidoResponse;
import com.jmcavel.sigcav.dto.response.NotaInternaPedidoResponse;
import com.jmcavel.sigcav.dto.response.PedidoResponse;
import com.jmcavel.sigcav.dto.response.ResumenFinancieroResponse;
import com.jmcavel.sigcav.entity.HistorialEstadoPedido;
import com.jmcavel.sigcav.entity.NotaInternaPedido;
import com.jmcavel.sigcav.entity.Pedido;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Component
public class PedidoMapper {

    private static final int DIAS_ALERTA_ENTREGA = 2;

    public PedidoResponse toResponse(Pedido pedido) {
        BigDecimal gananciaBruta = calcularGananciaBruta(pedido);
        BigDecimal margen = calcularMargen(pedido, gananciaBruta);
        String semaforo = calcularSemaforo(margen);
        boolean alertaEntrega = calcularAlertaEntrega(pedido);

        return PedidoResponse.builder()
                .id(pedido.getId())
                .numeroPedido(pedido.getNumeroPedido())
                .clienteId(pedido.getCliente().getId())
                .clienteNombre(pedido.getCliente().getNombreRazonSocial())
                .clienteNumeroDocumento(pedido.getCliente().getNumeroDocumento())
                .categoriaProductoId(pedido.getCategoriaProducto().getId())
                .categoriaProductoNombre(pedido.getCategoriaProducto().getNombre())
                .descripcion(pedido.getDescripcion())
                .especificaciones(pedido.getEspecificaciones())
                .cantidad(pedido.getCantidad())
                .precioVenta(pedido.getPrecioVenta())
                .fechaIngreso(pedido.getFechaIngreso())
                .fechaEntregaComprometida(pedido.getFechaEntregaComprometida())
                .estado(pedido.getEstado())
                .estadoPago(pedido.getEstadoPago())
                .costoTotal(pedido.getCostoTotal())
                .gananciaBruta(gananciaBruta)
                .margenGananciaPorcentaje(margen)
                .semaforoColor(semaforo)
                .alertaEntregaProxima(alertaEntrega)
                .creadoPorNombre(pedido.getCreadoPor() != null
                        ? pedido.getCreadoPor().getNombreCompleto()
                        : null)
                .creadoEn(pedido.getCreadoEn())
                .actualizadoEn(pedido.getActualizadoEn())
                .build();
    }

    public ResumenFinancieroResponse toResumenFinanciero(Pedido pedido) {
        BigDecimal gananciaBruta = calcularGananciaBruta(pedido);
        BigDecimal margen = calcularMargen(pedido, gananciaBruta);
        String semaforo = calcularSemaforo(margen);

        return ResumenFinancieroResponse.builder()
                .precioVenta(pedido.getPrecioVenta())
                .costoTotal(pedido.getCostoTotal())
                .gananciaBruta(gananciaBruta)
                .margenGananciaPorcentaje(margen)
                .semaforoColor(semaforo)
                .build();
    }

    public HistorialEstadoPedidoResponse toHistorialResponse(HistorialEstadoPedido historial) {
        return HistorialEstadoPedidoResponse.builder()
                .id(historial.getId())
                .estadoAnterior(historial.getEstadoAnterior())
                .estadoNuevo(historial.getEstadoNuevo())
                .justificacion(historial.getJustificacion())
                .usuarioNombre(historial.getUsuario() != null
                        ? historial.getUsuario().getNombreCompleto()
                        : "sistema")
                .creadoEn(historial.getCreadoEn())
                .build();
    }

    public NotaInternaPedidoResponse toNotaResponse(NotaInternaPedido nota) {
        return NotaInternaPedidoResponse.builder()
                .id(nota.getId())
                .contenido(nota.getContenido())
                .usuarioNombre(nota.getUsuario() != null
                        ? nota.getUsuario().getNombreCompleto()
                        : "sistema")
                .creadoEn(nota.getCreadoEn())
                .build();
    }

    private BigDecimal calcularGananciaBruta(Pedido pedido) {
        return pedido.getPrecioVenta().subtract(pedido.getCostoTotal());
    }

    private BigDecimal calcularMargen(Pedido pedido, BigDecimal gananciaBruta) {
        if (pedido.getPrecioVenta().compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return gananciaBruta
                .divide(pedido.getPrecioVenta(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private String calcularSemaforo(BigDecimal margen) {
        if (margen == null) {
            return "GRIS";
        }
        if (margen.compareTo(BigDecimal.valueOf(30)) >= 0) {
            return "VERDE";
        }
        if (margen.compareTo(BigDecimal.valueOf(15)) >= 0) {
            return "AMARILLO";
        }
        return "ROJO";
    }

    private boolean calcularAlertaEntrega(Pedido pedido) {
        LocalDate limite = LocalDate.now().plusDays(DIAS_ALERTA_ENTREGA);
        boolean estadoSinAlerta = pedido.getEstado().name().equals("LISTO_ENTREGA")
                || pedido.getEstado().name().equals("ENTREGADO")
                || pedido.getEstado().name().equals("FACTURADO")
                || pedido.getEstado().name().equals("ANULADO");
        return !estadoSinAlerta && !pedido.getFechaEntregaComprometida().isAfter(limite);
    }
}