package com.jmcavel.sigcav.security;

public class SecurityConstants {

    private SecurityConstants() {
    }

    public static final String HEADER_AUTORIZACION = "Authorization";
    public static final String PREFIJO_TOKEN = "Bearer ";
    public static final String CLAIM_USUARIO_ID = "usuarioId";
    public static final String CLAIM_NOMBRE_USUARIO = "nombreUsuario";
    public static final String CLAIM_ROL = "rol";

    public static final String[] RUTAS_PUBLICAS = {
            "/auth/login",
            "/auth/refresh"
    };
}