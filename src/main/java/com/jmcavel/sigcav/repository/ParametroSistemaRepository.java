package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.ParametroSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParametroSistemaRepository extends JpaRepository<ParametroSistema, Long> {

    Optional<ParametroSistema> findByClave(String clave);

    boolean existsByClave(String clave);

    List<ParametroSistema> findByTipoDato(String tipoDato);
}