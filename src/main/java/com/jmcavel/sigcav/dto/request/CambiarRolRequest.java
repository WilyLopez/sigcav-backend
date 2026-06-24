package com.jmcavel.sigcav.dto.request;

import com.jmcavel.sigcav.enums.Rol;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CambiarRolRequest {

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;
}