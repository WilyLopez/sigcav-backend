package com.jmcavel.sigcav.service;

import com.jmcavel.sigcav.repository.RefreshTokenRepository;
import com.jmcavel.sigcav.repository.SesionUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SesionLimpiezaService {

    private final SesionUsuarioRepository sesionUsuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${sigcav.sesion.inactividad-minutos:10}")
    private Integer inactividadMinutos;

    @Scheduled(fixedDelayString = "${sigcav.sesion.intervalo-limpieza-ms:60000}")
    @Transactional
    public void desactivarSesionesInactivas() {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(inactividadMinutos);
        sesionUsuarioRepository.desactivarSesionesInactivas(limite);
    }

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void limpiarTokensExpirados() {
        refreshTokenRepository.eliminarExpiradosYRevocados();
    }
}