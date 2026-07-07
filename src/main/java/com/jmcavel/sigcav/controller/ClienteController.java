package com.jmcavel.sigcav.controller;

import com.jmcavel.sigcav.dto.request.ClienteRequest;
import com.jmcavel.sigcav.dto.response.ClienteDetalleResponse;
import com.jmcavel.sigcav.dto.response.ClienteFichaResponse;
import com.jmcavel.sigcav.dto.response.ClienteResumenResponse;
import com.jmcavel.sigcav.service.ClienteService;
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
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<Page<ClienteResumenResponse>> buscar(
            @RequestParam(defaultValue = "") String termino,
            @PageableDefault(size = 20, sort = "nombreRazonSocial", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(clienteService.buscar(termino, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDetalleResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtenerPorId(id));
    }

    @GetMapping("/{id}/ficha")
    public ResponseEntity<ClienteFichaResponse> obtenerFicha(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtenerFicha(id));
    }

    @PostMapping
    public ResponseEntity<ClienteDetalleResponse> crear(@Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDetalleResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(clienteService.actualizar(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        clienteService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        clienteService.activar(id);
        return ResponseEntity.noContent().build();
    }
}