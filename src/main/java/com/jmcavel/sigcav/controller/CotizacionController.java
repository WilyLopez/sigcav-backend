package com.jmcavel.sigcav.controller;

import com.jmcavel.sigcav.dto.request.ActualizarCotizacionRequest;
import com.jmcavel.sigcav.dto.request.CambiarEstadoCotizacionRequest;
import com.jmcavel.sigcav.dto.request.CrearCotizacionRequest;
import com.jmcavel.sigcav.dto.response.ApiResponse;
import com.jmcavel.sigcav.dto.response.CotizacionResponse;
import com.jmcavel.sigcav.enums.EstadoCotizacion;
import com.jmcavel.sigcav.security.UsuarioPrincipal;
import com.jmcavel.sigcav.service.CotizacionService;
import com.jmcavel.sigcav.service.PedidoService;
import com.jmcavel.sigcav.dto.response.PedidoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/cotizaciones")
@RequiredArgsConstructor
public class CotizacionController {

    private final CotizacionService cotizacionService;
    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<ApiResponse<CotizacionResponse>> crear(
            @Valid @RequestBody CrearCotizacionRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal
    ) {
        CotizacionResponse response = cotizacionService.crear(request, principal.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Cotización creada exitosamente", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CotizacionResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.exito("Cotización obtenida exitosamente", cotizacionService.obtenerPorId(id))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CotizacionResponse>>> buscar(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) EstadoCotizacion estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @PageableDefault(size = 20, sort = "fechaEmision", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CotizacionResponse> resultado = cotizacionService.buscarConFiltros(
                clienteId, estado, desde, hasta, pageable
        );
        return ResponseEntity.ok(ApiResponse.exito("Cotizaciones obtenidas exitosamente", resultado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CotizacionResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarCotizacionRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal
    ) {
        return ResponseEntity.ok(
                ApiResponse.exito("Cotización actualizada exitosamente",
                        cotizacionService.actualizar(id, request, principal.getId()))
        );
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<CotizacionResponse>> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoCotizacionRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal
    ) {
        return ResponseEntity.ok(
                ApiResponse.exito("Estado actualizado exitosamente",
                        cotizacionService.cambiarEstado(id, request, principal.getId()))
        );
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<ApiResponse<CotizacionResponse>> reactivar(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate nuevaFechaVencimiento,
            @AuthenticationPrincipal UsuarioPrincipal principal
    ) {
        return ResponseEntity.ok(
                ApiResponse.exito("Cotización reactivada exitosamente",
                        cotizacionService.reactivarVencida(id, nuevaFechaVencimiento, principal.getId()))
        );
    }

    @PostMapping("/{id}/duplicar")
    public ResponseEntity<ApiResponse<CotizacionResponse>> duplicar(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioPrincipal principal
    ) {
        CotizacionResponse response = cotizacionService.duplicar(id, principal.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Cotización duplicada exitosamente", response));
    }

    @PostMapping("/{id}/convertir")
    public ResponseEntity<ApiResponse<PedidoResponse>> convertirAPedido(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioPrincipal principal
    ) {
        PedidoResponse response = pedidoService.convertirDeCotizacion(id, principal.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Cotización convertida en pedido exitosamente", response));
    }
}