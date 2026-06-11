package com.jmcavel.sigcav.repository;

import com.jmcavel.sigcav.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByTokenAndRevocadoFalse(String token);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.revocado = true WHERE r.usuario.id = :usuarioId")
    void revocarTodosPorUsuario(@Param("usuarioId") Long usuarioId);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.revocado = true WHERE r.token = :token")
    void revocarPorToken(@Param("token") String token);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.revocado = true OR r.expiraEn < CURRENT_TIMESTAMP")
    void eliminarExpiradosYRevocados();
}