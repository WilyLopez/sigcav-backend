package com.jmcavel.sigcav.dto.response;

import com.jmcavel.sigcav.enums.TipoGasto;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GastoPedidoResponse {

    private Long id;
    private Long pedidoId;
    private TipoGasto tipoGasto;
    private String descripcion;
    private Long proveedorId;
    private String proveedorNombre;
    private LocalDate fechaGasto;
    private BigDecimal monto;
    private String numeroComprobanteProveedor;
    private String observaciones;
    private Long registradoPorId;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}