package com.jmcavel.sigcav.mapper;

import com.jmcavel.sigcav.dto.response.PlantillaCotizacionResponse;
import com.jmcavel.sigcav.entity.PlantillaCotizacion;
import org.springframework.stereotype.Component;

@Component
public class PlantillaCotizacionMapper {

    public PlantillaCotizacionResponse toResponse(PlantillaCotizacion plantilla) {
        return PlantillaCotizacionResponse.builder()
                .id(plantilla.getId())
                .nombrePlantilla(plantilla.getNombrePlantilla())
                .descripcionProducto(plantilla.getDescripcionProducto())
                .tipoImpresion(plantilla.getTipoImpresion())
                .material(plantilla.getMaterial())
                .dimensiones(plantilla.getDimensiones())
                .acabados(plantilla.getAcabados())
                .condicionesPago(plantilla.getCondicionesPago())
                .observaciones(plantilla.getObservaciones())
                .activo(plantilla.getActivo())
                .creadoPorNombre(plantilla.getCreadoPor() != null
                        ? plantilla.getCreadoPor().getNombreCompleto()
                        : null)
                .creadoEn(plantilla.getCreadoEn())
                .build();
    }
}