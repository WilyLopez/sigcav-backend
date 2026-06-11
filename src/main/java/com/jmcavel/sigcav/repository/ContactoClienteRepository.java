package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.ContactoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactoClienteRepository extends JpaRepository<ContactoCliente, Long> {

    List<ContactoCliente> findAllByClienteId(Long clienteId);

    Optional<ContactoCliente> findByIdAndClienteId(Long id, Long clienteId);

    boolean existsByClienteIdAndEsPrincipalTrue(Long clienteId);

    long countByClienteId(Long clienteId);
}