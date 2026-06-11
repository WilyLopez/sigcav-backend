package com.jmcavel.sigcav.dto.response;

import com.jmcavel.sigcav.enums.TipoDocumento;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClienteResumenResponse {

    private Long id;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private String nombreRazonSocial;
    private String telefonoPrincipal;
    private String correo;
    private Boolean activo;
}