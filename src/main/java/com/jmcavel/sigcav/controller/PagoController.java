package com.jmcavel.sigcav.controller;

import com.jmcavel.sigcav.dto.request.NotaCorreccionPagoRequest;
import com.jmcavel.sigcav.dto.request.PagoRequest;
import com.jmcavel.sigcav.dto.response.NotaCorreccionPagoResponse;
import com.jmcavel.sigcav.dto.response.PagoResponse;
import com.jmcavel.sigcav.security.UsuarioPrincipal;
import com.jmcavel.sigcav.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    public ResponseEntity<PagoResponse> registrarPago(
            @Valid @RequestBody PagoRequest request,
            @AuthenticationPrincipal UsuarioPrincipal usuarioAutenticado) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pagoService.registrarPago(request, usuarioAutenticado.getId()));
    }

    @PostMapping("/correcciones")
    public ResponseEntity<NotaCorreccionPagoResponse> corregirPago(
            @Valid @RequestBody NotaCorreccionPagoRequest request,
            @AuthenticationPrincipal UsuarioPrincipal usuarioAutenticado) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pagoService.corregirPago(request, usuarioAutenticado.getId()));
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<PagoResponse>> listarPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pagoService.listarPorPedido(pedidoId));
    }

    @GetMapping("/{pagoId}/correcciones")
    public ResponseEntity<List<NotaCorreccionPagoResponse>> listarCorrecciones(@PathVariable Long pagoId) {
        return ResponseEntity.ok(pagoService.listarCorrencionesPorPago(pagoId));
    }
}