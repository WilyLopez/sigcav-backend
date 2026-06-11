package com.jmcavel.sigcav.mapper;

import com.jmcavel.sigcav.dto.response.ParametroSistemaResponse;
import com.jmcavel.sigcav.entity.ParametroSistema;
import org.springframework.stereotype.Component;

@Component
public class ParametroSistemaMapper {

    public ParametroSistemaResponse toResponse(ParametroSistema parametro) {
        return ParametroSistemaResponse.builder()
                .id(parametro.getId())
                .clave(parametro.getClave())
                .valor(parametro.getValor())
                .descripcion(parametro.getDescripcion())
                .tipoDato(parametro.getTipoDato())
                .actualizadoEn(parametro.getActualizadoEn())
                .actualizadoPorNombre(
                        parametro.getActualizadoPor() != null
                                ? parametro.getActualizadoPor().getNombreCompleto()
                                : null
                )
                .build();
    }
}