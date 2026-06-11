package com.jmcavel.sigcav.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class ComprobanteResumenResponse {

    private Long id;
    private String tipoComprobante;
    private String numeroCompleto;
    private LocalDate fechaEmision;
    private BigDecimal total;
    private Boolean anulado;
}