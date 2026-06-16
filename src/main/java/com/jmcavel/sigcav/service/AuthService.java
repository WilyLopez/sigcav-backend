package com.jmcavel.sigcav.service;

import com.jmcavel.sigcav.dto.request.LoginRequest;
import com.jmcavel.sigcav.dto.request.RefreshTokenRequest;
import com.jmcavel.sigcav.dto.response.TokenResponse;
import com.jmcavel.sigcav.entity.RefreshToken;
import com.jmcavel.sigcav.entity.SesionUsuario;
import com.jmcavel.sigcav.entity.Usuario;
import com.jmcavel.sigcav.enums.AccionAuditoria;
import com.jmcavel.sigcav.enums.EntidadAuditoria;
import com.jmcavel.sigcav.exception.RecursoNoEncontradoException;
import com.jmcavel.sigcav.exception.ReglaDeNegocioException;
import com.jmcavel.sigcav.mapper.UsuarioMapper;
import com.jmcavel.sigcav.repository.RefreshTokenRepository;
import com.jmcavel.sigcav.repository.SesionUsuarioRepository;
import com.jmcavel.sigcav.repository.UsuarioRepository;
import com.jmcavel.sigcav.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final SesionUsuarioRepository sesionUsuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuditoriaService auditoriaService;
    private final UsuarioMapper usuarioMapper;

    @Value("${sigcav.jwt.expiracion-segundos:3600}")
    private Long expiracionSegundos;

    @Value("${sigcav.refresh-token.expiracion-dias:7}")
    private Long expiracionDiasRefreshToken;

    @Transactional
    public TokenResponse login(LoginRequest request, String ipOrigen) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(request.getNombreUsuario())
                .orElseThrow(() -> new ReglaDeNegocioException(
                        "Credenciales incorrectas"
                ));

        if (!usuario.getActivo()) {
            throw new ReglaDeNegocioException("El usuario se encuentra desactivado");
        }

        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasenaHash())) {
            throw new ReglaDeNegocioException("Credenciales incorrectas");
        }

        String accessToken = jwtTokenProvider.generarToken(usuario);
        String refreshToken = generarRefreshToken(usuario);

        SesionUsuario sesion = SesionUsuario.builder()
                .usuario(usuario)
                .tokenSesion(accessToken)
                .ipOrigen(ipOrigen)
                .activa(true)
                .build();
        sesionUsuarioRepository.save(sesion);

        auditoriaService.registrar(
                usuario.getId(),
                AccionAuditoria.LOGIN,
                EntidadAuditoria.USUARIO,
                usuario.getId(),
                "Inicio de sesión",
                ipOrigen
        );

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tipo("Bearer")
                .expiraEnSegundos(expiracionSegundos)
                .usuario(usuarioMapper.toResponse(usuario))
                .build();
    }

    @Transactional
    public TokenResponse renovarToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenAndRevocadoFalse(request.getRefreshToken())
                .orElseThrow(() -> new ReglaDeNegocioException(
                        "Refresh token inválido o revocado"
                ));

        if (refreshToken.estaExpirado()) {
            refreshTokenRepository.revocarPorToken(refreshToken.getToken());
            throw new ReglaDeNegocioException("El refresh token ha expirado, inicie sesión nuevamente");
        }

        Usuario usuario = refreshToken.getUsuario();

        if (!usuario.getActivo()) {
            throw new ReglaDeNegocioException("El usuario se encuentra desactivado");
        }

        refreshTokenRepository.revocarPorToken(refreshToken.getToken());
        sesionUsuarioRepository.desactivarSesionesPorUsuario(usuario.getId());

        String nuevoAccessToken = jwtTokenProvider.generarToken(usuario);
        String nuevoRefreshToken = generarRefreshToken(usuario);

        SesionUsuario sesion = SesionUsuario.builder()
                .usuario(usuario)
                .tokenSesion(nuevoAccessToken)
                .activa(true)
                .build();
        sesionUsuarioRepository.save(sesion);

        return TokenResponse.builder()
                .accessToken(nuevoAccessToken)
                .refreshToken(nuevoRefreshToken)
                .tipo("Bearer")
                .expiraEnSegundos(expiracionSegundos)
                .usuario(usuarioMapper.toResponse(usuario))
                .build();
    }

    @Transactional
    public void logout(String tokenSesion, Long usuarioId) {
        sesionUsuarioRepository.findByTokenSesionAndActivaTrue(tokenSesion)
                .ifPresent(sesion -> {
                    sesion.setActiva(false);
                    sesionUsuarioRepository.save(sesion);
                });

        refreshTokenRepository.revocarTodosPorUsuario(usuarioId);

        auditoriaService.registrar(
                usuarioId,
                AccionAuditoria.LOGOUT,
                EntidadAuditoria.USUARIO,
                usuarioId,
                "Cierre de sesión"
        );
    }

    @Transactional
    public void actualizarUltimoAcceso(String tokenSesion) {
        sesionUsuarioRepository.actualizarUltimoAcceso(tokenSesion, LocalDateTime.now());
    }

    private String generarRefreshToken(Usuario usuario) {
        RefreshToken refreshToken = RefreshToken.builder()
                .usuario(usuario)
                .token(UUID.randomUUID().toString())
                .expiraEn(LocalDateTime.now().plusDays(expiracionDiasRefreshToken))
                .revocado(false)
                .build();
        return refreshTokenRepository.save(refreshToken).getToken();
    }
}