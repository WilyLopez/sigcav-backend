package com.jmcavel.sigcav.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ContactoClienteResponse {

    private Long id;
    private Long clienteId;
    private String nombre;
    private String telefono;
    private String correo;
    private String cargo;
    private Boolean esPrincipal;
    private LocalDateTime creadoEn;
}