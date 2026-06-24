package com.jmcavel.sigcav.util;

import com.jmcavel.sigcav.dto.request.ReporteFiltroRequest;
import com.jmcavel.sigcav.entity.Compra;
import com.jmcavel.sigcav.entity.Comprobante;
import com.jmcavel.sigcav.entity.Pedido;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExcelExportUtil {

    public byte[] generarReporteVentas(List<Comprobante> datos, ReporteFiltroRequest filtro) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Ventas");
            CellStyle headerStyle = crearEstiloEncabezado(workbook);

            String[] columnas = {"N° Comprobante", "Tipo", "Fecha Emisión", "Cliente",
                    "RUC/DNI", "Subtotal", "IGV", "Total", "Forma Pago", "Anulado"};
            crearFila(sheet, 0, columnas, headerStyle);

            int fila = 1;
            for (Comprobante c : datos) {
                Row row = sheet.createRow(fila++);
                row.createCell(0).setCellValue(c.getNumeroCompleto());
                row.createCell(1).setCellValue(c.getTipoComprobante().name());
                row.createCell(2).setCellValue(c.getFechaEmision().toString());
                row.createCell(3).setCellValue(c.getCliente().getNombreRazonSocial());
                row.createCell(4).setCellValue(c.getCliente().getNumeroDocumento());
                row.createCell(5).setCellValue(c.getSubtotal().doubleValue());
                row.createCell(6).setCellValue(c.getIgvMonto().doubleValue());
                row.createCell(7).setCellValue(c.getTotal().doubleValue());
                row.createCell(8).setCellValue(c.getFormaPago().name());
                row.createCell(9).setCellValue(c.isAnulado() ? "Sí" : "No");
            }

            autoAjustarColumnas(sheet, columnas.length);
            return toBytes(workbook);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel de ventas", e);
        }
    }

    public byte[] generarReporteRentabilidad(List<Pedido> datos, ReporteFiltroRequest filtro) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Rentabilidad");
            CellStyle headerStyle = crearEstiloEncabezado(workbook);

            String[] columnas = {"N° Pedido", "Cliente", "Fecha Ingreso", "Fecha Entrega",
                    "Precio Venta", "Costo Total", "Ganancia Bruta", "Margen %", "Estado"};
            crearFila(sheet, 0, columnas, headerStyle);

            int fila = 1;
            for (Pedido p : datos) {
                Row row = sheet.createRow(fila++);
                row.createCell(0).setCellValue(p.getNumeroPedido());
                row.createCell(1).setCellValue(p.getCliente().getNombreRazonSocial());
                row.createCell(2).setCellValue(p.getFechaIngreso().toString());
                row.createCell(3).setCellValue(p.getFechaEntregaComprometida().toString());
                row.createCell(4).setCellValue(p.getPrecioVenta().doubleValue());
                row.createCell(5).setCellValue(p.getCostoTotal().doubleValue());
                BigDecimal gananciaBruta = p.getPrecioVenta().subtract(p.getCostoTotal());
                BigDecimal margen = p.getPrecioVenta().compareTo(BigDecimal.ZERO) != 0
                        ? gananciaBruta.multiply(new BigDecimal("100")).divide(p.getPrecioVenta(), 2, java.math.RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;
                row.createCell(6).setCellValue(gananciaBruta.doubleValue());
                row.createCell(7).setCellValue(margen.doubleValue());
                row.createCell(8).setCellValue(p.getEstado().name());
            }

            autoAjustarColumnas(sheet, columnas.length);
            return toBytes(workbook);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel de rentabilidad", e);
        }
    }

    public byte[] generarReporteCompras(List<Compra> datos, ReporteFiltroRequest filtro) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Compras por Proveedor");
            CellStyle headerStyle = crearEstiloEncabezado(workbook);

            String[] columnas = {"Proveedor", "RUC/DNI", "Fecha Compra",
                    "N° Comprobante Proveedor", "Tipo Comprobante", "Total"};
            crearFila(sheet, 0, columnas, headerStyle);

            int fila = 1;
            for (Compra c : datos) {
                Row row = sheet.createRow(fila++);
                row.createCell(0).setCellValue(c.getProveedor().getNombreRazonSocial());
                row.createCell(1).setCellValue(c.getProveedor().getNumeroDocumento());
                row.createCell(2).setCellValue(c.getFechaCompra().toString());
                row.createCell(3).setCellValue(c.getNumeroComprobanteProveedor() != null
                        ? c.getNumeroComprobanteProveedor() : "");
                row.createCell(4).setCellValue(c.getTipoComprobanteProveedor() != null
                        ? c.getTipoComprobanteProveedor().name() : "");
                row.createCell(5).setCellValue(c.getTotal().doubleValue());
            }

            autoAjustarColumnas(sheet, columnas.length);
            return toBytes(workbook);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel de compras", e);
        }
    }

    public byte[] generarReportePedidosPorEstado(List<Object[]> datos, ReporteFiltroRequest filtro) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Pedidos por Estado");
            CellStyle headerStyle = crearEstiloEncabezado(workbook);

            String[] columnas = {"Estado", "Cantidad"};
            crearFila(sheet, 0, columnas, headerStyle);

            int fila = 1;
            for (Object[] row : datos) {
                Row excelRow = sheet.createRow(fila++);
                excelRow.createCell(0).setCellValue(row[0].toString());
                excelRow.createCell(1).setCellValue(((Number) row[1]).longValue());
            }

            autoAjustarColumnas(sheet, columnas.length);
            return toBytes(workbook);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel de pedidos por estado", e);
        }
    }

    public byte[] generarResumenFinanciero(List<Object[]> datos, ReporteFiltroRequest filtro) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Resumen Financiero");
            CellStyle headerStyle = crearEstiloEncabezado(workbook);

            String[] columnas = {"Mes", "Subtotal", "IGV Total", "Total Facturado", "N° Comprobantes"};
            crearFila(sheet, 0, columnas, headerStyle);

            int fila = 1;
            for (Object[] row : datos) {
                Row excelRow = sheet.createRow(fila++);
                excelRow.createCell(0).setCellValue(row[0].toString());
                excelRow.createCell(1).setCellValue(((BigDecimal) row[1]).doubleValue());
                excelRow.createCell(2).setCellValue(((BigDecimal) row[2]).doubleValue());
                excelRow.createCell(3).setCellValue(((BigDecimal) row[3]).doubleValue());
                excelRow.createCell(4).setCellValue(((Number) row[4]).longValue());
            }

            autoAjustarColumnas(sheet, columnas.length);
            return toBytes(workbook);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel de resumen financiero", e);
        }
    }

    private void crearFila(Sheet sheet, int numFila, String[] valores, CellStyle style) {
        Row row = sheet.createRow(numFila);
        for (int i = 0; i < valores.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(valores[i]);
            cell.setCellStyle(style);
        }
    }

    private CellStyle crearEstiloEncabezado(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private void autoAjustarColumnas(Sheet sheet, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private byte[] toBytes(XSSFWorkbook workbook) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return out.toByteArray();
    }
}