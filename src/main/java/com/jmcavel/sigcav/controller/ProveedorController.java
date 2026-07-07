package com.jmcavel.sigcav.controller;

import com.jmcavel.sigcav.dto.request.ProveedorRequest;
import com.jmcavel.sigcav.dto.response.ProveedorDetalleResponse;
import com.jmcavel.sigcav.dto.response.ProveedorResumenResponse;
import com.jmcavel.sigcav.service.ProveedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping
    public ResponseEntity<Page<ProveedorResumenResponse>> buscar(
            @RequestParam(defaultValue = "") String termino,
            @PageableDefault(size = 20, sort = "nombreRazonSocial", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(proveedorService.buscar(termino, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorDetalleResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<ProveedorDetalleResponse> crear(@Valid @RequestBody ProveedorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorDetalleResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProveedorRequest request) {
        return ResponseEntity.ok(proveedorService.actualizar(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        proveedorService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        proveedorService.activar(id);
        return ResponseEntity.noContent().build();
    }
}