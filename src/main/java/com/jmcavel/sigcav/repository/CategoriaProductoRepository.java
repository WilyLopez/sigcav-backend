package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.CategoriaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaProductoRepository extends JpaRepository<CategoriaProducto, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    Optional<CategoriaProducto> findByNombre(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    Optional<CategoriaProducto> findByIdAndActivoTrue(Long id);

    List<CategoriaProducto> findAllByActivoTrueOrderByNombreAsc();
}