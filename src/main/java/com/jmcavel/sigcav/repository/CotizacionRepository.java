package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.Cotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {
    java.util.List<Cotizacion> findAllByClienteIdOrderByFechaEmisionDesc(Long clienteId);
}
