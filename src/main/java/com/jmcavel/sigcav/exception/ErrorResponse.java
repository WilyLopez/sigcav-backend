package com.jmcavel.sigcav.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        int estado,
        String mensaje,
        List<String> errores,
        LocalDateTime timestamp
) {
    public static ErrorResponse de(int estado, String mensaje) {
        return new ErrorResponse(estado, mensaje, null, LocalDateTime.now());
    }

    public static ErrorResponse de(int estado, String mensaje, List<String> errores) {
        return new ErrorResponse(estado, mensaje, errores, LocalDateTime.now());
    }
}