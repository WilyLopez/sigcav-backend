package com.jmcavel.sigcav.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CategoriaProveedorResponse {

    private Long id;
    private String nombre;
    private Boolean activo;
    private LocalDateTime creadoEn;
}