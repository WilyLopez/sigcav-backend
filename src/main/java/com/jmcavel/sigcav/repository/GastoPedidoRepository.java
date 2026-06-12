package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.GastoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface GastoPedidoRepository extends JpaRepository<GastoPedido, Long> {

    List<GastoPedido> findByPedidoIdOrderByCreadoEnDesc(Long pedidoId);

    @Query("""
            SELECT COALESCE(SUM(g.monto), 0)
            FROM GastoPedido g
            WHERE g.pedido.id = :pedidoId
            """)
    BigDecimal sumarGastosPorPedido(@Param("pedidoId") Long pedidoId);

    @Query("""
            SELECT COALESCE(SUM(g.monto), 0)
            FROM GastoPedido g
            WHERE g.pedido.id = :pedidoId
              AND g.id <> :excluirId
            """)
    BigDecimal sumarGastosPorPedidoExcluyendo(
            @Param("pedidoId") Long pedidoId,
            @Param("excluirId") Long excluirId
    );

    void deleteByPedidoId(Long pedidoId);
}