package com.jmcavel.sigcav.service;

import com.jmcavel.sigcav.dto.request.CategoriaProductoRequest;
import com.jmcavel.sigcav.dto.response.CategoriaProductoResponse;
import com.jmcavel.sigcav.entity.CategoriaProducto;
import com.jmcavel.sigcav.exception.ConflictoException;
import com.jmcavel.sigcav.exception.RecursoNoEncontradoException;
import com.jmcavel.sigcav.mapper.CategoriaProductoMapper;
import com.jmcavel.sigcav.repository.CategoriaProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaProductoService {

    private final CategoriaProductoRepository categoriaProductoRepository;
    private final CategoriaProductoMapper categoriaProductoMapper;
    private final AuditoriaService auditoriaService;

    @Transactional(readOnly = true)
    public List<CategoriaProductoResponse> listarActivas() {
        return categoriaProductoRepository.findAllByActivoTrueOrderByNombreAsc()
                .stream()
                .map(categoriaProductoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoriaProductoResponse> listarTodas() {
        return categoriaProductoRepository.findAll()
                .stream()
                .map(categoriaProductoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoriaProductoResponse obtenerPorId(Long id) {
        return categoriaProductoMapper.toResponse(buscarEntidadActiva(id));
    }

    @Transactional
    public CategoriaProductoResponse crear(CategoriaProductoRequest request) {
        validarNombreUnico(request.getNombre(), null);
        CategoriaProducto entidad = categoriaProductoMapper.toEntity(request);
        CategoriaProducto guardada = categoriaProductoRepository.save(entidad);
        auditoriaService.registrar("CREAR", "categoria_producto", guardada.getId(),
                "Categoría creada: " + guardada.getNombre());
        return categoriaProductoMapper.toResponse(guardada);
    }

    @Transactional
    public CategoriaProductoResponse actualizar(Long id, CategoriaProductoRequest request) {
        CategoriaProducto entidad = buscarEntidadActiva(id);
        validarNombreUnico(request.getNombre(), id);
        categoriaProductoMapper.actualizarDesdeRequest(request, entidad);
        CategoriaProducto guardada = categoriaProductoRepository.save(entidad);
        auditoriaService.registrar("ACTUALIZAR", "categoria_producto", guardada.getId(),
                "Categoría actualizada: " + guardada.getNombre());
        return categoriaProductoMapper.toResponse(guardada);
    }

    @Transactional
    public void desactivar(Long id) {
        CategoriaProducto entidad = buscarEntidadActiva(id);
        entidad.setActivo(false);
        categoriaProductoRepository.save(entidad);
        auditoriaService.registrar("DESACTIVAR", "categoria_producto", id,
                "Categoría desactivada: " + entidad.getNombre());
    }

    @Transactional
    public void activar(Long id) {
        CategoriaProducto entidad = categoriaProductoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría de producto no encontrada con id: " + id));
        entidad.setActivo(true);
        categoriaProductoRepository.save(entidad);
        auditoriaService.registrar("ACTIVAR", "categoria_producto", id,
                "Categoría activada: " + entidad.getNombre());
    }

    public CategoriaProducto buscarEntidadActiva(Long id) {
        return categoriaProductoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría de producto no encontrada con id: " + id));
    }

    private void validarNombreUnico(String nombre, Long idExcluido) {
        boolean existe = idExcluido == null
                ? categoriaProductoRepository.existsByNombreIgnoreCase(nombre)
                : categoriaProductoRepository.existsByNombreIgnoreCaseAndIdNot(nombre, idExcluido);
        if (existe) {
            throw new ConflictoException("Ya existe una categoría de producto con el nombre: " + nombre);
        }
    }
}