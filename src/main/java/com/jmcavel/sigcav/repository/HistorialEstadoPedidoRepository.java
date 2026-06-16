package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.HistorialEstadoPedido;
import com.jmcavel.sigcav.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistorialEstadoPedidoRepository extends JpaRepository<HistorialEstadoPedido, Long> {

    List<HistorialEstadoPedido> findByPedidoIdOrderByCreadoEnDesc(Long pedidoId);

    List<HistorialEstadoPedido> findByPedidoIdOrderByCreadoEnAsc(Long pedidoId);

    @Query("""
            SELECT h FROM HistorialEstadoPedido h
            WHERE h.pedido.id = :pedidoId
            ORDER BY h.creadoEn DESC
            LIMIT 1
            """)
    Optional<HistorialEstadoPedido> findUltimoCambioPorPedido(@Param("pedidoId") Long pedidoId);

    @Query("""
            SELECT COUNT(h) > 0 FROM HistorialEstadoPedido h
            WHERE h.pedido.id = :pedidoId
            AND h.estadoNuevo = :estado
            """)
    boolean existeCambioAEstado(@Param("pedidoId") Long pedidoId, @Param("estado") EstadoPedido estado);
}