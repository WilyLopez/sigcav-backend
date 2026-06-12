package com.jmcavel.sigcav.dto.response;

import com.jmcavel.sigcav.enums.EstadoCotizacion;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionResponse {

    private Long id;
    private String numeroCotizacion;
    private Long clienteId;
    private String clienteNombre;
    private String clienteNumeroDocumento;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private String descripcionProducto;
    private String tipoImpresion;
    private String material;
    private String dimensiones;
    private String acabados;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuentoPorcentaje;
    private BigDecimal recargoPorcentaje;
    private BigDecimal subtotal;
    private boolean aplicaIgv;
    private BigDecimal igvMonto;
    private BigDecimal total;
    private String tiempoEntregaEstimado;
    private String condicionesPago;
    private String observaciones;
    private EstadoCotizacion estado;
    private boolean convertidaEnPedido;
    private String creadoPorNombre;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private boolean alertaVencimientoProximo;
}