package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.SerieComprobante;
import com.jmcavel.sigcav.enums.TipoComprobante;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SerieComprobanteRepository extends JpaRepository<SerieComprobante, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SerieComprobante s WHERE s.tipoComprobante = :tipo")
    Optional<SerieComprobante> findByTipoComprobanteWithLock(@Param("tipo") TipoComprobante tipo);

    Optional<SerieComprobante> findByTipoComprobante(TipoComprobante tipo);
}