package com.jmcavel.sigcav.dto.request;

import com.jmcavel.sigcav.enums.TipoComprobanteProveedor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraRequest {

    @NotNull(message = "El proveedor es obligatorio")
    private Long proveedorId;

    @NotNull(message = "La fecha de compra es obligatoria")
    private LocalDate fechaCompra;

    @Size(max = 100, message = "El número de comprobante no puede superar 100 caracteres")
    private String numeroComprobanteProveedor;

    private TipoComprobanteProveedor tipoComprobanteProveedor;

    private String observaciones;

    @NotEmpty(message = "La compra debe tener al menos un ítem")
    @Valid
    private List<ItemCompraRequest> items;
}