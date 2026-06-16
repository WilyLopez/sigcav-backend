package com.jmcavel.sigcav.controller;

import com.jmcavel.sigcav.dto.request.GastoPedidoRequest;
import com.jmcavel.sigcav.dto.response.CompraPedidoResponse;
import com.jmcavel.sigcav.dto.response.GastoPedidoResponse;
import com.jmcavel.sigcav.entity.Usuario;
import com.jmcavel.sigcav.service.CompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class GastoPedidoController {

    private final CompraService compraService;

    @PostMapping("/{pedidoId}/gastos")
    public ResponseEntity<GastoPedidoResponse> registrarGasto(
            @PathVariable Long pedidoId,
            @Valid @RequestBody GastoPedidoRequest solicitud,
            @AuthenticationPrincipal Usuario usuarioActual) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(compraService.registrarGasto(pedidoId, solicitud, usuarioActual));
    }

    @GetMapping("/{pedidoId}/gastos")
    public ResponseEntity<List<GastoPedidoResponse>> listarGastos(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(compraService.listarGastosPorPedido(pedidoId));
    }

    @GetMapping("/{pedidoId}/asignaciones")
    public ResponseEntity<List<CompraPedidoResponse>> listarAsignaciones(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(compraService.listarAsignacionesPorPedido(pedidoId));
    }

    @PutMapping("/gastos/{gastoId}")
    public ResponseEntity<GastoPedidoResponse> editarGasto(
            @PathVariable Long gastoId,
            @Valid @RequestBody GastoPedidoRequest solicitud,
            @AuthenticationPrincipal Usuario usuarioActual) {
        return ResponseEntity.ok(compraService.editarGasto(gastoId, solicitud, usuarioActual));
    }

    @DeleteMapping("/gastos/{gastoId}")
    public ResponseEntity<Void> eliminarGasto(
            @PathVariable Long gastoId,
            @AuthenticationPrincipal Usuario usuarioActual) {
        compraService.eliminarGasto(gastoId, usuarioActual);
        return ResponseEntity.noContent().build();
    }
}