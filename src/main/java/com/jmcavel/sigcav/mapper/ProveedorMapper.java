package com.jmcavel.sigcav.mapper;

import com.jmcavel.sigcav.dto.request.ProveedorRequest;
import com.jmcavel.sigcav.dto.response.ProveedorDetalleResponse;
import com.jmcavel.sigcav.dto.response.ProveedorResumenResponse;
import com.jmcavel.sigcav.entity.CategoriaProveedor;
import com.jmcavel.sigcav.entity.Proveedor;
import org.springframework.stereotype.Component;

@Component
public class ProveedorMapper {

    public Proveedor toEntity(ProveedorRequest request, CategoriaProveedor categoria) {
        return Proveedor.builder()
                .tipoDocumento(request.getTipoDocumento())
                .numeroDocumento(request.getNumeroDocumento().trim())
                .nombreRazonSocial(request.getNombreRazonSocial().trim())
                .categoriaProveedor(categoria)
                .telefono(request.getTelefono().trim())
                .correo(request.getCorreo() != null ? request.getCorreo().trim().toLowerCase() : null)
                .direccion(request.getDireccion())
                .nombreRepresentante(request.getNombreRepresentante())
                .observaciones(request.getObservaciones())
                .build();
    }

    public void actualizarDesdeRequest(ProveedorRequest request, Proveedor entidad, CategoriaProveedor categoria) {
        entidad.setTipoDocumento(request.getTipoDocumento());
        entidad.setNumeroDocumento(request.getNumeroDocumento().trim());
        entidad.setNombreRazonSocial(request.getNombreRazonSocial().trim());
        entidad.setCategoriaProveedor(categoria);
        entidad.setTelefono(request.getTelefono().trim());
        entidad.setCorreo(request.getCorreo() != null ? request.getCorreo().trim().toLowerCase() : null);
        entidad.setDireccion(request.getDireccion());
        entidad.setNombreRepresentante(request.getNombreRepresentante());
        entidad.setObservaciones(request.getObservaciones());
    }

    public ProveedorResumenResponse toResumenResponse(Proveedor entidad) {
        return ProveedorResumenResponse.builder()
                .id(entidad.getId())
                .tipoDocumento(entidad.getTipoDocumento())
                .numeroDocumento(entidad.getNumeroDocumento())
                .nombreRazonSocial(entidad.getNombreRazonSocial())
                .categoriaNombre(entidad.getCategoriaProveedor().getNombre())
                .telefono(entidad.getTelefono())
                .activo(entidad.getActivo())
                .build();
    }

    public ProveedorDetalleResponse toDetalleResponse(Proveedor entidad) {
        return ProveedorDetalleResponse.builder()
                .id(entidad.getId())
                .tipoDocumento(entidad.getTipoDocumento())
                .numeroDocumento(entidad.getNumeroDocumento())
                .nombreRazonSocial(entidad.getNombreRazonSocial())
                .categoriaProveedorId(entidad.getCategoriaProveedor().getId())
                .categoriaNombre(entidad.getCategoriaProveedor().getNombre())
                .telefono(entidad.getTelefono())
                .correo(entidad.getCorreo())
                .direccion(entidad.getDireccion())
                .nombreRepresentante(entidad.getNombreRepresentante())
                .observaciones(entidad.getObservaciones())
                .activo(entidad.getActivo())
                .creadoEn(entidad.getCreadoEn())
                .actualizadoEn(entidad.getActualizadoEn())
                .build();
    }
}