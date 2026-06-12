package com.jmcavel.sigcav.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotaInternaPedidoResponse {

    private Long id;
    private String contenido;
    private String usuarioNombre;
    private LocalDateTime creadoEn;
}