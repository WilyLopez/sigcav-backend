package com.jmcavel.sigcav.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoFichaResponse {

    private PedidoResponse datosgenerales;
    private ResumenFinancieroResponse resumenFinanciero;
    private List<HistorialEstadoPedidoResponse> historialEstados;
    private List<NotaInternaPedidoResponse> notasInternas;
}