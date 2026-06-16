package com.jmcavel.sigcav.dto.response;

import com.jmcavel.sigcav.enums.TipoDato;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParametroSistemaResponse {

    private Long id;
    private String clave;
    private String valor;
    private String descripcion;
    private TipoDato tipoDato;
    private LocalDateTime actualizadoEn;
    private String actualizadoPorNombre;
}