package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.CategoriaProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaProveedorRepository extends JpaRepository<CategoriaProveedor, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    Optional<CategoriaProveedor> findByIdAndActivoTrue(Long id);

    List<CategoriaProveedor> findAllByActivoTrueOrderByNombreAsc();
}