package com.jmcavel.sigcav.mapper;

import com.jmcavel.sigcav.dto.request.RegistroUsuarioRequest;
import com.jmcavel.sigcav.dto.response.UsuarioResponse;
import com.jmcavel.sigcav.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombreCompleto(usuario.getNombreCompleto())
                .nombreUsuario(usuario.getNombreUsuario())
                .correo(usuario.getCorreo())
                .activo(usuario.getActivo())
                .creadoEn(usuario.getCreadoEn())
                .actualizadoEn(usuario.getActualizadoEn())
                .build();
    }

    public Usuario toEntity(RegistroUsuarioRequest request) {
        return Usuario.builder()
                .nombreCompleto(request.getNombreCompleto())
                .nombreUsuario(request.getNombreUsuario())
                .correo(request.getCorreo())
                .build();
    }
}