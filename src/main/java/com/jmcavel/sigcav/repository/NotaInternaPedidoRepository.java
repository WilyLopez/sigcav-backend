package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.NotaInternaPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotaInternaPedidoRepository extends JpaRepository<NotaInternaPedido, Long> {
    List<NotaInternaPedido> findByPedidoIdOrderByCreadoEnDesc(Long pedidoId);
}
