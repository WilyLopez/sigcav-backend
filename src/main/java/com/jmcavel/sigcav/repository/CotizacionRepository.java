package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.Cotizacion;
import com.jmcavel.sigcav.enums.EstadoCotizacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {

    Optional<Cotizacion> findByNumeroCotizacion(String numeroCotizacion);

    boolean existsByNumeroCotizacion(String numeroCotizacion);

    List<Cotizacion> findAllByClienteIdOrderByFechaEmisionDesc(Long clienteId);

    Page<Cotizacion> findByEstado(EstadoCotizacion estado, Pageable pageable);

    Page<Cotizacion> findByClienteIdAndEstado(Long clienteId, EstadoCotizacion estado, Pageable pageable);

    @Query("""
            SELECT c FROM Cotizacion c
            WHERE c.estado = 'ENVIADA'
            AND c.fechaVencimiento < :hoy
            AND c.convertidaEnPedido = false
            """)
    List<Cotizacion> findCotizacionesAVencer(@Param("hoy") LocalDate hoy);

    @Modifying
    @Query("""
            UPDATE Cotizacion c
            SET c.estado = 'VENCIDA'
            WHERE c.estado = 'ENVIADA'
            AND c.fechaVencimiento < :hoy
            AND c.convertidaEnPedido = false
            """)
    int actualizarCotizacionesVencidas(@Param("hoy") LocalDate hoy);

    @Query("""
            SELECT COUNT(c) FROM Cotizacion c
            WHERE c.cliente.id = :clienteId
            AND c.estado = :estado
            """)
    long contarPorClienteYEstado(@Param("clienteId") Long clienteId, @Param("estado") EstadoCotizacion estado);

    @Query("""
            SELECT c FROM Cotizacion c
            WHERE (:clienteId IS NULL OR c.cliente.id = :clienteId)
            AND (:estado IS NULL OR c.estado = :estado)
            AND (:desde IS NULL OR c.fechaEmision >= :desde)
            AND (:hasta IS NULL OR c.fechaEmision <= :hasta)
            """)
    Page<Cotizacion> buscarConFiltros(
            @Param("clienteId") Long clienteId,
            @Param("estado") EstadoCotizacion estado,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta,
            Pageable pageable
    );

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(c.numeroCotizacion, 12) AS int)), 0) FROM Cotizacion c WHERE c.numeroCotizacion LIKE :prefijo%")
    int obtenerUltimoCorrelativo(@Param("prefijo") String prefijo);
}