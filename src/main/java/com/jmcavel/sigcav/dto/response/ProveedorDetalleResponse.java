package com.jmcavel.sigcav.dto.response;

import com.jmcavel.sigcav.enums.TipoDocumento;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProveedorDetalleResponse {

    private Long id;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private String nombreRazonSocial;
    private Long categoriaProveedorId;
    private String categoriaNombre;
    private String telefono;
    private String correo;
    private String direccion;
    private String nombreRepresentante;
    private String observaciones;
    private Boolean activo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}