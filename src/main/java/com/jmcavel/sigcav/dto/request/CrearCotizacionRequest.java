package com.jmcavel.sigcav.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CrearCotizacionRequest {

    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Future(message = "La fecha de vencimiento debe ser una fecha futura")
    private LocalDate fechaVencimiento;

    @NotBlank(message = "La descripción del producto es obligatoria")
    private String descripcionProducto;

    private String tipoImpresion;
    private String material;
    private String dimensiones;
    private String acabados;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a cero")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a cero")
    private BigDecimal precioUnitario;

    @DecimalMin(value = "0.00", message = "El descuento no puede ser negativo")
    @DecimalMax(value = "100.00", message = "El descuento no puede superar el 100%")
    private BigDecimal descuentoPorcentaje = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", message = "El recargo no puede ser negativo")
    private BigDecimal recargoPorcentaje = BigDecimal.ZERO;

    private boolean aplicaIgv = false;

    private String tiempoEntregaEstimado;
    private String condicionesPago;
    private String observaciones;
}