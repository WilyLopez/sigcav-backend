package com.jmcavel.sigcav.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "plantilla_cotizacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlantillaCotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_plantilla", nullable = false, length = 200)
    private String nombrePlantilla;

    @Column(name = "descripcion_producto", nullable = false, columnDefinition = "TEXT")
    private String descripcionProducto;

    @Column(name = "tipo_impresion", length = 200)
    private String tipoImpresion;

    @Column(name = "material", length = 200)
    private String material;

    @Column(name = "dimensiones", length = 200)
    private String dimensiones;

    @Column(name = "acabados", length = 200)
    private String acabados;

    @Column(name = "condiciones_pago", columnDefinition = "TEXT")
    private String condicionesPago;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por_id")
    private Usuario creadoPor;

    @CreationTimestamp
    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void antesDeCrear() {
        if (activo == null) {
            activo = true;
        }
    }
}
