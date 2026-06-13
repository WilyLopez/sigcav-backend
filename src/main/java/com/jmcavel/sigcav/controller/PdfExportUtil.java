package com.jmcavel.sigcav.util;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.jmcavel.sigcav.dto.request.ReporteFiltroRequest;
import com.jmcavel.sigcav.entity.Compra;
import com.jmcavel.sigcav.entity.Comprobante;
import com.jmcavel.sigcav.entity.Pedido;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

@Component
public class PdfExportUtil {

    public byte[] generarReporteVentas(List<Comprobante> datos, ReporteFiltroRequest filtro) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PdfDocument pdf = new PdfDocument(new PdfWriter(out));
             Document doc = new Document(pdf)) {

            agregarTitulo(doc, "Reporte de Ventas", filtro);

            Table tabla = new Table(UnitValue.createPercentArray(new float[]{2, 1.5f, 1.5f, 3, 2, 1.5f, 1.5f, 1.5f}))
                    .useAllAvailableWidth();

            agregarEncabezados(tabla, "N° Comprobante", "Tipo", "Fecha", "Cliente",
                    "Doc. Cliente", "Subtotal", "IGV", "Total");

            for (Comprobante c : datos) {
                tabla.addCell(c.getNumeroCompleto());
                tabla.addCell(c.getTipoComprobante().name());
                tabla.addCell(c.getFechaEmision().toString());
                tabla.addCell(c.getCliente().getNombreRazonSocial());
                tabla.addCell(c.getCliente().getNumeroDocumento());
                tabla.addCell(c.getSubtotal().toPlainString());
                tabla.addCell(c.getIgvMonto().toPlainString());
                tabla.addCell(c.getTotal().toPlainString());
            }

            doc.add(tabla);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de ventas", e);
        }
        return out.toByteArray();
    }

    public byte[] generarReporteRentabilidad(List<Pedido> datos, ReporteFiltroRequest filtro) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PdfDocument pdf = new PdfDocument(new PdfWriter(out));
             Document doc = new Document(pdf)) {

            agregarTitulo(doc, "Reporte de Rentabilidad", filtro);

            Table tabla = new Table(UnitValue.createPercentArray(new float[]{2, 3, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f}))
                    .useAllAvailableWidth();

            agregarEncabezados(tabla, "N° Pedido", "Cliente", "Precio Venta",
                    "Costo Total", "Ganancia", "Margen %", "Estado");

            for (Pedido p : datos) {
                tabla.addCell(p.getNumeroPedido());
                tabla.addCell(p.getCliente().getNombreRazonSocial());
                tabla.addCell(p.getPrecioVenta().toPlainString());
                tabla.addCell(p.getCostoTotal().toPlainString());
                tabla.addCell(p.getGananciaBruta() != null
                        ? p.getGananciaBruta().toPlainString() : "0.00");
                tabla.addCell(p.getMargenGananciaPorcentaje() != null
                        ? p.getMargenGananciaPorcentaje().toPlainString() : "0.00");
                tabla.addCell(p.getEstado().name());
            }

            doc.add(tabla);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de rentabilidad", e);
        }
        return out.toByteArray();
    }

    public byte[] generarReporteCompras(List<Compra> datos, ReporteFiltroRequest filtro) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PdfDocument pdf = new PdfDocument(new PdfWriter(out));
             Document doc = new Document(pdf)) {

            agregarTitulo(doc, "Reporte de Compras por Proveedor", filtro);

            Table tabla = new Table(UnitValue.createPercentArray(new float[]{3, 2, 1.5f, 2, 2, 1.5f}))
                    .useAllAvailableWidth();

            agregarEncabezados(tabla, "Proveedor", "Doc. Proveedor", "Fecha",
                    "N° Comprobante", "Tipo", "Total");

            for (Compra c : datos) {
                tabla.addCell(c.getProveedor().getNombreRazonSocial());
                tabla.addCell(c.getProveedor().getNumeroDocumento());
                tabla.addCell(c.getFechaCompra().toString());
                tabla.addCell(c.getNumeroComprobanteProveedor() != null
                        ? c.getNumeroComprobanteProveedor() : "-");
                tabla.addCell(c.getTipoComprobanteProveedor() != null
                        ? c.getTipoComprobanteProveedor() : "-");
                tabla.addCell(c.getTotal().toPlainString());
            }

            doc.add(tabla);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de compras", e);
        }
        return out.toByteArray();
    }

    public byte[] generarReportePedidosPorEstado(List<Object[]> datos, ReporteFiltroRequest filtro) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PdfDocument pdf = new PdfDocument(new PdfWriter(out));
             Document doc = new Document(pdf)) {

            agregarTitulo(doc, "Pedidos por Estado", filtro);

            Table tabla = new Table(UnitValue.createPercentArray(new float[]{3, 2}))
                    .useAllAvailableWidth();

            agregarEncabezados(tabla, "Estado", "Cantidad");

            for (Object[] row : datos) {
                tabla.addCell(row[0].toString());
                tabla.addCell(row[1].toString());
            }

            doc.add(tabla);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de pedidos por estado", e);
        }
        return out.toByteArray();
    }

    public byte[] generarResumenFinanciero(List<Object[]> datos, ReporteFiltroRequest filtro) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PdfDocument pdf = new PdfDocument(new PdfWriter(out));
             Document doc = new Document(pdf)) {

            agregarTitulo(doc, "Resumen Financiero Mensual", filtro);

            Table tabla = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 2, 1.5f}))
                    .useAllAvailableWidth();

            agregarEncabezados(tabla, "Mes", "Subtotal", "IGV Total", "Total Facturado", "Comprobantes");

            for (Object[] row : datos) {
                tabla.addCell(row[0].toString());
                tabla.addCell(((BigDecimal) row[1]).toPlainString());
                tabla.addCell(((BigDecimal) row[2]).toPlainString());
                tabla.addCell(((BigDecimal) row[3]).toPlainString());
                tabla.addCell(row[4].toString());
            }

            doc.add(tabla);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de resumen financiero", e);
        }
        return out.toByteArray();
    }

    private void agregarTitulo(Document doc, String titulo, ReporteFiltroRequest filtro) {
        doc.add(new Paragraph(titulo)
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("Período: " + filtro.getDesde() + " al " + filtro.getHasta())
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("\n"));
    }

    private void agregarEncabezados(Table tabla, String... encabezados) {
        for (String enc : encabezados) {
            tabla.addHeaderCell(new Cell().add(new Paragraph(enc).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        }
    }
}