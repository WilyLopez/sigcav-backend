package com.jmcavel.sigcav.dto.response;

import com.jmcavel.sigcav.enums.FormaPago;
import com.jmcavel.sigcav.enums.TipoPago;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoResponse {
    private Long id;
    private Long pedidoId;
    private String numeroPedido;
    private TipoPago tipoPago;
    private BigDecimal monto;
    private LocalDate fechaPago;
    private FormaPago formaPago;
    private String observacion;
    private String registradoPorNombre;
    private LocalDateTime creadoEn;
}