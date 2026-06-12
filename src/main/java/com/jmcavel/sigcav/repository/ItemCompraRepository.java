package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.ItemCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemCompraRepository extends JpaRepository<ItemCompra, Long> {

    List<ItemCompra> findByCompraId(Long compraId);

    void deleteByCompraId(Long compraId);
}