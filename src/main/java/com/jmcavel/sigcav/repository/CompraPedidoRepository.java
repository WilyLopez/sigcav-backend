package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.CompraPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompraPedidoRepository extends JpaRepository<CompraPedido, Long> {

    List<CompraPedido> findByCompraId(Long compraId);

    List<CompraPedido> findByPedidoId(Long pedidoId);

    Optional<CompraPedido> findByCompraIdAndPedidoId(Long compraId, Long pedidoId);

    boolean existsByCompraIdAndPedidoId(Long compraId, Long pedidoId);

    @Query("""
            SELECT COALESCE(SUM(cp.montoAsignado), 0)
            FROM CompraPedido cp
            WHERE cp.compra.id = :compraId
            """)
    BigDecimal sumarMontoAsignadoPorCompra(@Param("compraId") Long compraId);

    @Query("""
            SELECT COALESCE(SUM(cp.montoAsignado), 0)
            FROM CompraPedido cp
            WHERE cp.compra.id = :compraId
              AND cp.id <> :excluirId
            """)
    BigDecimal sumarMontoAsignadoPorCompraExcluyendo(
            @Param("compraId") Long compraId,
            @Param("excluirId") Long excluirId
    );

    @Query("""
            SELECT COALESCE(SUM(cp.montoAsignado), 0)
            FROM CompraPedido cp
            WHERE cp.pedido.id = :pedidoId
            """)
    BigDecimal sumarMontoAsignadoPorPedido(@Param("pedidoId") Long pedidoId);

    void deleteByCompraId(Long compraId);
}