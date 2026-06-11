package com.jmcavel.sigcav.mapper;

import com.jmcavel.sigcav.dto.response.LogAuditoriaResponse;
import com.jmcavel.sigcav.entity.LogAuditoria;
import org.springframework.stereotype.Component;

@Component
public class LogAuditoriaMapper {

    public LogAuditoriaResponse toResponse(LogAuditoria log) {
        return LogAuditoriaResponse.builder()
                .id(log.getId())
                .nombreUsuario(
                        log.getUsuario() != null
                                ? log.getUsuario().getNombreUsuario()
                                : "sistema"
                )
                .accion(log.getAccion())
                .entidad(log.getEntidad())
                .entidadId(log.getEntidadId())
                .detalle(log.getDetalle())
                .ipOrigen(log.getIpOrigen())
                .creadoEn(log.getCreadoEn())
                .build();
    }
}