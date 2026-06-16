package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    boolean existsByNumeroDocumento(String numeroDocumento);

    boolean existsByNumeroDocumentoAndIdNot(String numeroDocumento, Long id);

    Optional<Proveedor> findByIdAndActivoTrue(Long id);

    @Query("""
            SELECT p FROM Proveedor p
            WHERE p.activo = true
            AND (
                LOWER(p.nombreRazonSocial) LIKE LOWER(CONCAT('%', :termino, '%'))
                OR p.numeroDocumento LIKE CONCAT('%', :termino, '%')
            )
            """)
    Page<Proveedor> buscarActivosPorTermino(@Param("termino") String termino, Pageable pageable);

    @Query("""
            SELECT p FROM Proveedor p
            JOIN FETCH p.categoriaProveedor
            WHERE p.activo = true
            AND (
                LOWER(p.nombreRazonSocial) LIKE LOWER(CONCAT('%', :termino, '%'))
                OR p.numeroDocumento LIKE CONCAT('%', :termino, '%')
            )
            """)
    Page<Proveedor> buscarActivosConCategoriasPorTermino(@Param("termino") String termino, Pageable pageable);
}