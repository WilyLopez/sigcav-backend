package com.jmcavel.sigcav.dto.response;

import com.jmcavel.sigcav.enums.EstadoPagoPedido;
import com.jmcavel.sigcav.enums.EstadoPedido;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponse {

    private Long id;
    private String numeroPedido;
    private Long clienteId;
    private String clienteNombre;
    private String clienteNumeroDocumento;
    private Long categoriaProductoId;
    private String categoriaProductoNombre;
    private String descripcion;
    private String especificaciones;
    private Integer cantidad;
    private BigDecimal precioVenta;
    private LocalDate fechaIngreso;
    private LocalDate fechaEntregaComprometida;
    private EstadoPedido estado;
    private EstadoPagoPedido estadoPago;
    private BigDecimal costoTotal;
    private BigDecimal gananciaBruta;
    private BigDecimal margenGananciaPorcentaje;
    private String semaforoColor;
    private boolean alertaEntregaProxima;
    private String creadoPorNombre;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}