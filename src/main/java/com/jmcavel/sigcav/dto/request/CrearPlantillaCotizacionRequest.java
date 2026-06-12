package com.jmcavel.sigcav.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearPlantillaCotizacionRequest {

    @NotBlank(message = "El nombre de la plantilla es obligatorio")
    @Size(max = 200, message = "El nombre no puede superar los 200 caracteres")
    private String nombrePlantilla;

    @NotBlank(message = "La descripción del producto es obligatoria")
    private String descripcionProducto;

    private String tipoImpresion;
    private String material;
    private String dimensiones;
    private String acabados;
    private String condicionesPago;
    private String observaciones;
}