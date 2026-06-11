package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByNumeroDocumento(String numeroDocumento);

    boolean existsByNumeroDocumentoAndIdNot(String numeroDocumento, Long id);

    Optional<Cliente> findByIdAndActivoTrue(Long id);

    @Query("""
            SELECT c FROM Cliente c
            WHERE c.activo = true
            AND (
                LOWER(c.nombreRazonSocial) LIKE LOWER(CONCAT('%', :termino, '%'))
                OR c.numeroDocumento LIKE CONCAT('%', :termino, '%')
            )
            """)
    Page<Cliente> buscarActivosPorTermino(@Param("termino") String termino, Pageable pageable);

    @Query("""
            SELECT c FROM Cliente c
            LEFT JOIN FETCH c.contactos
            WHERE c.id = :id
            """)
    Optional<Cliente> buscarConContactos(@Param("id") Long id);

    @Query("""
            SELECT COALESCE(SUM(p.precioVenta), 0)
            FROM Pedido p
            WHERE p.cliente.id = :clienteId
            AND p.estado NOT IN ('ANULADO')
            """)
    java.math.BigDecimal calcularMontoAcumuladoVentas(@Param("clienteId") Long clienteId);
}