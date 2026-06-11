package com.jmcavel.sigcav.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class PedidoResumenResponse {

    private Long id;
    private String numeroPedido;
    private String descripcion;
    private Integer cantidad;
    private BigDecimal precioVenta;
    private LocalDate fechaIngreso;
    private LocalDate fechaEntregaComprometida;
    private String estado;
    private String estadoPago;
}