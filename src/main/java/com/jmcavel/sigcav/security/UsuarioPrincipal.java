package com.jmcavel.sigcav.security;

import com.jmcavel.sigcav.entity.Usuario;
import com.jmcavel.sigcav.enums.Rol;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UsuarioPrincipal implements UserDetails {

    private final Long id;
    private final String nombreCompleto;
    private final String username;
    private final String password;
    private final Rol rol;
    private final boolean activo;

    private UsuarioPrincipal(Usuario usuario) {
        this.id = usuario.getId();
        this.nombreCompleto = usuario.getNombreCompleto();
        this.username = usuario.getNombreUsuario();
        this.password = usuario.getContrasenaHash();
        this.rol = usuario.getRol();
        this.activo = usuario.getActivo();
    }

    public static UsuarioPrincipal desde(Usuario usuario) {
        return new UsuarioPrincipal(usuario);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROL_" + rol.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return activo;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }
}