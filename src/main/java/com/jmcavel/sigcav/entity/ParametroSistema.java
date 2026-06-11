package com.jmcavel.sigcav.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "parametro_sistema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParametroSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "clave", nullable = false, unique = true, length = 100)
    private String clave;

    @Column(name = "valor", nullable = false, length = 500)
    private String valor;

    @Column(name = "descripcion", length = 300)
    private String descripcion;

    @Column(name = "tipo_dato", nullable = false, length = 20)
    private TipoDato tipoDato;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actualizado_por_id")
    private Usuario actualizadoPor;

    @PrePersist
    protected void antesDeCrear() {
        actualizadoEn = LocalDateTime.now();
        if (tipoDato == null) {
            tipoDato = TipoDato.TEXTO;
        }
    }

    @PreUpdate
    protected void antesDeActualizar() {
        actualizadoEn = LocalDateTime.now();
    }

    public Integer getValorComoEntero() {
        return Integer.parseInt(valor);
    }

    public Double getValorComoDecimal() {
        return Double.parseDouble(valor);
    }

    public Boolean getValorComoBooleano() {
        return Boolean.parseBoolean(valor);
    }
}