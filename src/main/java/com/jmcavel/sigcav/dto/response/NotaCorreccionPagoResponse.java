package com.jmcavel.sigcav.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotaCorreccionPagoResponse {
    private Long id;
    private Long pagoId;
    private String justificacion;
    private String usuarioNombre;
    private LocalDateTime creadoEn;
}