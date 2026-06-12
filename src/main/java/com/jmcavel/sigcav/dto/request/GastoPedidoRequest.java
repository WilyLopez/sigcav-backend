package com.jmcavel.sigcav.dto.request;

import com.jmcavel.sigcav.enums.TipoGasto;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GastoPedidoRequest {

    @NotNull(message = "El tipo de gasto es obligatorio")
    private TipoGasto tipoGasto;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 300)
    private String descripcion;

    private Long proveedorId;

    @NotNull(message = "La fecha del gasto es obligatoria")
    private LocalDate fechaGasto;

    @NotNull
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @Size(max = 100)
    private String numeroComprobanteProveedor;

    private String observaciones;
}