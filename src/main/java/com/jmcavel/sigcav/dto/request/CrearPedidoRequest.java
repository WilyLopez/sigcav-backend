package com.jmcavel.sigcav.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CrearPedidoRequest {

    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;

    private Long cotizacionId;

    @NotNull(message = "La categoría del producto es obligatoria")
    private Long categoriaProductoId;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, message = "La descripción debe tener al menos 10 caracteres")
    private String descripcion;

    @NotBlank(message = "Las especificaciones son obligatorias")
    private String especificaciones;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a cero")
    private Integer cantidad;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio de venta debe ser mayor a cero")
    private BigDecimal precioVenta;

    @NotNull(message = "La fecha de entrega comprometida es obligatoria")
    private LocalDate fechaEntregaComprometida;
}