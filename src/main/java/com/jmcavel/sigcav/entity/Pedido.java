package com.jmcavel.sigcav.entity;

import com.jmcavel.sigcav.enums.EstadoPagoPedido;
import com.jmcavel.sigcav.enums.EstadoPedido;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_pedido", nullable = false, unique = true, length = 20)
    private String numeroPedido;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cotizacion_id", unique = true)
    private Cotizacion cotizacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_producto_id", nullable = false)
    private CategoriaProducto categoriaProducto;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "especificaciones", nullable = false, columnDefinition = "TEXT")
    private String especificaciones;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_venta", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "fecha_entrega_comprometida", nullable = false)
    private LocalDate fechaEntregaComprometida;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoPedido estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago", nullable = false, length = 25)
    private EstadoPagoPedido estadoPago;

    @Column(name = "costo_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal costoTotal;

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
