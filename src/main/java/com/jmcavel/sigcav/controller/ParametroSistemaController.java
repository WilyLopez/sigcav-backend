package com.jmcavel.sigcav.controller;

import com.jmcavel.sigcav.dto.request.ActualizarParametroRequest;
import com.jmcavel.sigcav.dto.response.ApiResponse;
import com.jmcavel.sigcav.dto.response.ParametroSistemaResponse;
import com.jmcavel.sigcav.security.UsuarioPrincipal;
import com.jmcavel.sigcav.service.ParametroSistemaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/configuracion")
@RequiredArgsConstructor
public class ParametroSistemaController {

    private final ParametroSistemaService parametroSistemaService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ParametroSistemaResponse>>> listarTodos() {
        List<ParametroSistemaResponse> parametros = parametroSistemaService.listarTodos();
        return ResponseEntity.ok(ApiResponse.exito("Parámetros obtenidos exitosamente", parametros));
    }

    @GetMapping("/{clave}")
    public ResponseEntity<ApiResponse<ParametroSistemaResponse>> obtenerPorClave(
            @PathVariable String clave
    ) {
        ParametroSistemaResponse response = parametroSistemaService.obtenerPorClave(clave);
        return ResponseEntity.ok(ApiResponse.exito("Parámetro obtenido exitosamente", response));
    }

    @PatchMapping("/{clave}")
    public ResponseEntity<ApiResponse<ParametroSistemaResponse>> actualizar(
            @PathVariable String clave,
            @Valid @RequestBody ActualizarParametroRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal
    ) {
        ParametroSistemaResponse response = parametroSistemaService.actualizar(clave, request, principal.getId());
        return ResponseEntity.ok(ApiResponse.exito("Parámetro actualizado exitosamente", response));
    }
}