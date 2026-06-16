package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.Comprobante;
import com.jmcavel.sigcav.enums.TipoComprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {

    Optional<Comprobante> findByPedidoId(Long pedidoId);

    Optional<Comprobante> findByNumeroCompleto(String numeroCompleto);

    boolean existsByPedidoId(Long pedidoId);

    List<Comprobante> findByTipoComprobanteAndAuladoFalse(TipoComprobante tipoComprobante);

    @Query("SELECT c FROM Comprobante c WHERE c.fechaEmision BETWEEN :desde AND :hasta")
    List<Comprobante> findByRangoFecha(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query("SELECT c FROM Comprobante c WHERE c.cliente.id = :clienteId ORDER BY c.fechaEmision DESC")
    List<Comprobante> findByClienteId(@Param("clienteId") Long clienteId);

    @Query("""
            SELECT c FROM Comprobante c
            WHERE c.fechaEmision BETWEEN :desde AND :hasta
            AND (:tipo IS NULL OR c.tipoComprobante = :tipo)
            AND c.anulado = false
            ORDER BY c.fechaEmision DESC
            """)
    List<Comprobante> findByFiltros(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta,
            @Param("tipo") TipoComprobante tipo
    );
}
