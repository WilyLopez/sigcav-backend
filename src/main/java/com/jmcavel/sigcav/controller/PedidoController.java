package com.jmcavel.sigcav.controller;

import com.jmcavel.sigcav.dto.request.ActualizarPedidoRequest;
import com.jmcavel.sigcav.dto.request.CambiarEstadoPedidoRequest;
import com.jmcavel.sigcav.dto.request.CrearNotaInternaRequest;
import com.jmcavel.sigcav.dto.request.CrearPedidoRequest;
import com.jmcavel.sigcav.dto.response.*;
import com.jmcavel.sigcav.enums.EstadoPedido;
import com.jmcavel.sigcav.security.UsuarioPrincipal;
import com.jmcavel.sigcav.service.PedidoService;
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
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<ApiResponse<PedidoResponse>> crear(
            @Valid @RequestBody CrearPedidoRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal
    ) {
        PedidoResponse response = pedidoService.crear(request, principal.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Pedido creado exitosamente", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PedidoFichaResponse>> obtenerFicha(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.exito("Pedido obtenido exitosamente", pedidoService.obtenerFicha(id))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PedidoResponse>>> buscar(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) EstadoPedido estado,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desdeIngreso,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hastaIngreso,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desdeEntrega,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hastaEntrega,
            @PageableDefault(size = 20, sort = "fechaIngreso", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<PedidoResponse> resultado = pedidoService.buscarConFiltros(
                clienteId, estado, categoriaId,
                desdeIngreso, hastaIngreso,
                desdeEntrega, hastaEntrega,
                pageable
        );
        return ResponseEntity.ok(ApiResponse.exito("Pedidos obtenidos exitosamente", resultado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PedidoResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarPedidoRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal
    ) {
        return ResponseEntity.ok(
                ApiResponse.exito("Pedido actualizado exitosamente",
                        pedidoService.actualizar(id, request, principal.getId()))
        );
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<PedidoResponse>> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoPedidoRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal
    ) {
        return ResponseEntity.ok(
                ApiResponse.exito("Estado actualizado exitosamente",
                        pedidoService.cambiarEstado(id, request, principal.getId()))
        );
    }

    @PostMapping("/{id}/notas")
    public ResponseEntity<ApiResponse<NotaInternaPedidoResponse>> agregarNota(
            @PathVariable Long id,
            @Valid @RequestBody CrearNotaInternaRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal
    ) {
        NotaInternaPedidoResponse response = pedidoService.agregarNota(id, request, principal.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Nota agregada exitosamente", response));
    }
}