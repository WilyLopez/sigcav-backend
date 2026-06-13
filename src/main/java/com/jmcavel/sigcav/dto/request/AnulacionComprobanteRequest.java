package com.jmcavel.sigcav.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnulacionComprobanteRequest {

    @NotNull
    private Long comprobanteId;

    @NotBlank
    private String justificacion;
}