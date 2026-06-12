package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    List<Compra> findByProveedorIdOrderByFechaCompraDesc(Long proveedorId);

    List<Compra> findByFechaCompraBetweenOrderByFechaCompraDesc(LocalDate desde, LocalDate hasta);

    @Query("""
            SELECT c FROM Compra c
            WHERE (:proveedorId IS NULL OR c.proveedor.id = :proveedorId)
              AND (:desde IS NULL OR c.fechaCompra >= :desde)
              AND (:hasta IS NULL OR c.fechaCompra <= :hasta)
            ORDER BY c.fechaCompra DESC
            """)
    List<Compra> buscarConFiltros(
            @Param("proveedorId") Long proveedorId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );
}