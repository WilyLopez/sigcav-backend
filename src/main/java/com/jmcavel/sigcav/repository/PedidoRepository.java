package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.Pedido;
import com.jmcavel.sigcav.enums.EstadoPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Optional<Pedido> findByNumeroPedido(String numeroPedido);

    boolean existsByNumeroPedido(String numeroPedido);

    List<Pedido> findAllByClienteIdOrderByFechaIngresoDesc(Long clienteId);

    Page<Pedido> findByEstado(EstadoPedido estado, Pageable pageable);

    @Query("""
            SELECT p FROM Pedido p
            WHERE (:clienteId IS NULL OR p.cliente.id = :clienteId)
            AND (:estado IS NULL OR p.estado = :estado)
            AND (:categoriaId IS NULL OR p.categoriaProducto.id = :categoriaId)
            AND (:desdeIngreso IS NULL OR p.fechaIngreso >= :desdeIngreso)
            AND (:hastaIngreso IS NULL OR p.fechaIngreso <= :hastaIngreso)
            AND (:desdeEntrega IS NULL OR p.fechaEntregaComprometida >= :desdeEntrega)
            AND (:hastaEntrega IS NULL OR p.fechaEntregaComprometida <= :hastaEntrega)
            """)
    Page<Pedido> buscarConFiltros(
            @Param("clienteId") Long clienteId,
            @Param("estado") EstadoPedido estado,
            @Param("categoriaId") Long categoriaId,
            @Param("desdeIngreso") LocalDate desdeIngreso,
            @Param("hastaIngreso") LocalDate hastaIngreso,
            @Param("desdeEntrega") LocalDate desdeEntrega,
            @Param("hastaEntrega") LocalDate hastaEntrega,
            Pageable pageable
    );

    @Query("""
            SELECT p FROM Pedido p
            WHERE p.fechaEntregaComprometida <= :fechaLimite
            AND p.estado NOT IN ('LISTO_ENTREGA', 'ENTREGADO', 'FACTURADO', 'ANULADO')
            """)
    List<Pedido> findPedidosProximosAVencer(@Param("fechaLimite") LocalDate fechaLimite);

    @Query("""
            SELECT p FROM Pedido p
            WHERE p.estado NOT IN ('FACTURADO', 'ANULADO')
            AND p.cliente.id = :clienteId
            """)
    List<Pedido> findPedidosActivosPorCliente(@Param("clienteId") Long clienteId);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(p.numeroPedido, 12) AS int)), 0) FROM Pedido p WHERE p.numeroPedido LIKE :prefijo%")
    int obtenerUltimoCorrelativo(@Param("prefijo") String prefijo);

    @Query("""
            SELECT COUNT(p) FROM Pedido p
            WHERE p.estado = :estado
            """)
    long contarPorEstado(@Param("estado") EstadoPedido estado);
}