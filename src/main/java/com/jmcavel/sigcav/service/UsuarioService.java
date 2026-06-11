package com.jmcavel.sigcav.service;

import com.jmcavel.sigcav.dto.request.ActualizarUsuarioRequest;
import com.jmcavel.sigcav.dto.request.CambioContrasenaRequest;
import com.jmcavel.sigcav.dto.request.RegistroUsuarioRequest;
import com.jmcavel.sigcav.dto.response.UsuarioResponse;
import com.jmcavel.sigcav.entity.Usuario;
import com.jmcavel.sigcav.enums.AccionAuditoria;
import com.jmcavel.sigcav.enums.EntidadAuditoria;
import com.jmcavel.sigcav.exception.RecursoNoEncontradoException;
import com.jmcavel.sigcav.exception.ReglaDeNegocioException;
import com.jmcavel.sigcav.mapper.UsuarioMapper;
import com.jmcavel.sigcav.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;
    private final AuditoriaService auditoriaService;

    @Transactional
    public UsuarioResponse registrar(RegistroUsuarioRequest request, Long usuarioSolicitanteId) {
        if (usuarioRepository.existsByNombreUsuario(request.getNombreUsuario())) {
            throw new ReglaDeNegocioException(
                    "El nombre de usuario ya está en uso: " + request.getNombreUsuario()
            );
        }
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new ReglaDeNegocioException(
                    "El correo ya está registrado: " + request.getCorreo()
            );
        }

        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setContrasenaHash(passwordEncoder.encode(request.getContrasena()));

        Usuario guardado = usuarioRepository.save(usuario);

        auditoriaService.registrar(
                usuarioSolicitanteId,
                AccionAuditoria.CREAR,
                EntidadAuditoria.USUARIO,
                guardado.getId(),
                "Usuario creado: " + guardado.getNombreUsuario()
        );

        return usuarioMapper.toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con id: " + id
                ));
        return usuarioMapper.toResponse(usuario);
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, ActualizarUsuarioRequest request, Long usuarioSolicitanteId) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con id: " + id
                ));

        if (!usuario.getCorreo().equals(request.getCorreo())
                && usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new ReglaDeNegocioException(
                    "El correo ya está en uso: " + request.getCorreo()
            );
        }

        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setCorreo(request.getCorreo());

        Usuario guardado = usuarioRepository.save(usuario);

        auditoriaService.registrar(
                usuarioSolicitanteId,
                AccionAuditoria.EDITAR,
                EntidadAuditoria.USUARIO,
                guardado.getId(),
                "Usuario actualizado: " + guardado.getNombreUsuario()
        );

        return usuarioMapper.toResponse(guardado);
    }

    @Transactional
    public void cambiarContrasena(Long id, CambioContrasenaRequest request, Long usuarioSolicitanteId) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con id: " + id
                ));

        if (!passwordEncoder.matches(request.getContrasenaActual(), usuario.getContrasenaHash())) {
            throw new ReglaDeNegocioException("La contraseña actual es incorrecta");
        }

        if (!request.getContrasenaNueva().equals(request.getConfirmarContrasena())) {
            throw new ReglaDeNegocioException("La nueva contraseña y la confirmación no coinciden");
        }

        usuario.setContrasenaHash(passwordEncoder.encode(request.getContrasenaNueva()));
        usuarioRepository.save(usuario);

        auditoriaService.registrar(
                usuarioSolicitanteId,
                AccionAuditoria.CAMBIO_CONTRASENA,
                EntidadAuditoria.USUARIO,
                id,
                "Cambio de contraseña para usuario: " + usuario.getNombreUsuario()
        );
    }

    @Transactional
    public void desactivar(Long id, Long usuarioSolicitanteId) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con id: " + id
                ));

        if (id.equals(usuarioSolicitanteId)) {
            throw new ReglaDeNegocioException("Un usuario no puede desactivarse a sí mismo");
        }

        usuario.setActivo(false);
        usuarioRepository.save(usuario);

        auditoriaService.registrar(
                usuarioSolicitanteId,
                AccionAuditoria.ELIMINAR,
                EntidadAuditoria.USUARIO,
                id,
                "Usuario desactivado: " + usuario.getNombreUsuario()
        );
    }

    @Transactional
    public void activar(Long id, Long usuarioSolicitanteId) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con id: " + id
                ));

        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        auditoriaService.registrar(
                usuarioSolicitanteId,
                AccionAuditoria.EDITAR,
                EntidadAuditoria.USUARIO,
                id,
                "Usuario reactivado: " + usuario.getNombreUsuario()
        );
    }
}