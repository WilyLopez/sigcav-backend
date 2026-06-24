package com.jmcavel.sigcav.dto.response;

import com.jmcavel.sigcav.enums.FormaPago;
import com.jmcavel.sigcav.enums.TipoComprobante;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprobanteResponse {
    private Long id;
    private Long pedidoId;
    private String numeroPedido;
    private TipoComprobante tipoComprobante;
    private String serie;
    private Integer correlativo;
    private String numeroCompleto;
    private LocalDate fechaEmision;
    private Long clienteId;
    private String clienteNombreRazonSocial;
    private String clienteNumeroDocumento;
    private String descripcionServicio;
    private BigDecimal subtotal;
    private BigDecimal igvPorcentaje;
    private BigDecimal igvMonto;
    private BigDecimal total;
    private FormaPago formaPago;
    private boolean anulado;
    private String emitidoPorNombre;
    private LocalDateTime creadoEn;
}