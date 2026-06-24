package com.jmcavel.sigcav.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantillaCotizacionResponse {

    private Long id;
    private String nombrePlantilla;
    private String descripcionProducto;
    private String tipoImpresion;
    private String material;
    private String dimensiones;
    private String acabados;
    private String condicionesPago;
    private String observaciones;
    private Boolean activo;
    private String creadoPorNombre;
    private LocalDateTime creadoEn;
}