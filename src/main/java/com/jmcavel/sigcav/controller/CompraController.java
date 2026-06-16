package com.jmcavel.sigcav.controller;

import com.jmcavel.sigcav.dto.request.CompraPedidoRequest;
import com.jmcavel.sigcav.dto.request.CompraRequest;
import com.jmcavel.sigcav.dto.request.GastoPedidoRequest;
import com.jmcavel.sigcav.dto.response.CompraPedidoResponse;
import com.jmcavel.sigcav.dto.response.CompraResponse;
import com.jmcavel.sigcav.dto.response.GastoPedidoResponse;
import com.jmcavel.sigcav.entity.Usuario;
import com.jmcavel.sigcav.service.CompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;

    @PostMapping
    public ResponseEntity<CompraResponse> registrarCompra(
            @Valid @RequestBody CompraRequest solicitud,
            @AuthenticationPrincipal Usuario usuarioActual) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(compraService.registrarCompra(solicitud, usuarioActual));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraResponse> obtenerCompra(@PathVariable Long id) {
        return ResponseEntity.ok(compraService.obtenerCompra(id));
    }

    @GetMapping
    public ResponseEntity<List<CompraResponse>> listarCompras(
            @RequestParam(required = false) Long proveedorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(compraService.listarCompras(proveedorId, desde, hasta));
    }

    @PostMapping("/{id}/asignaciones")
    public ResponseEntity<CompraPedidoResponse> asignarCompraAPedido(
            @PathVariable Long id,
            @Valid @RequestBody CompraPedidoRequest solicitud,
            @AuthenticationPrincipal Usuario usuarioActual) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(compraService.asignarCompraAPedido(id, solicitud, usuarioActual));
    }

    @GetMapping("/{id}/asignaciones")
    public ResponseEntity<List<CompraPedidoResponse>> listarAsignacionesPorCompra(@PathVariable Long id) {
        return ResponseEntity.ok(compraService.listarAsignacionesPorCompra(id));
    }

    @PutMapping("/asignaciones/{asignacionId}")
    public ResponseEntity<CompraPedidoResponse> editarAsignacion(
            @PathVariable Long asignacionId,
            @Valid @RequestBody CompraPedidoRequest solicitud,
            @AuthenticationPrincipal Usuario usuarioActual) {
        return ResponseEntity.ok(compraService.editarAsignacionCompra(asignacionId, solicitud, usuarioActual));
    }

    @DeleteMapping("/asignaciones/{asignacionId}")
    public ResponseEntity<Void> eliminarAsignacion(
            @PathVariable Long asignacionId,
            @AuthenticationPrincipal Usuario usuarioActual) {
        compraService.eliminarAsignacionCompra(asignacionId, usuarioActual);
        return ResponseEntity.noContent().build();
    }
}