package com.jmcavel.sigcav.service;

import com.jmcavel.sigcav.dto.request.ClienteRequest;
import com.jmcavel.sigcav.dto.request.ContactoClienteRequest;
import com.jmcavel.sigcav.dto.response.*;
import com.jmcavel.sigcav.entity.Cliente;
import com.jmcavel.sigcav.entity.ContactoCliente;
import com.jmcavel.sigcav.enums.AccionAuditoria;
import com.jmcavel.sigcav.enums.EntidadAuditoria;
import com.jmcavel.sigcav.enums.TipoDocumento;
import com.jmcavel.sigcav.exception.ConflictoException;
import com.jmcavel.sigcav.exception.RecursoNoEncontradoException;
import com.jmcavel.sigcav.exception.ReglaNegocioException;
import com.jmcavel.sigcav.mapper.ClienteFichaMapper;
import com.jmcavel.sigcav.mapper.ClienteMapper;
import com.jmcavel.sigcav.mapper.ContactoClienteMapper;
import com.jmcavel.sigcav.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final CotizacionRepository cotizacionRepository;
    private final PedidoRepository pedidoRepository;
    private final ComprobanteRepository comprobanteRepository;
    private final ClienteMapper clienteMapper;
    private final ContactoClienteMapper contactoClienteMapper;
    private final ClienteFichaMapper clienteFichaMapper;
    private final AuditoriaService auditoriaService;

    @Transactional(readOnly = true)
    public Page<ClienteResumenResponse> buscar(String termino, Pageable pageable) {
        return clienteRepository.buscarActivosPorTermino(termino, pageable)
                .map(clienteMapper::toResumenResponse);
    }

    @Transactional(readOnly = true)
    public ClienteDetalleResponse obtenerPorId(Long id) {
        Cliente cliente = clienteRepository.buscarConContactos(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: " + id));
        return clienteMapper.toDetalleResponse(cliente);
    }

    @Transactional(readOnly = true)
    public ClienteFichaResponse obtenerFicha(Long id) {
        Cliente cliente = clienteRepository.buscarConContactos(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: " + id));

        List<CotizacionResumenResponse> cotizaciones = cotizacionRepository
                .findAllByClienteIdOrderByFechaEmisionDesc(id)
                .stream()
                .map(clienteFichaMapper::toCotizacionResumen)
                .collect(Collectors.toList());

        List<PedidoResumenResponse> pedidos = pedidoRepository
                .findAllByClienteIdOrderByFechaIngresoDesc(id)
                .stream()
                .map(clienteFichaMapper::toPedidoResumen)
                .collect(Collectors.toList());

        List<ComprobanteResumenResponse> comprobantes = comprobanteRepository
                .findByClienteId(id)
                .stream()
                .map(clienteFichaMapper::toComprobanteResumen)
                .collect(Collectors.toList());

        return ClienteFichaResponse.builder()
                .id(cliente.getId())
                .tipoDocumento(cliente.getTipoDocumento())
                .numeroDocumento(cliente.getNumeroDocumento())
                .nombreRazonSocial(cliente.getNombreRazonSocial())
                .telefonoPrincipal(cliente.getTelefonoPrincipal())
                .telefonoSecundario(cliente.getTelefonoSecundario())
                .correo(cliente.getCorreo())
                .direccionEntrega(cliente.getDireccionEntrega())
                .nombreContactoRef(cliente.getNombreContactoRef())
                .observaciones(cliente.getObservaciones())
                .activo(cliente.getActivo())
                .creadoEn(cliente.getCreadoEn())
                .actualizadoEn(cliente.getActualizadoEn())
                .contactos(
                        cliente.getContactos().stream()
                                .map(contactoClienteMapper::toResponse)
                                .collect(Collectors.toList())
                )
                .cotizaciones(cotizaciones)
                .pedidos(pedidos)
                .comprobantes(comprobantes)
                .montoAcumuladoVentas(clienteRepository.calcularMontoAcumuladoVentas(id))
                .build();
    }

    @Transactional
    public ClienteDetalleResponse crear(ClienteRequest request) {
        validarDocumento(request);
        validarNumeroDocumentoUnico(request.getNumeroDocumento(), null);

        Cliente cliente = clienteMapper.toEntity(request);

        if (request.getContactos() != null && !request.getContactos().isEmpty()) {
            validarContactosPrincipal(request.getContactos());
            List<ContactoCliente> contactos = request.getContactos().stream()
                    .map(c -> contactoClienteMapper.toEntity(c, cliente))
                    .collect(Collectors.toList());
            cliente.getContactos().addAll(contactos);
        }

        Cliente guardado = clienteRepository.save(cliente);

        auditoriaService.registrar(
                null,
                AccionAuditoria.CREAR,
                EntidadAuditoria.CLIENTE,
                guardado.getId(),
                "Cliente creado: " + guardado.getNombreRazonSocial()
        );

        return clienteMapper.toDetalleResponse(guardado);
    }

    @Transactional
    public ClienteDetalleResponse actualizar(Long id, ClienteRequest request) {
        Cliente cliente = clienteRepository.buscarConContactos(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: " + id));

        validarDocumento(request);
        validarNumeroDocumentoUnico(request.getNumeroDocumento(), id);
        clienteMapper.actualizarDesdeRequest(request, cliente);

        Cliente guardado = clienteRepository.save(cliente);

        auditoriaService.registrar(
                null,
                AccionAuditoria.EDITAR,
                EntidadAuditoria.CLIENTE,
                id,
                "Cliente actualizado: " + guardado.getNombreRazonSocial()
        );

        return clienteMapper.toDetalleResponse(guardado);
    }

    @Transactional
    public void desactivar(Long id) {
        Cliente cliente = clienteRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: " + id));
        cliente.setActivo(false);
        clienteRepository.save(cliente);

        auditoriaService.registrar(
                null,
                AccionAuditoria.EDITAR,
                EntidadAuditoria.CLIENTE,
                id,
                "Cliente desactivado: " + cliente.getNombreRazonSocial()
        );
    }

    @Transactional
    public void activar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: " + id));
        cliente.setActivo(true);
        clienteRepository.save(cliente);

        auditoriaService.registrar(
                null,
                AccionAuditoria.EDITAR,
                EntidadAuditoria.CLIENTE,
                id,
                "Cliente activado: " + cliente.getNombreRazonSocial()
        );
    }

    public Cliente buscarEntidadActiva(Long id) {
        return clienteRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: " + id));
    }

    private void validarDocumento(ClienteRequest request) {
        TipoDocumento tipo = request.getTipoDocumento();
        String numero = request.getNumeroDocumento().trim();
        if (tipo == TipoDocumento.DNI && numero.length() != 8) {
            throw new ReglaNegocioException("El DNI debe tener exactamente 8 dígitos");
        }
        if (tipo == TipoDocumento.RUC && numero.length() != 11) {
            throw new ReglaNegocioException("El RUC debe tener exactamente 11 dígitos");
        }
    }

    private void validarNumeroDocumentoUnico(String numeroDocumento, Long idExcluido) {
        boolean existe = idExcluido == null
                ? clienteRepository.existsByNumeroDocumento(numeroDocumento)
                : clienteRepository.existsByNumeroDocumentoAndIdNot(numeroDocumento, idExcluido);
        if (existe) {
            throw new ConflictoException("Ya existe un cliente con el número de documento: " + numeroDocumento);
        }
    }

    private void validarContactosPrincipal(List<ContactoClienteRequest> contactos) {
        long principalesCount = contactos.stream()
                .filter(c -> Boolean.TRUE.equals(c.getEsPrincipal()))
                .count();
        if (principalesCount > 1) {
            throw new ReglaNegocioException("Solo se puede definir un contacto como principal");
        }
    }
}