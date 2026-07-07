package com.jmcavel.sigcav.controller;

import com.jmcavel.sigcav.dto.request.CategoriaProveedorRequest;
import com.jmcavel.sigcav.dto.response.CategoriaProveedorResponse;
import com.jmcavel.sigcav.service.CategoriaProveedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias-proveedor")
@RequiredArgsConstructor
public class CategoriaProveedorController {

    private final CategoriaProveedorService categoriaProveedorService;

    @GetMapping
    public ResponseEntity<List<CategoriaProveedorResponse>> listarActivas() {
        return ResponseEntity.ok(categoriaProveedorService.listarActivas());
    }

    @GetMapping("/todas")
    public ResponseEntity<List<CategoriaProveedorResponse>> listarTodas() {
        return ResponseEntity.ok(categoriaProveedorService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaProveedorResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaProveedorService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<CategoriaProveedorResponse> crear(@Valid @RequestBody CategoriaProveedorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaProveedorService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaProveedorResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaProveedorRequest request) {
        return ResponseEntity.ok(categoriaProveedorService.actualizar(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        categoriaProveedorService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        categoriaProveedorService.activar(id);
        return ResponseEntity.noContent().build();
    }
}