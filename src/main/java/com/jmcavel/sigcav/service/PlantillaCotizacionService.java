package com.jmcavel.sigcav.service;

import com.jmcavel.sigcav.dto.request.CrearPlantillaCotizacionRequest;
import com.jmcavel.sigcav.dto.response.PlantillaCotizacionResponse;
import com.jmcavel.sigcav.entity.PlantillaCotizacion;
import com.jmcavel.sigcav.entity.Usuario;
import com.jmcavel.sigcav.exception.RecursoNoEncontradoException;
import com.jmcavel.sigcav.exception.ReglaDeNegocioException;
import com.jmcavel.sigcav.mapper.PlantillaCotizacionMapper;
import com.jmcavel.sigcav.repository.PlantillaCotizacionRepository;
import com.jmcavel.sigcav.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlantillaCotizacionService {

    private final PlantillaCotizacionRepository plantillaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PlantillaCotizacionMapper plantillaMapper;

    @Transactional
    public PlantillaCotizacionResponse crear(CrearPlantillaCotizacionRequest request, Long usuarioId) {
        if (plantillaRepository.existsByNombrePlantillaAndActivoTrue(request.getNombrePlantilla())) {
            throw new ReglaDeNegocioException(
                    "Ya existe una plantilla activa con el nombre: " + request.getNombrePlantilla()
            );
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        PlantillaCotizacion plantilla = PlantillaCotizacion.builder()
                .nombrePlantilla(request.getNombrePlantilla())
                .descripcionProducto(request.getDescripcionProducto())
                .tipoImpresion(request.getTipoImpresion())
                .material(request.getMaterial())
                .dimensiones(request.getDimensiones())
                .acabados(request.getAcabados())
                .condicionesPago(request.getCondicionesPago())
                .observaciones(request.getObservaciones())
                .activo(true)
                .creadoPor(usuario)
                .build();

        return plantillaMapper.toResponse(plantillaRepository.save(plantilla));
    }

    @Transactional(readOnly = true)
    public List<PlantillaCotizacionResponse> listarActivas() {
        return plantillaRepository.findByActivoTrueOrderByNombrePlantillaAsc()
                .stream()
                .map(plantillaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PlantillaCotizacionResponse obtenerPorId(Long id) {
        return plantillaMapper.toResponse(buscarPorId(id));
    }

    @Transactional
    public void desactivar(Long id) {
        PlantillaCotizacion plantilla = buscarPorId(id);
        plantilla.setActivo(false);
        plantillaRepository.save(plantilla);
    }

    private PlantillaCotizacion buscarPorId(Long id) {
        return plantillaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Plantilla no encontrada con id: " + id
                ));
    }
}