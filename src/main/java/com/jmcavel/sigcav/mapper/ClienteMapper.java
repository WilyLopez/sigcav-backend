package com.jmcavel.sigcav.mapper;

import com.jmcavel.sigcav.dto.request.ClienteRequest;
import com.jmcavel.sigcav.dto.response.ClienteDetalleResponse;
import com.jmcavel.sigcav.dto.response.ClienteResumenResponse;
import com.jmcavel.sigcav.entity.Cliente;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ClienteMapper {

    private final ContactoClienteMapper contactoClienteMapper;

    public Cliente toEntity(ClienteRequest request) {
        return Cliente.builder()
                .tipoDocumento(request.getTipoDocumento())
                .numeroDocumento(request.getNumeroDocumento().trim())
                .nombreRazonSocial(request.getNombreRazonSocial().trim())
                .telefonoPrincipal(request.getTelefonoPrincipal().trim())
                .telefonoSecundario(request.getTelefonoSecundario())
                .correo(request.getCorreo().trim().toLowerCase())
                .direccionEntrega(request.getDireccionEntrega().trim())
                .nombreContactoRef(request.getNombreContactoRef())
                .observaciones(request.getObservaciones())
                .build();
    }

    public void actualizarDesdeRequest(ClienteRequest request, Cliente entidad) {
        entidad.setTipoDocumento(request.getTipoDocumento());
        entidad.setNumeroDocumento(request.getNumeroDocumento().trim());
        entidad.setNombreRazonSocial(request.getNombreRazonSocial().trim());
        entidad.setTelefonoPrincipal(request.getTelefonoPrincipal().trim());
        entidad.setTelefonoSecundario(request.getTelefonoSecundario());
        entidad.setCorreo(request.getCorreo().trim().toLowerCase());
        entidad.setDireccionEntrega(request.getDireccionEntrega().trim());
        entidad.setNombreContactoRef(request.getNombreContactoRef());
        entidad.setObservaciones(request.getObservaciones());
    }

    public ClienteResumenResponse toResumenResponse(Cliente entidad) {
        return ClienteResumenResponse.builder()
                .id(entidad.getId())
                .tipoDocumento(entidad.getTipoDocumento())
                .numeroDocumento(entidad.getNumeroDocumento())
                .nombreRazonSocial(entidad.getNombreRazonSocial())
                .telefonoPrincipal(entidad.getTelefonoPrincipal())
                .correo(entidad.getCorreo())
                .activo(entidad.getActivo())
                .build();
    }

    public ClienteDetalleResponse toDetalleResponse(Cliente entidad) {
        return ClienteDetalleResponse.builder()
                .id(entidad.getId())
                .tipoDocumento(entidad.getTipoDocumento())
                .numeroDocumento(entidad.getNumeroDocumento())
                .nombreRazonSocial(entidad.getNombreRazonSocial())
                .telefonoPrincipal(entidad.getTelefonoPrincipal())
                .telefonoSecundario(entidad.getTelefonoSecundario())
                .correo(entidad.getCorreo())
                .direccionEntrega(entidad.getDireccionEntrega())
                .nombreContactoRef(entidad.getNombreContactoRef())
                .observaciones(entidad.getObservaciones())
                .activo(entidad.getActivo())
                .creadoEn(entidad.getCreadoEn())
                .actualizadoEn(entidad.getActualizadoEn())
                .contactos(
                        entidad.getContactos() != null
                                ? entidad.getContactos().stream()
                                        .map(contactoClienteMapper::toResponse)
                                        .collect(Collectors.toList())
                                : Collections.emptyList()
                )
                .build();
    }
}