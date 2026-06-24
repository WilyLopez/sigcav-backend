package com.jmcavel.sigcav.security;

import com.jmcavel.sigcav.entity.Usuario;
import com.jmcavel.sigcav.exception.TokenInvalidoException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey claveFirmado;
    private final long expiracionMs;

    public JwtTokenProvider(
            @Value("${sigcav.jwt.secret}") String secreto,
            @Value("${sigcav.jwt.expiration-seconds:3600}") long expiracionSegundos
    ) {
        this.claveFirmado = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.expiracionMs = expiracionSegundos * 1000;
    }

    public String generarToken(Usuario usuario) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expiracionMs);

        return Jwts.builder()
                .subject(usuario.getNombreUsuario())
                .claim(SecurityConstants.CLAIM_USUARIO_ID, usuario.getId())
                .claim(SecurityConstants.CLAIM_NOMBRE_USUARIO, usuario.getNombreCompleto())
                .claim(SecurityConstants.CLAIM_ROL, usuario.getRol().name())
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(claveFirmado)
                .compact();
    }

    public String obtenerNombreUsuario(String token) {
        return parsearClaims(token).getSubject();
    }

    public Long obtenerUsuarioId(String token) {
        return parsearClaims(token).get(SecurityConstants.CLAIM_USUARIO_ID, Long.class);
    }

    public String obtenerRol(String token) {
        return parsearClaims(token).get(SecurityConstants.CLAIM_ROL, String.class);
    }

    public boolean esValido(String token) {
        try {
            parsearClaims(token);
            return true;
        } catch (TokenInvalidoException ex) {
            return false;
        }
    }

    private Claims parsearClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(claveFirmado)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException ex) {
            throw new TokenInvalidoException("El token ha expirado");
        } catch (JwtException ex) {
            throw new TokenInvalidoException("El token no es válido");
        }
    }
}