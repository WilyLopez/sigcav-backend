package com.jmcavel.sigcav.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraPedidoResponse {

    private Long id;
    private Long compraId;
    private Long pedidoId;
    private String pedidoNumero;
    private BigDecimal montoAsignado;
    private LocalDateTime creadoEn;
}