package com.jmcavel.sigcav.dto.request;

import com.jmcavel.sigcav.enums.TipoComprobante;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteFiltroRequest {

    @NotNull
    private LocalDate desde;

    @NotNull
    private LocalDate hasta;

    private TipoComprobante tipoComprobante;

    private Long proveedorId;
}