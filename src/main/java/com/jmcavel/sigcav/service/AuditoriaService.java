package com.jmcavel.sigcav.service;

import com.jmcavel.sigcav.entity.LogAuditoria;
import com.jmcavel.sigcav.entity.Usuario;
import com.jmcavel.sigcav.enums.AccionAuditoria;
import com.jmcavel.sigcav.enums.EntidadAuditoria;
import com.jmcavel.sigcav.repository.LogAuditoriaRepository;
import com.jmcavel.sigcav.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final LogAuditoriaRepository logAuditoriaRepository;
    private final UsuarioRepository usuarioRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(
            Long usuarioId,
            AccionAuditoria accion,
            EntidadAuditoria entidad,
            Long entidadId,
            String detalle,
            String ipOrigen
    ) {
        Usuario usuario = usuarioId != null
                ? usuarioRepository.findById(usuarioId).orElse(null)
                : null;

        LogAuditoria log = LogAuditoria.builder()
                .usuario(usuario)
                .accion(accion)
                .entidad(entidad)
                .entidadId(entidadId)
                .detalle(detalle)
                .ipOrigen(ipOrigen)
                .build();

        logAuditoriaRepository.save(log);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(
            Long usuarioId,
            AccionAuditoria accion,
            EntidadAuditoria entidad,
            Long entidadId,
            String detalle
    ) {
        registrar(usuarioId, accion, entidad, entidadId, detalle, null);
    }

    // Convenience method for legacy or simplified calls
    public void registrar(String accion, String entidad, Long entidadId, String detalle) {
        registrar(null, 
                AccionAuditoria.valueOf(accion.toUpperCase()), 
                EntidadAuditoria.valueOf(entidad.toUpperCase()), 
                entidadId, 
                detalle);
    }
}
