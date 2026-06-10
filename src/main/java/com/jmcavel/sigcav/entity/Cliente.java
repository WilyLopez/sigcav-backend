package com.jmcavel.sigcav.entity;

import com.jmcavel.sigcav.enums.TipoDocumento;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 3)
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento", nullable = false, unique = true, length = 11)
    private String numeroDocumento;

    @Column(name = "nombre_razon_social", nullable = false, length = 200)
    private String nombreRazonSocial;

    @Column(name = "telefono_principal", nullable = false, length = 20)
    private String telefonoPrincipal;

    @Column(name = "telefono_secundario", length = 20)
    private String telefonoSecundario;

    @Column(name = "correo", nullable = false, length = 150)
    private String correo;

    @Column(name = "direccion_entrega", nullable = false, length = 300)
    private String direccionEntrega;

    @Column(name = "nombre_contacto_ref", length = 200)
    private String nombreContactoRef;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn = LocalDateTime.now();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ContactoCliente> contactos = new ArrayList<>();

    @PreUpdate
    public void preUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }
}