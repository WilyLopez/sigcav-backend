package com.jmcavel.sigcav.controller;

import com.jmcavel.sigcav.dto.request.LoginRequest;
import com.jmcavel.sigcav.dto.request.RefreshTokenRequest;
import com.jmcavel.sigcav.dto.response.ApiResponse;
import com.jmcavel.sigcav.dto.response.TokenResponse;
import com.jmcavel.sigcav.security.SecurityConstants;
import com.jmcavel.sigcav.security.UsuarioPrincipal;
import com.jmcavel.sigcav.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        String ipOrigen = obtenerIpOrigen(httpRequest);
        TokenResponse token = authService.login(request, ipOrigen);
        return ResponseEntity.ok(ApiResponse.exito("Inicio de sesión exitoso", token));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> renovarToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        TokenResponse token = authService.renovarToken(request);
        return ResponseEntity.ok(ApiResponse.exito("Token renovado exitosamente", token));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal UsuarioPrincipal principal,
            HttpServletRequest httpRequest
    ) {
        String token = extraerToken(httpRequest);
        authService.logout(token, principal.getId());
        return ResponseEntity.ok(ApiResponse.exito("Sesión cerrada exitosamente"));
    }

    private String obtenerIpOrigen(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String extraerToken(HttpServletRequest request) {
        String cabecera = request.getHeader(SecurityConstants.HEADER_AUTORIZACION);
        if (StringUtils.hasText(cabecera) && cabecera.startsWith(SecurityConstants.PREFIJO_TOKEN)) {
            return cabecera.substring(SecurityConstants.PREFIJO_TOKEN.length());
        }
        return null;
    }
}