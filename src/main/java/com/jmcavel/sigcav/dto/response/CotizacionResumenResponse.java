package com.jmcavel.sigcav.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class CotizacionResumenResponse {

    private Long id;
    private String numeroCotizacion;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private String descripcionProducto;
    private BigDecimal total;
    private String estado;
}