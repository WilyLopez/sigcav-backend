package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CompraReporteRepository extends JpaRepository<Compra, Long> {

    @Query("""
            SELECT c FROM Compra c
            JOIN FETCH c.proveedor p
            WHERE c.fechaCompra BETWEEN :desde AND :hasta
            AND (:proveedorId IS NULL OR p.id = :proveedorId)
            ORDER BY p.nombreRazonSocial ASC, c.fechaCompra DESC
            """)
    List<Compra> reporteComprasPorProveedor(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta,
            @Param("proveedorId") Long proveedorId
    );
}