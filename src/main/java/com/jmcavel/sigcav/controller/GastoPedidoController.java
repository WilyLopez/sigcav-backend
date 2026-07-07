package com.jmcavel.sigcav.controller;

import com.jmcavel.sigcav.dto.request.GastoPedidoRequest;
import com.jmcavel.sigcav.dto.response.CompraPedidoResponse;
import com.jmcavel.sigcav.dto.response.GastoPedidoResponse;
import com.jmcavel.sigcav.security.UsuarioPrincipal;
import com.jmcavel.sigcav.service.CompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class GastoPedidoController {

    private final CompraService compraService;

    @PostMapping("/{pedidoId}/gastos")
    public ResponseEntity<GastoPedidoResponse> registrarGasto(
            @PathVariable Long pedidoId,
            @Valid @RequestBody GastoPedidoRequest solicitud,
            @AuthenticationPrincipal UsuarioPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(compraService.registrarGasto(pedidoId, solicitud, principal.getId()));
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
            @AuthenticationPrincipal UsuarioPrincipal principal) {
        return ResponseEntity.ok(compraService.editarGasto(gastoId, solicitud, principal.getId()));
    }

    @DeleteMapping("/gastos/{gastoId}")
    public ResponseEntity<Void> eliminarGasto(
            @PathVariable Long gastoId,
            @AuthenticationPrincipal UsuarioPrincipal principal) {
        compraService.eliminarGasto(gastoId, principal.getId());
        return ResponseEntity.noContent().build();
    }
}