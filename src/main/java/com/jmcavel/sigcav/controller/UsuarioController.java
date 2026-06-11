package com.jmcavel.sigcav.controller;

import com.jmcavel.sigcav.dto.request.ActualizarUsuarioRequest;
import com.jmcavel.sigcav.dto.request.CambioContrasenaRequest;
import com.jmcavel.sigcav.dto.request.RegistroUsuarioRequest;
import com.jmcavel.sigcav.dto.response.ApiResponse;
import com.jmcavel.sigcav.dto.response.UsuarioResponse;
import com.jmcavel.sigcav.security.UsuarioPrincipal;
import com.jmcavel.sigcav.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<ApiResponse<UsuarioResponse>> registrar(
            @Valid @RequestBody RegistroUsuarioRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal
    ) {
        UsuarioResponse response = usuarioService.registrar(request, principal.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Usuario registrado exitosamente", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioResponse>>> listarTodos() {
        List<UsuarioResponse> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(ApiResponse.exito("Usuarios obtenidos exitosamente", usuarios));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> obtenerPorId(@PathVariable Long id) {
        UsuarioResponse response = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.exito("Usuario obtenido exitosamente", response));
    }

    @GetMapping("/perfil")
    public ResponseEntity<ApiResponse<UsuarioResponse>> obtenerPerfil(
            @AuthenticationPrincipal UsuarioPrincipal principal
    ) {
        UsuarioResponse response = usuarioService.obtenerPorId(principal.getId());
        return ResponseEntity.ok(ApiResponse.exito("Perfil obtenido exitosamente", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarUsuarioRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal
    ) {
        UsuarioResponse response = usuarioService.actualizar(id, request, principal.getId());
        return ResponseEntity.ok(ApiResponse.exito("Usuario actualizado exitosamente", response));
    }

    @PatchMapping("/{id}/contrasena")
    public ResponseEntity<ApiResponse<Void>> cambiarContrasena(
            @PathVariable Long id,
            @Valid @RequestBody CambioContrasenaRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal
    ) {
        usuarioService.cambiarContrasena(id, request, principal.getId());
        return ResponseEntity.ok(ApiResponse.exito("Contraseña actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioPrincipal principal
    ) {
        usuarioService.desactivar(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.exito("Usuario desactivado exitosamente"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioPrincipal principal
    ) {
        usuarioService.activar(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.exito("Usuario activado exitosamente"));
    }
}