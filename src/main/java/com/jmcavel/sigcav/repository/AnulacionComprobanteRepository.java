package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.AnulacionComprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnulacionComprobanteRepository extends JpaRepository<AnulacionComprobante, Long> {

    Optional<AnulacionComprobante> findByComprobanteId(Long comprobanteId);

    boolean existsByComprobanteId(Long comprobanteId);
}