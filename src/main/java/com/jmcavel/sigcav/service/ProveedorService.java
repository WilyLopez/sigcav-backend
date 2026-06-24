package com.jmcavel.sigcav.service;

import com.jmcavel.sigcav.dto.request.ProveedorRequest;
import com.jmcavel.sigcav.dto.response.ProveedorDetalleResponse;
import com.jmcavel.sigcav.dto.response.ProveedorResumenResponse;
import com.jmcavel.sigcav.entity.CategoriaProveedor;
import com.jmcavel.sigcav.entity.Proveedor;
import com.jmcavel.sigcav.enums.AccionAuditoria;
import com.jmcavel.sigcav.enums.EntidadAuditoria;
import com.jmcavel.sigcav.enums.TipoDocumento;
import com.jmcavel.sigcav.exception.ConflictoException;
import com.jmcavel.sigcav.exception.RecursoNoEncontradoException;
import com.jmcavel.sigcav.exception.ReglaNegocioException;
import com.jmcavel.sigcav.mapper.ProveedorMapper;
import com.jmcavel.sigcav.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final CategoriaProveedorService categoriaProveedorService;
    private final ProveedorMapper proveedorMapper;
    private final AuditoriaService auditoriaService;

    @Transactional(readOnly = true)
    public Page<ProveedorResumenResponse> buscar(String termino, Pageable pageable) {
        return proveedorRepository.buscarActivosConCategoriasPorTermino(termino, pageable)
                .map(proveedorMapper::toResumenResponse);
    }

    @Transactional(readOnly = true)
    public ProveedorDetalleResponse obtenerPorId(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con id: " + id));
        return proveedorMapper.toDetalleResponse(proveedor);
    }

    @Transactional
    public ProveedorDetalleResponse crear(ProveedorRequest request) {
        validarDocumento(request);
        validarNumeroDocumentoUnico(request.getNumeroDocumento(), null);
        CategoriaProveedor categoria = categoriaProveedorService.buscarEntidadActiva(request.getCategoriaProveedorId());
        Proveedor entidad = proveedorMapper.toEntity(request, categoria);
        Proveedor guardado = proveedorRepository.save(entidad);

        auditoriaService.registrar(
                null,
                AccionAuditoria.CREAR,
                EntidadAuditoria.PROVEEDOR,
                guardado.getId(),
                "Proveedor creado: " + guardado.getNombreRazonSocial()
        );

        return proveedorMapper.toDetalleResponse(guardado);
    }

    @Transactional
    public ProveedorDetalleResponse actualizar(Long id, ProveedorRequest request) {
        Proveedor entidad = proveedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con id: " + id));
        validarDocumento(request);
        validarNumeroDocumentoUnico(request.getNumeroDocumento(), id);
        CategoriaProveedor categoria = categoriaProveedorService.buscarEntidadActiva(request.getCategoriaProveedorId());
        proveedorMapper.actualizarDesdeRequest(request, entidad, categoria);
        Proveedor guardado = proveedorRepository.save(entidad);

        auditoriaService.registrar(
                null,
                AccionAuditoria.EDITAR,
                EntidadAuditoria.PROVEEDOR,
                id,
                "Proveedor actualizado: " + guardado.getNombreRazonSocial()
        );

        return proveedorMapper.toDetalleResponse(guardado);
    }

    @Transactional
    public void desactivar(Long id) {
        Proveedor entidad = proveedorRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con id: " + id));
        entidad.setActivo(false);
        proveedorRepository.save(entidad);

        auditoriaService.registrar(
                null,
                AccionAuditoria.EDITAR,
                EntidadAuditoria.PROVEEDOR,
                id,
                "Proveedor desactivado: " + entidad.getNombreRazonSocial()
        );
    }

    @Transactional
    public void activar(Long id) {
        Proveedor entidad = proveedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con id: " + id));
        entidad.setActivo(true);
        proveedorRepository.save(entidad);

        auditoriaService.registrar(
                null,
                AccionAuditoria.EDITAR,
                EntidadAuditoria.PROVEEDOR,
                id,
                "Proveedor activado: " + entidad.getNombreRazonSocial()
        );
    }

    public Proveedor buscarEntidadActiva(Long id) {
        return proveedorRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Proveedor no encontrado o inactivo con id: " + id));
    }

    private void validarDocumento(ProveedorRequest request) {
        TipoDocumento tipo = request.getTipoDocumento();
        String numero = request.getNumeroDocumento().trim();
        if (tipo == TipoDocumento.DNI && numero.length() != 8) {
            throw new ReglaNegocioException("El DNI debe tener exactamente 8 dígitos");
        }
        if (tipo == TipoDocumento.RUC && numero.length() != 11) {
            throw new ReglaNegocioException("El RUC debe tener exactamente 11 dígitos");
        }
    }

    private void validarNumeroDocumentoUnico(String numeroDocumento, Long idExcluido) {
        boolean existe = idExcluido == null
                ? proveedorRepository.existsByNumeroDocumento(numeroDocumento)
                : proveedorRepository.existsByNumeroDocumentoAndIdNot(numeroDocumento, idExcluido);
        if (existe) {
            throw new ConflictoException("Ya existe un proveedor con el número de documento: " + numeroDocumento);
        }
    }
}