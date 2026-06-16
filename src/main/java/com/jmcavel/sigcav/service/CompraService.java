package com.jmcavel.sigcav.service;

import com.jmcavel.sigcav.dto.request.CompraPedidoRequest;
import com.jmcavel.sigcav.dto.request.CompraRequest;
import com.jmcavel.sigcav.dto.request.GastoPedidoRequest;
import com.jmcavel.sigcav.dto.response.CompraPedidoResponse;
import com.jmcavel.sigcav.dto.response.CompraResponse;
import com.jmcavel.sigcav.dto.response.GastoPedidoResponse;
import com.jmcavel.sigcav.entity.*;
import com.jmcavel.sigcav.exception.RecursoNoEncontradoException;
import com.jmcavel.sigcav.exception.ReglaDeNegocioException;
import com.jmcavel.sigcav.mapper.CompraPedidoMapper;
import com.jmcavel.sigcav.mapper.CompraMapper;
import com.jmcavel.sigcav.mapper.GastoPedidoMapper;
import com.jmcavel.sigcav.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompraService {

    private static final List<String> ESTADOS_EDITABLES = List.of("PENDIENTE", "EN_PRODUCCION");

    private final CompraRepository compraRepository;
    private final ItemCompraRepository itemCompraRepository;
    private final CompraPedidoRepository compraPedidoRepository;
    private final GastoPedidoRepository gastoPedidoRepository;
    private final PedidoRepository pedidoRepository;
    private final ProveedorRepository proveedorRepository;
    private final CompraMapper compraMapper;
    private final CompraPedidoMapper compraPedidoMapper;
    private final GastoPedidoMapper gastoPedidoMapper;
    private final AuditoriaService auditoriaService;

    @Transactional
    public CompraResponse registrarCompra(CompraRequest solicitud, Usuario usuarioActual) {
        Proveedor proveedor = obtenerProveedorOFallar(solicitud.getProveedorId());

        BigDecimal total = calcularTotalItems(solicitud);

        Compra compra = Compra.builder()
                .proveedor(proveedor)
                .fechaCompra(solicitud.getFechaCompra())
                .numeroComprobanteProveedor(solicitud.getNumeroComprobanteProveedor())
                .tipoComprobanteProveedor(solicitud.getTipoComprobanteProveedor())
                .total(total)
                .observaciones(solicitud.getObservaciones())
                .registradoPor(usuarioActual)
                .build();

        compraRepository.save(compra);

        List<ItemCompra> items = solicitud.getItems().stream()
                .map(itemReq -> {
                    BigDecimal subtotal = itemReq.getPrecioUnitario()
                            .multiply(itemReq.getCantidad())
                            .setScale(2, RoundingMode.HALF_UP);
                    return ItemCompra.builder()
                            .compra(compra)
                            .nombreMaterial(itemReq.getNombreMaterial())
                            .unidadMedida(itemReq.getUnidadMedida())
                            .cantidad(itemReq.getCantidad())
                            .precioUnitario(itemReq.getPrecioUnitario())
                            .subtotal(subtotal)
                            .build();
                })
                .toList();

        itemCompraRepository.saveAll(items);

        auditoriaService.registrar(usuarioActual, "CREAR", "compra", compra.getId(),
                "Compra registrada por S/ " + total);

        return compraMapper.toResponse(compra, items);
    }

    @Transactional(readOnly = true)
    public CompraResponse obtenerCompra(Long compraId) {
        Compra compra = obtenerCompraOFallar(compraId);
        List<ItemCompra> items = itemCompraRepository.findByCompraId(compraId);
        return compraMapper.toResponse(compra, items);
    }

    @Transactional(readOnly = true)
    public List<CompraResponse> listarCompras(Long proveedorId, LocalDate desde, LocalDate hasta) {
        return compraRepository.buscarConFiltros(proveedorId, desde, hasta).stream()
                .map(compra -> compraMapper.toResponse(compra,
                        itemCompraRepository.findByCompraId(compra.getId())))
                .toList();
    }

    @Transactional
    public CompraPedidoResponse asignarCompraAPedido(Long compraId, CompraPedidoRequest solicitud,
                                                      Usuario usuarioActual) {
        Compra compra = obtenerCompraOFallar(compraId);
        Pedido pedido = obtenerPedidoOFallar(solicitud.getPedidoId());

        if (compraPedidoRepository.existsByCompraIdAndPedidoId(compraId, pedido.getId())) {
            throw new ReglaDeNegocioException(
                    "Esta compra ya está asignada al pedido " + pedido.getNumeroPedido());
        }

        BigDecimal yaAsignado = compraPedidoRepository.sumarMontoAsignadoPorCompra(compraId);
        BigDecimal disponible = compra.getTotal().subtract(yaAsignado);

        if (solicitud.getMontoAsignado().compareTo(disponible) > 0) {
            throw new ReglaDeNegocioException(
                    "El monto asignado supera el disponible de la compra. Disponible: S/ " + disponible);
        }

        CompraPedido compraPedido = CompraPedido.builder()
                .compra(compra)
                .pedido(pedido)
                .montoAsignado(solicitud.getMontoAsignado())
                .build();

        compraPedidoRepository.save(compraPedido);
        recalcularCostoPedido(pedido);

        auditoriaService.registrar(usuarioActual, "CREAR", "compra_pedido", compraPedido.getId(),
                "Asignación de compra " + compraId + " a pedido " + pedido.getNumeroPedido()
                        + " por S/ " + solicitud.getMontoAsignado());

        return compraPedidoMapper.toResponse(compraPedido);
    }

    @Transactional
    public CompraPedidoResponse editarAsignacionCompra(Long compraPedidoId, CompraPedidoRequest solicitud,
                                                        Usuario usuarioActual) {
        CompraPedido compraPedido = compraPedidoRepository.findById(compraPedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Asignación no encontrada con id: " + compraPedidoId));

        Pedido pedido = compraPedido.getPedido();
        validarEstadoEditable(pedido);

        BigDecimal yaAsignado = compraPedidoRepository.sumarMontoAsignadoPorCompraExcluyendo(
                compraPedido.getCompra().getId(), compraPedidoId);
        BigDecimal disponible = compraPedido.getCompra().getTotal().subtract(yaAsignado);

        if (solicitud.getMontoAsignado().compareTo(disponible) > 0) {
            throw new ReglaDeNegocioException(
                    "El monto asignado supera el disponible de la compra. Disponible: S/ " + disponible);
        }

        compraPedido.setMontoAsignado(solicitud.getMontoAsignado());
        compraPedidoRepository.save(compraPedido);
        recalcularCostoPedido(pedido);

        auditoriaService.registrar(usuarioActual, "EDITAR", "compra_pedido", compraPedidoId,
                "Monto actualizado a S/ " + solicitud.getMontoAsignado());

        return compraPedidoMapper.toResponse(compraPedido);
    }

    @Transactional
    public void eliminarAsignacionCompra(Long compraPedidoId, Usuario usuarioActual) {
        CompraPedido compraPedido = compraPedidoRepository.findById(compraPedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Asignación no encontrada con id: " + compraPedidoId));

        Pedido pedido = compraPedido.getPedido();
        validarEstadoEditable(pedido);

        compraPedidoRepository.delete(compraPedido);
        recalcularCostoPedido(pedido);

        auditoriaService.registrar(usuarioActual, "ELIMINAR", "compra_pedido", compraPedidoId,
                "Asignación eliminada del pedido " + pedido.getNumeroPedido());
    }

    @Transactional(readOnly = true)
    public List<CompraPedidoResponse> listarAsignacionesPorCompra(Long compraId) {
        obtenerCompraOFallar(compraId);
        return compraPedidoRepository.findByCompraId(compraId).stream()
                .map(compraPedidoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CompraPedidoResponse> listarAsignacionesPorPedido(Long pedidoId) {
        obtenerPedidoOFallar(pedidoId);
        return compraPedidoRepository.findByPedidoId(pedidoId).stream()
                .map(compraPedidoMapper::toResponse)
                .toList();
    }

    @Transactional
    public GastoPedidoResponse registrarGasto(Long pedidoId, GastoPedidoRequest solicitud,
                                               Usuario usuarioActual) {
        Pedido pedido = obtenerPedidoOFallar(pedidoId);
        validarEstadoEditable(pedido);

        Proveedor proveedor = solicitud.getProveedorId() != null
                ? obtenerProveedorOFallar(solicitud.getProveedorId())
                : null;

        GastoPedido gasto = GastoPedido.builder()
                .pedido(pedido)
                .tipoGasto(solicitud.getTipoGasto())
                .descripcion(solicitud.getDescripcion())
                .proveedor(proveedor)
                .fechaGasto(solicitud.getFechaGasto())
                .monto(solicitud.getMonto())
                .numeroComprobanteProveedor(solicitud.getNumeroComprobanteProveedor())
                .observaciones(solicitud.getObservaciones())
                .registradoPor(usuarioActual)
                .build();

        gastoPedidoRepository.save(gasto);
        recalcularCostoPedido(pedido);

        auditoriaService.registrar(usuarioActual, "CREAR", "gasto_pedido", gasto.getId(),
                "Gasto " + gasto.getTipoGasto() + " de S/ " + gasto.getMonto()
                        + " en pedido " + pedido.getNumeroPedido());

        return gastoPedidoMapper.toResponse(gasto);
    }

    @Transactional
    public GastoPedidoResponse editarGasto(Long gastoId, GastoPedidoRequest solicitud,
                                            Usuario usuarioActual) {
        GastoPedido gasto = obtenerGastoOFallar(gastoId);
        Pedido pedido = gasto.getPedido();
        validarEstadoEditable(pedido);

        Proveedor proveedor = solicitud.getProveedorId() != null
                ? obtenerProveedorOFallar(solicitud.getProveedorId())
                : null;

        gasto.setTipoGasto(solicitud.getTipoGasto());
        gasto.setDescripcion(solicitud.getDescripcion());
        gasto.setProveedor(proveedor);
        gasto.setFechaGasto(solicitud.getFechaGasto());
        gasto.setMonto(solicitud.getMonto());
        gasto.setNumeroComprobanteProveedor(solicitud.getNumeroComprobanteProveedor());
        gasto.setObservaciones(solicitud.getObservaciones());

        gastoPedidoRepository.save(gasto);
        recalcularCostoPedido(pedido);

        auditoriaService.registrar(usuarioActual, "EDITAR", "gasto_pedido", gastoId,
                "Gasto actualizado a S/ " + solicitud.getMonto());

        return gastoPedidoMapper.toResponse(gasto);
    }

    @Transactional
    public void eliminarGasto(Long gastoId, Usuario usuarioActual) {
        GastoPedido gasto = obtenerGastoOFallar(gastoId);
        Pedido pedido = gasto.getPedido();
        validarEstadoEditable(pedido);

        gastoPedidoRepository.delete(gasto);
        recalcularCostoPedido(pedido);

        auditoriaService.registrar(usuarioActual, "ELIMINAR", "gasto_pedido", gastoId,
                "Gasto eliminado del pedido " + pedido.getNumeroPedido());
    }

    @Transactional(readOnly = true)
    public List<GastoPedidoResponse> listarGastosPorPedido(Long pedidoId) {
        obtenerPedidoOFallar(pedidoId);
        return gastoPedidoRepository.findByPedidoIdOrderByCreadoEnDesc(pedidoId).stream()
                .map(gastoPedidoMapper::toResponse)
                .toList();
    }

    private void recalcularCostoPedido(Pedido pedido) {
        BigDecimal totalGastos = gastoPedidoRepository.sumarGastosPorPedido(pedido.getId());
        BigDecimal totalCompras = compraPedidoRepository.sumarMontoAsignadoPorPedido(pedido.getId());
        pedido.setCostoTotal(totalGastos.add(totalCompras).setScale(2, RoundingMode.HALF_UP));
        pedidoRepository.save(pedido);
    }

    private void validarEstadoEditable(Pedido pedido) {
        if (!ESTADOS_EDITABLES.contains(pedido.getEstado())) {
            throw new ReglaDeNegocioException(
                    "No se pueden modificar costos de un pedido en estado: " + pedido.getEstado());
        }
    }

    private BigDecimal calcularTotalItems(CompraRequest solicitud) {
        return solicitud.getItems().stream()
                .map(item -> item.getPrecioUnitario()
                        .multiply(item.getCantidad())
                        .setScale(2, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Compra obtenerCompraOFallar(Long id) {
        return compraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Compra no encontrada con id: " + id));
    }

    private Pedido obtenerPedidoOFallar(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con id: " + id));
    }

    private Proveedor obtenerProveedorOFallar(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con id: " + id));
    }

    private GastoPedido obtenerGastoOFallar(Long id) {
        return gastoPedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Gasto no encontrado con id: " + id));
    }
}