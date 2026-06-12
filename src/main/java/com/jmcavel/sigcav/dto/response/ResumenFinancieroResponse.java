package com.jmcavel.sigcav.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenFinancieroResponse {

    private BigDecimal precioVenta;
    private BigDecimal costoTotal;
    private BigDecimal gananciaBruta;
    private BigDecimal margenGananciaPorcentaje;
    private String semaforoColor;
}