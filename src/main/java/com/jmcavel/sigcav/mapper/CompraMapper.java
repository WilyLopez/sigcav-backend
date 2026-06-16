package com.jmcavel.sigcav.mapper;

import com.jmcavel.sigcav.dto.response.CompraResponse;
import com.jmcavel.sigcav.dto.response.ItemCompraResponse;
import com.jmcavel.sigcav.entity.Compra;
import com.jmcavel.sigcav.entity.ItemCompra;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompraMapper {

    public ItemCompraResponse toItemResponse(ItemCompra item) {
        return ItemCompraResponse.builder()
                .id(item.getId())
                .nombreMaterial(item.getNombreMaterial())
                .unidadMedida(item.getUnidadMedida())
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitario())
                .subtotal(item.getSubtotal())
                .creadoEn(item.getCreadoEn())
                .build();
    }

    public CompraResponse toResponse(Compra compra, List<ItemCompra> items) {
        return CompraResponse.builder()
                .id(compra.getId())
                .proveedorId(compra.getProveedor().getId())
                .proveedorNombre(compra.getProveedor().getNombreRazonSocial())
                .fechaCompra(compra.getFechaCompra())
                .numeroComprobanteProveedor(compra.getNumeroComprobanteProveedor())
                .tipoComprobanteProveedor(compra.getTipoComprobanteProveedor())
                .total(compra.getTotal())
                .observaciones(compra.getObservaciones())
                .registradoPorId(compra.getRegistradoPor() != null ? compra.getRegistradoPor().getId() : null)
                .creadoEn(compra.getCreadoEn())
                .actualizadoEn(compra.getActualizadoEn())
                .items(items.stream().map(this::toItemResponse).toList())
                .build();
    }
}