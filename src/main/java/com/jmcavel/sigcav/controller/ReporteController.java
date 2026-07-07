package com.jmcavel.sigcav.controller;

import com.jmcavel.sigcav.dto.request.ReporteFiltroRequest;
import com.jmcavel.sigcav.service.ReporteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @PostMapping("/ventas/pdf")
    public ResponseEntity<byte[]> ventasPdf(@Valid @RequestBody ReporteFiltroRequest filtro) {
        return buildPdfResponse(reporteService.exportarReporteVentasPdf(filtro), "reporte_ventas.pdf");
    }

    @PostMapping("/ventas/excel")
    public ResponseEntity<byte[]> ventasExcel(@Valid @RequestBody ReporteFiltroRequest filtro) {
        return buildExcelResponse(reporteService.exportarReporteVentasExcel(filtro), "reporte_ventas.xlsx");
    }

    @PostMapping("/rentabilidad/pdf")
    public ResponseEntity<byte[]> rentabilidadPdf(@Valid @RequestBody ReporteFiltroRequest filtro) {
        return buildPdfResponse(reporteService.exportarReporteRentabilidadPdf(filtro), "reporte_rentabilidad.pdf");
    }

    @PostMapping("/rentabilidad/excel")
    public ResponseEntity<byte[]> rentabilidadExcel(@Valid @RequestBody ReporteFiltroRequest filtro) {
        return buildExcelResponse(reporteService.exportarReporteRentabilidadExcel(filtro), "reporte_rentabilidad.xlsx");
    }

    @PostMapping("/compras/pdf")
    public ResponseEntity<byte[]> comprasPdf(@Valid @RequestBody ReporteFiltroRequest filtro) {
        return buildPdfResponse(reporteService.exportarReporteComprasPdf(filtro), "reporte_compras.pdf");
    }

    @PostMapping("/compras/excel")
    public ResponseEntity<byte[]> comprasExcel(@Valid @RequestBody ReporteFiltroRequest filtro) {
        return buildExcelResponse(reporteService.exportarReporteComprasExcel(filtro), "reporte_compras.xlsx");
    }

    @PostMapping("/pedidos-estado/pdf")
    public ResponseEntity<byte[]> pedidosEstadoPdf(@Valid @RequestBody ReporteFiltroRequest filtro) {
        return buildPdfResponse(reporteService.exportarReportePedidosEstadoPdf(filtro), "reporte_pedidos_estado.pdf");
    }

    @PostMapping("/pedidos-estado/excel")
    public ResponseEntity<byte[]> pedidosEstadoExcel(@Valid @RequestBody ReporteFiltroRequest filtro) {
        return buildExcelResponse(reporteService.exportarReportePedidosEstadoExcel(filtro), "reporte_pedidos_estado.xlsx");
    }

    @PostMapping("/resumen-financiero/pdf")
    public ResponseEntity<byte[]> resumenFinancieroPdf(@Valid @RequestBody ReporteFiltroRequest filtro) {
        return buildPdfResponse(reporteService.exportarResumenFinancieroPdf(filtro), "resumen_financiero.pdf");
    }

    @PostMapping("/resumen-financiero/excel")
    public ResponseEntity<byte[]> resumenFinancieroExcel(@Valid @RequestBody ReporteFiltroRequest filtro) {
        return buildExcelResponse(reporteService.exportarResumenFinancieroExcel(filtro), "resumen_financiero.xlsx");
    }

    private ResponseEntity<byte[]> buildPdfResponse(byte[] contenido, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(contenido);
    }

    private ResponseEntity<byte[]> buildExcelResponse(byte[] contenido, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(contenido);
    }
}