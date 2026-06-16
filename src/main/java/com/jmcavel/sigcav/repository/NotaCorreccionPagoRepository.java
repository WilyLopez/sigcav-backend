package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.NotaCorreccionPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotaCorreccionPagoRepository extends JpaRepository<NotaCorreccionPago, Long> {

    List<NotaCorreccionPago> findByPagoIdOrderByCreadoEnDesc(Long pagoId);

    List<NotaCorreccionPago> findByPagoPedidoId(Long pedidoId);
}