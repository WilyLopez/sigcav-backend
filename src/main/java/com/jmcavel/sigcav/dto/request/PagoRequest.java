package com.jmcavel.sigcav.dto.request;

import com.jmcavel.sigcav.enums.FormaPago;
import com.jmcavel.sigcav.enums.TipoPago;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoRequest {

    @NotNull
    private Long pedidoId;

    @NotNull
    private TipoPago tipoPago;

    @NotNull
    @DecimalMin(value = "0.01")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal monto;

    @NotNull
    private LocalDate fechaPago;

    @NotNull
    private FormaPago formaPago;

    private String observacion;
}