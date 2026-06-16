package com.jmcavel.sigcav.dto.response;

import com.jmcavel.sigcav.enums.EstadoPedido;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialEstadoPedidoResponse {

    private Long id;
    private EstadoPedido estadoAnterior;
    private EstadoPedido estadoNuevo;
    private String justificacion;
    private String usuarioNombre;
    private LocalDateTime creadoEn;
}