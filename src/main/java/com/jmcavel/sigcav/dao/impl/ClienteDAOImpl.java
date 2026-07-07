package com.jmcavel.sigcav.dao.impl;

import com.jmcavel.sigcav.dao.ClienteDAO;
import com.jmcavel.sigcav.entity.Cliente;
import com.jmcavel.sigcav.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ClienteDAOImpl implements ClienteDAO {

    private final ClienteRepository clienteRepository;

    @Override
    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    @Override
    public Optional<Cliente> findByIdAndActivoTrue(Long id) {
        return clienteRepository.findByIdAndActivoTrue(id);
    }

    @Override
    public Optional<Cliente> buscarConContactos(Long id) {
        return clienteRepository.buscarConContactos(id);
    }

    @Override
    public Page<Cliente> buscarActivosPorTermino(String termino, Pageable pageable) {
        return clienteRepository.buscarActivosPorTermino(termino, pageable);
    }

    @Override
    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    public boolean existsByNumeroDocumento(String numeroDocumento) {
        return clienteRepository.existsByNumeroDocumento(numeroDocumento);
    }

    @Override
    public boolean existsByNumeroDocumentoAndIdNot(String numeroDocumento, Long id) {
        return clienteRepository.existsByNumeroDocumentoAndIdNot(numeroDocumento, id);
    }

    @Override
    public BigDecimal calcularMontoAcumuladoVentas(Long clienteId) {
        return clienteRepository.calcularMontoAcumuladoVentas(clienteId);
    }
}
