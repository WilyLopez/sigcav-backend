package com.jmcavel.sigcav.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sesion_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SesionUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "token_sesion", nullable = false, unique = true, length = 512)
    private String tokenSesion;

    @Column(name = "ultimo_acceso", nullable = false)
    private LocalDateTime ultimoAcceso;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(name = "activa", nullable = false)
    private Boolean activa;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void antesDeCrear() {
        creadoEn = LocalDateTime.now();
        ultimoAcceso = LocalDateTime.now();
        if (activa == null) {
            activa = true;
        }
    }
}