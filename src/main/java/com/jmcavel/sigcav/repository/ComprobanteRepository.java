package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.Comprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {
    java.util.List<Comprobante> findAllByClienteIdOrderByFechaEmisionDesc(Long clienteId);
}
