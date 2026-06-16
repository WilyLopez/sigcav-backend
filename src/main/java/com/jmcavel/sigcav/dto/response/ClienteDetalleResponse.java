package com.jmcavel.sigcav.dto.response;

import com.jmcavel.sigcav.enums.TipoDocumento;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ClienteDetalleResponse {

    private Long id;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private String nombreRazonSocial;
    private String telefonoPrincipal;
    private String telefonoSecundario;
    private String correo;
    private String direccionEntrega;
    private String nombreContactoRef;
    private String observaciones;
    private Boolean activo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private List<ContactoClienteResponse> contactos;
}