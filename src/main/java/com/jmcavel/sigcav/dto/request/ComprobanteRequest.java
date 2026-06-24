package com.jmcavel.sigcav.dto.request;

import com.jmcavel.sigcav.enums.FormaPago;
import com.jmcavel.sigcav.enums.TipoComprobante;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprobanteRequest {

    @NotNull
    private Long pedidoId;

    @NotNull
    private TipoComprobante tipoComprobante;

    @NotNull
    private FormaPago formaPago;

    @NotBlank
    private String descripcionServicio;
}