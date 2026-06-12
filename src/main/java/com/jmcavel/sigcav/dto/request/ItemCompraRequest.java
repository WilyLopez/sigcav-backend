package com.jmcavel.sigcav.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCompraRequest {

    @NotBlank(message = "El nombre del material es obligatorio")
    @Size(max = 200)
    private String nombreMaterial;

    @NotBlank(message = "La unidad de medida es obligatoria")
    @Size(max = 30)
    private String unidadMedida;

    @NotNull
    @DecimalMin(value = "0.0001", message = "La cantidad debe ser mayor a 0")
    private BigDecimal cantidad;

    @NotNull
    @DecimalMin(value = "0.0", message = "El precio unitario no puede ser negativo")
    private BigDecimal precioUnitario;
}