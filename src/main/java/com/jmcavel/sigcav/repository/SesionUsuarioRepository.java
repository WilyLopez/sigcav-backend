package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.SesionUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SesionUsuarioRepository extends JpaRepository<SesionUsuario, Long> {

    Optional<SesionUsuario> findByTokenSesionAndActivaTrue(String tokenSesion);

    List<SesionUsuario> findByUsuarioIdAndActivaTrue(Long usuarioId);

    @Modifying
    @Query("UPDATE SesionUsuario s SET s.activa = false WHERE s.usuario.id = :usuarioId")
    void desactivarSesionesPorUsuario(@Param("usuarioId") Long usuarioId);

    @Modifying
    @Query("UPDATE SesionUsuario s SET s.activa = false WHERE s.activa = true AND s.ultimoAcceso < :limite")
    void desactivarSesionesInactivas(@Param("limite") LocalDateTime limite);

    @Modifying
    @Query("UPDATE SesionUsuario s SET s.ultimoAcceso = :ahora WHERE s.tokenSesion = :tokenSesion")
    void actualizarUltimoAcceso(@Param("tokenSesion") String tokenSesion, @Param("ahora") LocalDateTime ahora);
}