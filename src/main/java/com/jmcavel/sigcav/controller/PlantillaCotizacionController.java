package com.jmcavel.sigcav.controller;

import com.jmcavel.sigcav.dto.request.CrearPlantillaCotizacionRequest;
import com.jmcavel.sigcav.dto.response.ApiResponse;
import com.jmcavel.sigcav.dto.response.PlantillaCotizacionResponse;
import com.jmcavel.sigcav.security.UsuarioPrincipal;
import com.jmcavel.sigcav.service.PlantillaCotizacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plantillas-cotizacion")
@RequiredArgsConstructor
public class PlantillaCotizacionController {

    private final PlantillaCotizacionService plantillaService;

    @PostMapping
    public ResponseEntity<ApiResponse<PlantillaCotizacionResponse>> crear(
            @Valid @RequestBody CrearPlantillaCotizacionRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal
    ) {
        PlantillaCotizacionResponse response = plantillaService.crear(request, principal.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Plantilla creada exitosamente", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PlantillaCotizacionResponse>>> listarActivas() {
        return ResponseEntity.ok(
                ApiResponse.exito("Plantillas obtenidas exitosamente", plantillaService.listarActivas())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlantillaCotizacionResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.exito("Plantilla obtenida exitosamente", plantillaService.obtenerPorId(id))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        plantillaService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.exito("Plantilla desactivada exitosamente"));
    }
}