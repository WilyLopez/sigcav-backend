package com.jmcavel.sigcav.dto.response;

import com.jmcavel.sigcav.enums.AccionAuditoria;
import com.jmcavel.sigcav.enums.EntidadAuditoria;
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
public class LogAuditoriaResponse {

    private Long id;
    private String nombreUsuario;
    private AccionAuditoria accion;
    private EntidadAuditoria entidad;
    private Long entidadId;
    private String detalle;
    private String ipOrigen;
    private LocalDateTime creadoEn;
}