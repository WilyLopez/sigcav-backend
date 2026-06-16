package com.jmcavel.sigcav.mapper;

import com.jmcavel.sigcav.dto.response.NotaCorreccionPagoResponse;
import com.jmcavel.sigcav.entity.NotaCorreccionPago;
import org.springframework.stereotype.Component;

@Component
public class NotaCorreccionPagoMapper {

    public NotaCorreccionPagoResponse toResponse(NotaCorreccionPago nota) {
        return NotaCorreccionPagoResponse.builder()
                .id(nota.getId())
                .pagoId(nota.getPago().getId())
                .justificacion(nota.getJustificacion())
                .usuarioNombre(nota.getUsuario() != null
                        ? nota.getUsuario().getNombreCompleto() : null)
                .creadoEn(nota.getCreadoEn())
                .build();
    }
}