package com.jmcavel.sigcav.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualizarParametroRequest {

    @NotBlank(message = "El valor del parámetro es obligatorio")
    @Size(max = 500, message = "El valor no puede superar los 500 caracteres")
    private String valor;
}