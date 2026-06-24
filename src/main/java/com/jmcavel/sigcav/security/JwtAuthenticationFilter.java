package com.jmcavel.sigcav.security;

import com.jmcavel.sigcav.exception.TokenInvalidoException;
import com.jmcavel.sigcav.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = extraerToken(request);

        if (StringUtils.hasText(token)) {
            try {
                if (jwtTokenProvider.esValido(token)) {
                    String nombreUsuario = jwtTokenProvider.obtenerNombreUsuario(token);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(nombreUsuario);

                    UsernamePasswordAuthenticationToken autenticacion =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    autenticacion.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(autenticacion);
                }
            } catch (TokenInvalidoException ex) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extraerToken(HttpServletRequest request) {
        String cabecera = request.getHeader(SecurityConstants.HEADER_AUTORIZACION);
        if (StringUtils.hasText(cabecera) && cabecera.startsWith(SecurityConstants.PREFIJO_TOKEN)) {
            return cabecera.substring(SecurityConstants.PREFIJO_TOKEN.length());
        }
        return null;
    }
}