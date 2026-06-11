package com.jmcavel.sigcav.dto.request;

import com.jmcavel.sigcav.enums.TipoDocumento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ClienteRequest {

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

    @NotBlank(message = "El teléfono principal es obligatorio")
    @Size(max = 20, message = "El teléfono principal no puede superar los 20 caracteres")
    private String telefonoPrincipal;

    @Size(max = 20, message = "El teléfono secundario no puede superar los 20 caracteres")
    private String telefonoSecundario;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 150, message = "El correo no puede superar los 150 caracteres")
    private String correo;

    @NotBlank(message = "La dirección de entrega es obligatoria")
    @Size(max = 300, message = "La dirección no puede superar los 300 caracteres")
    private String direccionEntrega;

    @Size(max = 200, message = "El nombre de contacto de referencia no puede superar los 200 caracteres")
    private String nombreContactoRef;

    private String observaciones;

    @Valid
    private List<ContactoClienteRequest> contactos = new ArrayList<>();
}