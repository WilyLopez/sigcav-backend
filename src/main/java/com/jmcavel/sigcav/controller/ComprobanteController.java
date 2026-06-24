package com.jmcavel.sigcav.controller;

import com.jmcavel.sigcav.dto.request.AnulacionComprobanteRequest;
import com.jmcavel.sigcav.dto.request.ComprobanteRequest;
import com.jmcavel.sigcav.dto.response.AnulacionComprobanteResponse;
import com.jmcavel.sigcav.dto.response.ComprobanteResponse;
import com.jmcavel.sigcav.enums.TipoComprobante;
import com.jmcavel.sigcav.security.UsuarioPrincipal;
import com.jmcavel.sigcav.service.ComprobanteService;
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
@RequestMapping("/api/comprobantes")
@RequiredArgsConstructor
public class ComprobanteController {

    private final ComprobanteService comprobanteService;

    @PostMapping
    public ResponseEntity<ComprobanteResponse> emitirComprobante(
            @Valid @RequestBody ComprobanteRequest request,
            @AuthenticationPrincipal UsuarioPrincipal usuarioAutenticado) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(comprobanteService.emitirComprobante(request, usuarioAutenticado.getId()));
    }

    @PostMapping("/anular")
    public ResponseEntity<AnulacionComprobanteResponse> anularComprobante(
            @Valid @RequestBody AnulacionComprobanteRequest request,
            @AuthenticationPrincipal UsuarioPrincipal usuarioAutenticado) {
        return ResponseEntity.ok(comprobanteService.anularComprobante(request, usuarioAutenticado.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComprobanteResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(comprobanteService.obtenerPorId(id));
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<ComprobanteResponse> obtenerPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(comprobanteService.obtenerPorPedido(pedidoId));
    }

    @GetMapping
    public ResponseEntity<List<ComprobanteResponse>> listar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) TipoComprobante tipo) {
        return ResponseEntity.ok(comprobanteService.listarPorFiltros(desde, hasta, tipo));
    }
}