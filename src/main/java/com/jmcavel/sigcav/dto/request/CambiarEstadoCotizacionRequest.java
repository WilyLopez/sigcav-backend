package com.jmcavel.sigcav.dto.request;

import com.jmcavel.sigcav.enums.EstadoCotizacion;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CambiarEstadoCotizacionRequest {

    @NotNull(message = "El estado es obligatorio")
    private EstadoCotizacion estado;

    private String observacion;
}