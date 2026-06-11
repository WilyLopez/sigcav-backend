package com.jmcavel.sigcav.service;

import com.jmcavel.sigcav.dto.request.ActualizarParametroRequest;
import com.jmcavel.sigcav.dto.response.ParametroSistemaResponse;
import com.jmcavel.sigcav.entity.ParametroSistema;
import com.jmcavel.sigcav.entity.Usuario;
import com.jmcavel.sigcav.enums.AccionAuditoria;
import com.jmcavel.sigcav.enums.EntidadAuditoria;
import com.jmcavel.sigcav.exception.RecursoNoEncontradoException;
import com.jmcavel.sigcav.mapper.ParametroSistemaMapper;
import com.jmcavel.sigcav.repository.ParametroSistemaRepository;
import com.jmcavel.sigcav.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParametroSistemaService {

    private final ParametroSistemaRepository parametroSistemaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ParametroSistemaMapper parametroSistemaMapper;
    private final AuditoriaService auditoriaService;

    @Cacheable(value = "parametros", key = "#clave")
    @Transactional(readOnly = true)
    public String obtenerValor(String clave) {
        return parametroSistemaRepository.findByClave(clave)
                .map(ParametroSistema::getValor)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Parámetro no encontrado: " + clave
                ));
    }

    @Transactional(readOnly = true)
    public Double obtenerValorDecimal(String clave) {
        return Double.parseDouble(obtenerValor(clave));
    }

    @Transactional(readOnly = true)
    public Integer obtenerValorEntero(String clave) {
        return Integer.parseInt(obtenerValor(clave));
    }

    @Transactional(readOnly = true)
    public Boolean obtenerValorBooleano(String clave) {
        return Boolean.parseBoolean(obtenerValor(clave));
    }

    @Transactional(readOnly = true)
    public List<ParametroSistemaResponse> listarTodos() {
        return parametroSistemaRepository.findAll()
                .stream()
                .map(parametroSistemaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ParametroSistemaResponse obtenerPorClave(String clave) {
        ParametroSistema parametro = parametroSistemaRepository.findByClave(clave)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Parámetro no encontrado: " + clave
                ));
        return parametroSistemaMapper.toResponse(parametro);
    }

    @CacheEvict(value = "parametros", key = "#clave")
    @Transactional
    public ParametroSistemaResponse actualizar(String clave, ActualizarParametroRequest request, Long usuarioId) {
        ParametroSistema parametro = parametroSistemaRepository.findByClave(clave)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Parámetro no encontrado: " + clave
                ));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado: " + usuarioId
                ));

        String valorAnterior = parametro.getValor();
        parametro.setValor(request.getValor());
        parametro.setActualizadoPor(usuario);

        ParametroSistema guardado = parametroSistemaRepository.save(parametro);

        auditoriaService.registrar(
                usuarioId,
                AccionAuditoria.EDITAR,
                EntidadAuditoria.PARAMETRO_SISTEMA,
                guardado.getId(),
                "Clave: " + clave + " | Valor anterior: " + valorAnterior + " | Valor nuevo: " + request.getValor()
        );

        return parametroSistemaMapper.toResponse(guardado);
    }
}