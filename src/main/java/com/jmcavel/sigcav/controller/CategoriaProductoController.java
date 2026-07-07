package com.jmcavel.sigcav.controller;

import com.jmcavel.sigcav.dto.request.CategoriaProductoRequest;
import com.jmcavel.sigcav.dto.response.CategoriaProductoResponse;
import com.jmcavel.sigcav.service.CategoriaProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias-producto")
@RequiredArgsConstructor
public class CategoriaProductoController {

    private final CategoriaProductoService categoriaProductoService;

    @GetMapping
    public ResponseEntity<List<CategoriaProductoResponse>> listarActivas() {
        return ResponseEntity.ok(categoriaProductoService.listarActivas());
    }

    @GetMapping("/todas")
    public ResponseEntity<List<CategoriaProductoResponse>> listarTodas() {
        return ResponseEntity.ok(categoriaProductoService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaProductoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaProductoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<CategoriaProductoResponse> crear(@Valid @RequestBody CategoriaProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaProductoService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaProductoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaProductoRequest request) {
        return ResponseEntity.ok(categoriaProductoService.actualizar(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        categoriaProductoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        categoriaProductoService.activar(id);
        return ResponseEntity.noContent().build();
    }
}