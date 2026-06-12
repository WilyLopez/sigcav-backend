package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.PlantillaCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlantillaCotizacionRepository extends JpaRepository<PlantillaCotizacion, Long> {

    List<PlantillaCotizacion> findByActivoTrueOrderByNombrePlantillaAsc();

    boolean existsByNombrePlantillaAndActivoTrue(String nombrePlantilla);
}