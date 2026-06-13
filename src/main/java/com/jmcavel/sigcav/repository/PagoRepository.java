package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.Pago;
import com.jmcavel.sigcav.enums.TipoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByPedidoId(Long pedidoId);

    Optional<Pago> findByPedidoIdAndTipoPago(Long pedidoId, TipoPago tipoPago);

    boolean existsByPedidoIdAndTipoPago(Long pedidoId, TipoPago tipoPago);

    int countByPedidoId(Long pedidoId);

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE p.pedido.id = :pedidoId")
    java.math.BigDecimal sumMontoByPedidoId(@Param("pedidoId") Long pedidoId);
}