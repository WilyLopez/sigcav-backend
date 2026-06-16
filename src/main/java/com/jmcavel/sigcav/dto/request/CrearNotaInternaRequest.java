package com.jmcavel.sigcav.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearNotaInternaRequest {

    @NotBlank(message = "El contenido de la nota es obligatorio")
    private String contenido;
}