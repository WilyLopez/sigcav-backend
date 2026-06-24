package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.Comprobante;
import com.jmcavel.sigcav.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Pedido, Long> {

    @Query("""
            SELECT c FROM Comprobante c
            WHERE c.anulado = false
            AND c.fechaEmision BETWEEN :desde AND :hasta
            ORDER BY c.fechaEmision DESC
            """)
    List<Comprobante> reporteVentas(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );

    @Query("""
            SELECT p FROM Pedido p
            WHERE p.fechaIngreso BETWEEN :desde AND :hasta
            AND p.estado <> 'ANULADO'
            ORDER BY p.margenGananciaPorcentaje DESC
            """)
    List<Pedido> reporteRentabilidad(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );

    @Query("""
            SELECT p.estado AS estado, COUNT(p) AS cantidad
            FROM Pedido p
            WHERE p.fechaIngreso BETWEEN :desde AND :hasta
            GROUP BY p.estado
            """)
    List<Object[]> reportePedidosPorEstado(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );

    @Query("""
            SELECT
                FUNCTION('TO_CHAR', c.fechaEmision, 'YYYY-MM') AS mes,
                SUM(c.subtotal) AS subtotal,
                SUM(c.igvMonto) AS igvTotal,
                SUM(c.total) AS totalFacturado,
                COUNT(c) AS cantidadComprobantes
            FROM Comprobante c
            WHERE c.anulado = false
            AND c.fechaEmision BETWEEN :desde AND :hasta
            GROUP BY FUNCTION('TO_CHAR', c.fechaEmision, 'YYYY-MM')
            ORDER BY mes ASC
            """)
    List<Object[]> reporteResumenFinancieroMensual(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );
}