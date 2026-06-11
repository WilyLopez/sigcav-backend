package com.jmcavel.sigcav.mapper;

import com.jmcavel.sigcav.dto.request.CategoriaProveedorRequest;
import com.jmcavel.sigcav.dto.response.CategoriaProveedorResponse;
import com.jmcavel.sigcav.entity.CategoriaProveedor;
import org.springframework.stereotype.Component;

@Component
public class CategoriaProveedorMapper {

    public CategoriaProveedor toEntity(CategoriaProveedorRequest request) {
        return CategoriaProveedor.builder()
                .nombre(request.getNombre().trim())
                .build();
    }

    public void actualizarDesdeRequest(CategoriaProveedorRequest request, CategoriaProveedor entidad) {
        entidad.setNombre(request.getNombre().trim());
    }

    public CategoriaProveedorResponse toResponse(CategoriaProveedor entidad) {
        return CategoriaProveedorResponse.builder()
                .id(entidad.getId())
                .nombre(entidad.getNombre())
                .activo(entidad.getActivo())
                .creadoEn(entidad.getCreadoEn())
                .build();
    }
}