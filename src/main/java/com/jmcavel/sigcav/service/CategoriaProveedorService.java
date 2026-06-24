package com.jmcavel.sigcav.service;

import com.jmcavel.sigcav.dto.request.CategoriaProveedorRequest;
import com.jmcavel.sigcav.dto.response.CategoriaProveedorResponse;
import com.jmcavel.sigcav.entity.CategoriaProveedor;
import com.jmcavel.sigcav.enums.AccionAuditoria;
import com.jmcavel.sigcav.enums.EntidadAuditoria;
import com.jmcavel.sigcav.exception.ConflictoException;
import com.jmcavel.sigcav.exception.RecursoNoEncontradoException;
import com.jmcavel.sigcav.mapper.CategoriaProveedorMapper;
import com.jmcavel.sigcav.repository.CategoriaProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaProveedorService {

    private final CategoriaProveedorRepository categoriaProveedorRepository;
    private final CategoriaProveedorMapper categoriaProveedorMapper;
    private final AuditoriaService auditoriaService;

    @Transactional(readOnly = true)
    public List<CategoriaProveedorResponse> listarActivas() {
        return categoriaProveedorRepository.findAllByActivoTrueOrderByNombreAsc()
                .stream()
                .map(categoriaProveedorMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoriaProveedorResponse> listarTodas() {
        return categoriaProveedorRepository.findAll()
                .stream()
                .map(categoriaProveedorMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoriaProveedorResponse obtenerPorId(Long id) {
        return categoriaProveedorMapper.toResponse(buscarEntidadActiva(id));
    }

    @Transactional
    public CategoriaProveedorResponse crear(CategoriaProveedorRequest request) {
        validarNombreUnico(request.getNombre(), null);
        CategoriaProveedor entidad = categoriaProveedorMapper.toEntity(request);
        CategoriaProveedor guardada = categoriaProveedorRepository.save(entidad);

        auditoriaService.registrar(
                null,
                AccionAuditoria.CREAR,
                EntidadAuditoria.CATEGORIA_PROVEEDOR,
                guardada.getId(),
                "Categoría creada: " + guardada.getNombre()
        );

        return categoriaProveedorMapper.toResponse(guardada);
    }

    @Transactional
    public CategoriaProveedorResponse actualizar(Long id, CategoriaProveedorRequest request) {
        CategoriaProveedor entidad = buscarEntidadActiva(id);
        validarNombreUnico(request.getNombre(), id);
        categoriaProveedorMapper.actualizarDesdeRequest(request, entidad);
        CategoriaProveedor guardada = categoriaProveedorRepository.save(entidad);

        auditoriaService.registrar(
                null,
                AccionAuditoria.EDITAR,
                EntidadAuditoria.CATEGORIA_PROVEEDOR,
                guardada.getId(),
                "Categoría actualizada: " + guardada.getNombre()
        );

        return categoriaProveedorMapper.toResponse(guardada);
    }

    @Transactional
    public void desactivar(Long id) {
        CategoriaProveedor entidad = buscarEntidadActiva(id);
        entidad.setActivo(false);
        categoriaProveedorRepository.save(entidad);

        auditoriaService.registrar(
                null,
                AccionAuditoria.EDITAR,
                EntidadAuditoria.CATEGORIA_PROVEEDOR,
                id,
                "Categoría desactivada: " + entidad.getNombre()
        );
    }

    @Transactional
    public void activar(Long id) {
        CategoriaProveedor entidad = categoriaProveedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría de proveedor no encontrada con id: " + id));
        entidad.setActivo(true);
        categoriaProveedorRepository.save(entidad);

        auditoriaService.registrar(
                null,
                AccionAuditoria.EDITAR,
                EntidadAuditoria.CATEGORIA_PROVEEDOR,
                id,
                "Categoría activada: " + entidad.getNombre()
        );
    }

    public CategoriaProveedor buscarEntidadActiva(Long id) {
        return categoriaProveedorRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría de proveedor no encontrada con id: " + id));
    }

    private void validarNombreUnico(String nombre, Long idExcluido) {
        boolean existe = idExcluido == null
                ? categoriaProveedorRepository.existsByNombreIgnoreCase(nombre)
                : categoriaProveedorRepository.existsByNombreIgnoreCaseAndIdNot(nombre, idExcluido);
        if (existe) {
            throw new ConflictoException("Ya existe una categoría de proveedor con el nombre: " + nombre);
        }
    }
}