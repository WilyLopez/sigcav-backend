package com.jmcavel.sigcav.dto.request;

import com.jmcavel.sigcav.enums.TipoDocumento;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProveedorRequest {

    @NotNull(message = "El tipo de documento es obligatorio")
    private TipoDocumento tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Pattern(
        regexp = "^(\\d{8}|\\d{11})$",
        message = "El número de documento debe tener 8 dígitos (DNI) o 11 dígitos (RUC)"
    )
    private String numeroDocumento;

    @NotBlank(message = "El nombre o razón social es obligatorio")
    @Size(max = 200, message = "El nombre no puede superar los 200 caracteres")
    private String nombreRazonSocial;

    @NotNull(message = "La categoría del proveedor es obligatoria")
    private Long categoriaProveedorId;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
    private String telefono;

    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 150, message = "El correo no puede superar los 150 caracteres")
    private String correo;

    @Size(max = 300, message = "La dirección no puede superar los 300 caracteres")
    private String direccion;

    @Size(max = 200, message = "El nombre del representante no puede superar los 200 caracteres")
    private String nombreRepresentante;

    private String observaciones;
}