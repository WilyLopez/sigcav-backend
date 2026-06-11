package com.jmcavel.sigcav.mapper;

import com.jmcavel.sigcav.dto.request.ContactoClienteRequest;
import com.jmcavel.sigcav.dto.response.ContactoClienteResponse;
import com.jmcavel.sigcav.entity.Cliente;
import com.jmcavel.sigcav.entity.ContactoCliente;
import org.springframework.stereotype.Component;

@Component
public class ContactoClienteMapper {

    public ContactoCliente toEntity(ContactoClienteRequest request, Cliente cliente) {
        return ContactoCliente.builder()
                .cliente(cliente)
                .nombre(request.getNombre().trim())
                .telefono(request.getTelefono())
                .correo(request.getCorreo())
                .cargo(request.getCargo())
                .esPrincipal(request.getEsPrincipal() != null ? request.getEsPrincipal() : false)
                .build();
    }

    public void actualizarDesdeRequest(ContactoClienteRequest request, ContactoCliente entidad) {
        entidad.setNombre(request.getNombre().trim());
        entidad.setTelefono(request.getTelefono());
        entidad.setCorreo(request.getCorreo());
        entidad.setCargo(request.getCargo());
        entidad.setEsPrincipal(request.getEsPrincipal() != null ? request.getEsPrincipal() : false);
    }

    public ContactoClienteResponse toResponse(ContactoCliente entidad) {
        return ContactoClienteResponse.builder()
                .id(entidad.getId())
                .clienteId(entidad.getCliente().getId())
                .nombre(entidad.getNombre())
                .telefono(entidad.getTelefono())
                .correo(entidad.getCorreo())
                .cargo(entidad.getCargo())
                .esPrincipal(entidad.getEsPrincipal())
                .creadoEn(entidad.getCreadoEn())
                .build();
    }
}