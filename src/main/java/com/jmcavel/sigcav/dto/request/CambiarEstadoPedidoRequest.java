package com.jmcavel.sigcav.dto.request;

import com.jmcavel.sigcav.enums.EstadoPedido;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CambiarEstadoPedidoRequest {

    @NotNull(message = "El estado destino es obligatorio")
    private EstadoPedido estadoDestino;

    private String justificacion;
}