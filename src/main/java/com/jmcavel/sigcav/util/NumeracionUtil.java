package com.jmcavel.sigcav.util;

import com.jmcavel.sigcav.repository.CotizacionRepository;
import com.jmcavel.sigcav.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class NumeracionUtil {

    private final CotizacionRepository cotizacionRepository;
    private final PedidoRepository pedidoRepository;

    private static final DateTimeFormatter FORMATO_PERIODO = DateTimeFormatter.ofPattern("yyyyMM");

    public synchronized String generarNumeroCotizacion() {
        String periodo = LocalDate.now().format(FORMATO_PERIODO);
        String prefijo = "COT-" + periodo + "-";
        int ultimoCorrelativo = cotizacionRepository.obtenerUltimoCorrelativo(prefijo);
        return prefijo + String.format("%03d", ultimoCorrelativo + 1);
    }

    public synchronized String generarNumeroPedido() {
        String periodo = LocalDate.now().format(FORMATO_PERIODO);
        String prefijo = "PED-" + periodo + "-";
        int ultimoCorrelativo = pedidoRepository.obtenerUltimoCorrelativo(prefijo);
        return prefijo + String.format("%03d", ultimoCorrelativo + 1);
    }
}