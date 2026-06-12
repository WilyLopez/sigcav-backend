package com.jmcavel.sigcav.dto.response;

import com.jmcavel.sigcav.enums.TipoComprobanteProveedor;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraResponse {

    private Long id;
    private Long proveedorId;
    private String proveedorNombre;
    private LocalDate fechaCompra;
    private String numeroComprobanteProveedor;
    private TipoComprobanteProveedor tipoComprobanteProveedor;
    private BigDecimal total;
    private String observaciones;
    private Long registradoPorId;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private List<ItemCompraResponse> items;
}