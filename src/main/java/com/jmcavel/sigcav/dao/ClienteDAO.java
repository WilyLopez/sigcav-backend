package com.jmcavel.sigcav.dao;

import com.jmcavel.sigcav.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

public interface ClienteDAO {
    Optional<Cliente> findById(Long id);
    Optional<Cliente> findByIdAndActivoTrue(Long id);
    Optional<Cliente> buscarConContactos(Long id);
    Page<Cliente> buscarActivosPorTermino(String termino, Pageable pageable);
    Cliente save(Cliente cliente);
    boolean existsByNumeroDocumento(String numeroDocumento);
    boolean existsByNumeroDocumentoAndIdNot(String numeroDocumento, Long id);
    BigDecimal calcularMontoAcumuladoVentas(Long clienteId);
}
