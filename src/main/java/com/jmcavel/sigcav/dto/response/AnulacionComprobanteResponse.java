package com.jmcavel.sigcav.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnulacionComprobanteResponse {
    private Long id;
    private Long comprobanteId;
    private String numeroCompleto;
    private String justificacion;
    private String usuarioNombre;
    private LocalDateTime creadoEn;
}