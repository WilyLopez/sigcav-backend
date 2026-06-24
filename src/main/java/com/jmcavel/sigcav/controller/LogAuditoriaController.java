package com.jmcavel.sigcav.controller;

import com.jmcavel.sigcav.dto.response.ApiResponse;
import com.jmcavel.sigcav.dto.response.LogAuditoriaResponse;
import com.jmcavel.sigcav.enums.AccionAuditoria;
import com.jmcavel.sigcav.enums.EntidadAuditoria;
import com.jmcavel.sigcav.mapper.LogAuditoriaMapper;
import com.jmcavel.sigcav.repository.LogAuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/auditoria")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROL_ADMINISTRADOR')")
public class LogAuditoriaController {

    private final LogAuditoriaRepository logAuditoriaRepository;
    private final LogAuditoriaMapper logAuditoriaMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<LogAuditoriaResponse>>> listarPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio
    ) {
        PageRequest pageRequest = PageRequest.of(
                pagina,
                tamanio,
                Sort.by(Sort.Direction.DESC, "creadoEn")
        );

        Page<LogAuditoriaResponse> logs = logAuditoriaRepository
                .findByCreadoEnBetweenOrderByCreadoEnDesc(desde, hasta, pageRequest)
                .map(logAuditoriaMapper::toResponse);

        return ResponseEntity.ok(ApiResponse.exito("Logs obtenidos exitosamente", logs));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ApiResponse<List<LogAuditoriaResponse>>> listarPorUsuario(
            @PathVariable Long usuarioId
    ) {
        List<LogAuditoriaResponse> logs = logAuditoriaRepository
                .findByUsuarioIdOrderByCreadoEnDesc(usuarioId)
                .stream()
                .map(logAuditoriaMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.exito("Logs del usuario obtenidos exitosamente", logs));
    }

    @GetMapping("/entidad/{entidad}/{entidadId}")
    public ResponseEntity<ApiResponse<List<LogAuditoriaResponse>>> listarPorEntidad(
            @PathVariable EntidadAuditoria entidad,
            @PathVariable Long entidadId
    ) {
        List<LogAuditoriaResponse> logs = logAuditoriaRepository
                .findByEntidadAndEntidadIdOrderByCreadoEnDesc(entidad, entidadId)
                .stream()
                .map(logAuditoriaMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.exito("Logs de la entidad obtenidos exitosamente", logs));
    }

    @GetMapping("/accion/{accion}")
    public ResponseEntity<ApiResponse<Page<LogAuditoriaResponse>>> listarPorAccion(
            @PathVariable AccionAuditoria accion,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio
    ) {
        PageRequest pageRequest = PageRequest.of(
                pagina,
                tamanio,
                Sort.by(Sort.Direction.DESC, "creadoEn")
        );

        Page<LogAuditoriaResponse> logs = logAuditoriaRepository
                .findByAccionOrderByCreadoEnDesc(accion, pageRequest)
                .map(logAuditoriaMapper::toResponse);

        return ResponseEntity.ok(ApiResponse.exito("Logs obtenidos exitosamente", logs));
    }
}