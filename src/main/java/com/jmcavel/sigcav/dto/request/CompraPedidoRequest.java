package com.jmcavel.sigcav.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraPedidoRequest {

    @NotNull(message = "El pedido es obligatorio")
    private Long pedidoId;

    @NotNull(message = "El monto asignado es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto asignado debe ser mayor a 0")
    private BigDecimal montoAsignado;
}