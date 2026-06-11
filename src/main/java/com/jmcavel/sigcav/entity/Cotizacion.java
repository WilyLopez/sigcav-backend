package com.jmcavel.sigcav.entity;

import com.jmcavel.sigcav.enums.EstadoCotizacion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cotizacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_cotizacion", nullable = false, unique = true, length = 20)
    private String numeroCotizacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

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

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "descuento_porcentaje", nullable = false, precision = 5, scale = 2)
    private BigDecimal descuentoPorcentaje;

    @Column(name = "recargo_porcentaje", nullable = false, precision = 5, scale = 2)
    private BigDecimal recargoPorcentaje;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "aplica_igv", nullable = false)
    private boolean aplicaIgv;

    @Column(name = "igv_monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal igvMonto;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(name = "tiempo_entrega_estimado", length = 100)
    private String tiempoEntregaEstimado;

    @Column(name = "condiciones_pago", columnDefinition = "TEXT")
    private String condicionesPago;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoCotizacion estado;

    @Column(name = "convertida_en_pedido", nullable = false)
    private boolean convertidaEnPedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por_id")
    private Usuario creadoPor;

    @CreationTimestamp
    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;
}
