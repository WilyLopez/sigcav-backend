package com.jmcavel.sigcav.service;

import com.jmcavel.sigcav.dto.request.ContactoClienteRequest;
import com.jmcavel.sigcav.dto.response.ContactoClienteResponse;
import com.jmcavel.sigcav.entity.Cliente;
import com.jmcavel.sigcav.entity.ContactoCliente;
import com.jmcavel.sigcav.enums.AccionAuditoria;
import com.jmcavel.sigcav.enums.EntidadAuditoria;
import com.jmcavel.sigcav.exception.RecursoNoEncontradoException;
import com.jmcavel.sigcav.exception.ReglaNegocioException;
import com.jmcavel.sigcav.mapper.ContactoClienteMapper;
import com.jmcavel.sigcav.repository.ClienteRepository;
import com.jmcavel.sigcav.repository.ContactoClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactoClienteService {

    private final ContactoClienteRepository contactoClienteRepository;
    private final ClienteRepository clienteRepository;
    private final ContactoClienteMapper contactoClienteMapper;
    private final AuditoriaService auditoriaService;

    @Transactional(readOnly = true)
    public List<ContactoClienteResponse> listarPorCliente(Long clienteId) {
        verificarClienteExiste(clienteId);
        return contactoClienteRepository.findAllByClienteId(clienteId)
                .stream()
                .map(contactoClienteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ContactoClienteResponse obtenerPorId(Long clienteId, Long contactoId) {
        return contactoClienteMapper.toResponse(buscarEntidad(clienteId, contactoId));
    }

    @Transactional
    public ContactoClienteResponse crear(Long clienteId, ContactoClienteRequest request) {
        Cliente cliente = clienteRepository.findByIdAndActivoTrue(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: " + clienteId));
        validarPrincipalUnico(clienteId, null, request.getEsPrincipal());
        ContactoCliente entidad = contactoClienteMapper.toEntity(request, cliente);
        ContactoCliente guardado = contactoClienteRepository.save(entidad);

        auditoriaService.registrar(
                null,
                AccionAuditoria.CREAR,
                EntidadAuditoria.CONTACTO_CLIENTE,
                guardado.getId(),
                "Contacto creado para cliente id: " + clienteId
        );

        return contactoClienteMapper.toResponse(guardado);
    }

    @Transactional
    public ContactoClienteResponse actualizar(Long clienteId, Long contactoId, ContactoClienteRequest request) {
        verificarClienteExiste(clienteId);
        ContactoCliente entidad = buscarEntidad(clienteId, contactoId);
        validarPrincipalUnico(clienteId, contactoId, request.getEsPrincipal());
        contactoClienteMapper.actualizarDesdeRequest(request, entidad);
        ContactoCliente guardado = contactoClienteRepository.save(entidad);

        auditoriaService.registrar(
                null,
                AccionAuditoria.EDITAR,
                EntidadAuditoria.CONTACTO_CLIENTE,
                contactoId,
                "Contacto actualizado para cliente id: " + clienteId
        );

        return contactoClienteMapper.toResponse(guardado);
    }

    @Transactional
    public void eliminar(Long clienteId, Long contactoId) {
        verificarClienteExiste(clienteId);
        ContactoCliente entidad = buscarEntidad(clienteId, contactoId);
        long totalContactos = contactoClienteRepository.countByClienteId(clienteId);
        if (totalContactos <= 1) {
            throw new ReglaNegocioException("No se puede eliminar el único contacto del cliente");
        }
        contactoClienteRepository.delete(entidad);

        auditoriaService.registrar(
                null,
                AccionAuditoria.ELIMINAR,
                EntidadAuditoria.CONTACTO_CLIENTE,
                contactoId,
                "Contacto eliminado para cliente id: " + clienteId
        );
    }

    private ContactoCliente buscarEntidad(Long clienteId, Long contactoId) {
        return contactoClienteRepository.findByIdAndClienteId(contactoId, clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Contacto no encontrado con id: " + contactoId + " para el cliente id: " + clienteId));
    }

    private void verificarClienteExiste(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new RecursoNoEncontradoException("Cliente no encontrado con id: " + clienteId);
        }
    }

    private void validarPrincipalUnico(Long clienteId, Long contactoIdExcluido, Boolean esPrincipal) {
        if (Boolean.TRUE.equals(esPrincipal)) {
            boolean yaExistePrincipal = contactoClienteRepository.existsByClienteIdAndEsPrincipalTrue(clienteId);
            if (yaExistePrincipal && contactoIdExcluido == null) {
                throw new ReglaNegocioException("El cliente ya tiene un contacto principal. Actualice el existente primero");
            }
        }
    }
}