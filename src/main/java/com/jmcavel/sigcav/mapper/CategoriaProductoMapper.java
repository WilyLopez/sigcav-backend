package com.jmcavel.sigcav.mapper;

import com.jmcavel.sigcav.dto.request.CategoriaProductoRequest;
import com.jmcavel.sigcav.dto.response.CategoriaProductoResponse;
import com.jmcavel.sigcav.entity.CategoriaProducto;
import org.springframework.stereotype.Component;

@Component
public class CategoriaProductoMapper {

    public CategoriaProducto toEntity(CategoriaProductoRequest request) {
        return CategoriaProducto.builder()
                .nombre(request.getNombre().trim())
                .build();
    }

    public void actualizarDesdeRequest(CategoriaProductoRequest request, CategoriaProducto entidad) {
        entidad.setNombre(request.getNombre().trim());
    }

    public CategoriaProductoResponse toResponse(CategoriaProducto entidad) {
        return CategoriaProductoResponse.builder()
                .id(entidad.getId())
                .nombre(entidad.getNombre())
                .activo(entidad.getActivo())
                .creadoEn(entidad.getCreadoEn())
                .build();
    }
}