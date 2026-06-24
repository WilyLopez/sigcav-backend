package com.jmcavel.sigcav.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "nota_correccion_pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotaCorreccionPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pago_id", nullable = false)
    private Pago pago;

    @Column(name = "justificacion", nullable = false, columnDefinition = "TEXT")
    private String justificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @CreationTimestamp
    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;
}
