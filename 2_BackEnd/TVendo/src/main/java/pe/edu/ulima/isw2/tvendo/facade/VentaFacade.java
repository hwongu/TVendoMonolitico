package pe.edu.ulima.isw2.tvendo.facade;

import pe.edu.ulima.isw2.tvendo.entity.DetalleVenta;
import pe.edu.ulima.isw2.tvendo.entity.Pago;
import pe.edu.ulima.isw2.tvendo.entity.Producto;
import pe.edu.ulima.isw2.tvendo.entity.Venta;
import pe.edu.ulima.isw2.tvendo.pattern.strategy.EstrategiaEnvio;
import pe.edu.ulima.isw2.tvendo.adapter.payment.ResultadoPago;
import pe.edu.ulima.isw2.tvendo.service.PagoService;
import pe.edu.ulima.isw2.tvendo.service.ProductoService;
import pe.edu.ulima.isw2.tvendo.service.VentaService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Facade para la gestión de ventas, incluyendo registro, anulación e impresión de comprobantes.
 * Proporciona una interfaz simplificada para interactuar con los servicios subyacentes de productos, ventas y pagos.
 * Implementa el patrón Facade para ocultar la complejidad de las operaciones internas y ofrecer métodos de alto nivel.
 * @author Henry Wong
 */
public class VentaFacade {

    private final ProductoService productoService;
    private final VentaService ventaService;
    private final PagoService pagoService;

    public VentaFacade(ProductoService productoService, VentaService ventaService, PagoService pagoService) {
        this.productoService = productoService;
        this.ventaService = ventaService;
        this.pagoService = pagoService;
    }

    public Venta registrarVenta(String codigoVenta, Map<String, Integer> productos, EstrategiaEnvio estrategiaEnvio, String tipoPago, String numeroDestino, String titular, String descripcionPago) {
        if (productos == null || productos.isEmpty()) {
            throw new IllegalArgumentException("Debe enviar al menos un producto");
        }
        List<Map.Entry<Producto, Integer>> items = productos.entrySet().stream().map(entry -> {
            Producto producto = productoService.validarExistencia(entry.getKey());
            Integer cantidad = entry.getValue();
            productoService.validarStock(producto, cantidad);
            return (Map.Entry<Producto, Integer>) new AbstractMap.SimpleEntry<>(producto, cantidad);
        }).toList();
        BigDecimal subtotalVenta = items.stream().map(entry -> entry.getKey().getPrecio().multiply(BigDecimal.valueOf(entry.getValue()))).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        BigDecimal costoEnvio = estrategiaEnvio.calcularCosto(subtotalVenta).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalVenta = subtotalVenta.add(costoEnvio).setScale(2, RoundingMode.HALF_UP);
        ResultadoPago resultadoPago = pagoService.procesarPagoExterno(tipoPago, numeroDestino, titular, totalVenta, descripcionPago);
        if (!"APROBADO".equalsIgnoreCase(resultadoPago.estado())) {
            throw new IllegalStateException("El pago no fue aprobado. Estado recibido: " + resultadoPago.estado());
        }
        Venta venta = new Venta(null, codigoVenta, LocalDateTime.now(), subtotalVenta, costoEnvio, totalVenta, "REGISTRADA", "Registro de venta");
        List<DetalleVenta> detallesVenta = items.stream().map(item -> {
            Producto producto = item.getKey();
            Integer cantidad = item.getValue();
            BigDecimal subtotalDetalle = producto.getPrecio().multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);
            return new DetalleVenta(null, null, producto.getId(), cantidad, producto.getPrecio().setScale(2, RoundingMode.HALF_UP), subtotalDetalle);
        }).toList();
        Map<Long, Integer> nuevoStockPorProducto = new HashMap<>();
        for (Map.Entry<Producto, Integer> item : items) {
            Producto producto = item.getKey();
            Integer cantidad = item.getValue();
            nuevoStockPorProducto.put(producto.getId(), producto.getStock() - cantidad);
        }
        Pago pago = pagoService.construirPago(null, tipoPago.toUpperCase(Locale.ROOT), totalVenta, descripcionPago, resultadoPago);
        return ventaService.registrarVentaCompletaTransaccional(venta, detallesVenta, nuevoStockPorProducto, pago);
    }

    public void anularVenta(String codigoVenta) {
        Venta venta = ventaService.buscarPorCodigo(codigoVenta).orElseThrow(() -> new IllegalArgumentException("No existe venta con código: " + codigoVenta));
        ventaService.anularVenta(venta.getId());
    }

    public void imprimirComprobante(String codigoVenta) {
        System.out.print(obtenerComprobante(codigoVenta));
    }

    public String obtenerComprobante(String codigoVenta) {
        validarCodigoVenta(codigoVenta);

        Venta venta = ventaService.buscarPorCodigo(codigoVenta).orElseThrow(() -> new IllegalArgumentException("No existe venta con código: " + codigoVenta));
        List<DetalleVenta> detalles = ventaService.listarDetallesPorVentaId(venta.getId());
        List<Pago> pagos = pagoService.listarPorVentaId(venta.getId());
        Map<Long, String> nombresProductoPorId = construirIndiceProductos();

        StringBuilder comprobante = new StringBuilder();
        comprobante.append("================================\n");
        comprobante.append("TVENDO\n");
        comprobante.append("Venta: ").append(venta.getCodigo()).append('\n');
        comprobante.append("Fecha: ").append(venta.getFecha()).append('\n');
        comprobante.append("Subtotal: S/ ").append(venta.getSubtotal()).append('\n');
        comprobante.append("Envío: S/ ").append(venta.getCostoEnvio()).append('\n');
        comprobante.append("Total: S/ ").append(venta.getTotal()).append('\n');
        comprobante.append("Estado: ").append(venta.getEstado()).append('\n');
        comprobante.append('\n');
        comprobante.append("DETALLE\n");

        for (DetalleVenta detalle : detalles) {
            String nombreProducto = nombresProductoPorId.getOrDefault(detalle.getProductoId(), "ID " + detalle.getProductoId());
            comprobante.append("Producto: ").append(nombreProducto).append('\n');
            comprobante.append("Cantidad: ").append(detalle.getCantidad()).append('\n');
            comprobante.append("Subtotal: S/ ").append(detalle.getSubtotal()).append('\n');
        }

        comprobante.append('\n');
        comprobante.append("PAGO\n");
        if (pagos.isEmpty()) {
            comprobante.append("Sin pagos registrados\n");
        } else {
            Pago ultimoPago = pagos.getLast();
            comprobante.append("Tipo: ").append(ultimoPago.getTipo()).append('\n');
            comprobante.append("Código externo: ").append(ultimoPago.getCodigoExterno()).append('\n');
            comprobante.append("Estado: ").append(ultimoPago.getEstado()).append('\n');
        }
        comprobante.append("================================\n");
        return comprobante.toString();
    }

    private Map<Long, String> construirIndiceProductos() {
        Map<Long, String> nombresPorId = new HashMap<>();
        for (Producto producto : productoService.listarProductos()) {
            nombresPorId.put(producto.getId(), producto.getNombre());
        }
        return nombresPorId;
    }

    private void validarCodigoVenta(String codigoVenta) {
        if (codigoVenta == null || codigoVenta.isBlank()) {
            throw new IllegalArgumentException("El código de venta es obligatorio");
        }
    }
}
