package com.jmcavel.sigcav.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class RespuestaError {

    private LocalDateTime timestamp;
    private int estado;
    private String error;
    private String mensaje;
    private Map<String, String> erroresCampos;
}