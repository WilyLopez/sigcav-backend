package com.jmcavel.sigcav.controller;

import com.jmcavel.sigcav.dto.request.ContactoClienteRequest;
import com.jmcavel.sigcav.dto.response.ContactoClienteResponse;
import com.jmcavel.sigcav.service.ContactoClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes/{clienteId}/contactos")
@RequiredArgsConstructor
public class ContactoClienteController {

    private final ContactoClienteService contactoClienteService;

    @GetMapping
    public ResponseEntity<List<ContactoClienteResponse>> listar(@PathVariable Long clienteId) {
        return ResponseEntity.ok(contactoClienteService.listarPorCliente(clienteId));
    }

    @GetMapping("/{contactoId}")
    public ResponseEntity<ContactoClienteResponse> obtenerPorId(
            @PathVariable Long clienteId,
            @PathVariable Long contactoId) {
        return ResponseEntity.ok(contactoClienteService.obtenerPorId(clienteId, contactoId));
    }

    @PostMapping
    public ResponseEntity<ContactoClienteResponse> crear(
            @PathVariable Long clienteId,
            @Valid @RequestBody ContactoClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contactoClienteService.crear(clienteId, request));
    }

    @PutMapping("/{contactoId}")
    public ResponseEntity<ContactoClienteResponse> actualizar(
            @PathVariable Long clienteId,
            @PathVariable Long contactoId,
            @Valid @RequestBody ContactoClienteRequest request) {
        return ResponseEntity.ok(contactoClienteService.actualizar(clienteId, contactoId, request));
    }

    @DeleteMapping("/{contactoId}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long clienteId,
            @PathVariable Long contactoId) {
        contactoClienteService.eliminar(clienteId, contactoId);
        return ResponseEntity.noContent().build();
    }
}