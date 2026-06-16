package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.NotaCorrecionPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotaCorrecionPagoRepository extends JpaRepository<NotaCorrecionPago, Long> {
    Optional<NotaCorrecionPago> findByPagoId(Long pagoId);
}
