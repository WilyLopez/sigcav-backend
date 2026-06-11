package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    java.util.List<Pedido> findAllByClienteIdOrderByFechaIngresoDesc(Long clienteId);
}
