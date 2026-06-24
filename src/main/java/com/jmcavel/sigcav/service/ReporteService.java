package com.jmcavel.sigcav.service;

import com.jmcavel.sigcav.dto.request.ReporteFiltroRequest;
import com.jmcavel.sigcav.entity.Comprobante;
import com.jmcavel.sigcav.entity.Pedido;
import com.jmcavel.sigcav.repository.CompraReporteRepository;
import com.jmcavel.sigcav.repository.ReporteRepository;
import com.jmcavel.sigcav.util.ExcelExportUtil;
import com.jmcavel.sigcav.util.PdfExportUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final CompraReporteRepository compraReporteRepository;
    private final ExcelExportUtil excelExportUtil;
    private final PdfExportUtil pdfExportUtil;

    @Transactional(readOnly = true)
    public byte[] exportarReporteVentasPdf(ReporteFiltroRequest filtro) {
        List<Comprobante> datos = reporteRepository.reporteVentas(filtro.getDesde(), filtro.getHasta());
        return pdfExportUtil.generarReporteVentas(datos, filtro);
    }

    @Transactional(readOnly = true)
    public byte[] exportarReporteVentasExcel(ReporteFiltroRequest filtro) {
        List<Comprobante> datos = reporteRepository.reporteVentas(filtro.getDesde(), filtro.getHasta());
        return excelExportUtil.generarReporteVentas(datos, filtro);
    }

    @Transactional(readOnly = true)
    public byte[] exportarReporteRentabilidadPdf(ReporteFiltroRequest filtro) {
        List<Pedido> datos = reporteRepository.reporteRentabilidad(filtro.getDesde(), filtro.getHasta());
        return pdfExportUtil.generarReporteRentabilidad(datos, filtro);
    }

    @Transactional(readOnly = true)
    public byte[] exportarReporteRentabilidadExcel(ReporteFiltroRequest filtro) {
        List<Pedido> datos = reporteRepository.reporteRentabilidad(filtro.getDesde(), filtro.getHasta());
        return excelExportUtil.generarReporteRentabilidad(datos, filtro);
    }

    @Transactional(readOnly = true)
    public byte[] exportarReporteComprasPdf(ReporteFiltroRequest filtro) {
        var datos = compraReporteRepository.reporteComprasPorProveedor(
                filtro.getDesde(), filtro.getHasta(), filtro.getProveedorId());
        return pdfExportUtil.generarReporteCompras(datos, filtro);
    }

    @Transactional(readOnly = true)
    public byte[] exportarReporteComprasExcel(ReporteFiltroRequest filtro) {
        var datos = compraReporteRepository.reporteComprasPorProveedor(
                filtro.getDesde(), filtro.getHasta(), filtro.getProveedorId());
        return excelExportUtil.generarReporteCompras(datos, filtro);
    }

    @Transactional(readOnly = true)
    public byte[] exportarReportePedidosEstadoPdf(ReporteFiltroRequest filtro) {
        List<Object[]> datos = reporteRepository.reportePedidosPorEstado(filtro.getDesde(), filtro.getHasta());
        return pdfExportUtil.generarReportePedidosPorEstado(datos, filtro);
    }

    @Transactional(readOnly = true)
    public byte[] exportarReportePedidosEstadoExcel(ReporteFiltroRequest filtro) {
        List<Object[]> datos = reporteRepository.reportePedidosPorEstado(filtro.getDesde(), filtro.getHasta());
        return excelExportUtil.generarReportePedidosPorEstado(datos, filtro);
    }

    @Transactional(readOnly = true)
    public byte[] exportarResumenFinancieroPdf(ReporteFiltroRequest filtro) {
        List<Object[]> datos = reporteRepository.reporteResumenFinancieroMensual(
                filtro.getDesde(), filtro.getHasta());
        return pdfExportUtil.generarResumenFinanciero(datos, filtro);
    }

    @Transactional(readOnly = true)
    public byte[] exportarResumenFinancieroExcel(ReporteFiltroRequest filtro) {
        List<Object[]> datos = reporteRepository.reporteResumenFinancieroMensual(
                filtro.getDesde(), filtro.getHasta());
        return excelExportUtil.generarResumenFinanciero(datos, filtro);
    }
}