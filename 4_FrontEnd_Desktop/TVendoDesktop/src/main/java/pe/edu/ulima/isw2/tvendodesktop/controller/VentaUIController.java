package pe.edu.ulima.isw2.tvendodesktop.controller;

import pe.edu.ulima.isw2.tvendo.entity.Producto;
import pe.edu.ulima.isw2.tvendo.facade.VentaFacade;
import pe.edu.ulima.isw2.tvendo.invoker.PanelCaja;
import pe.edu.ulima.isw2.tvendo.pattern.command.Command;
import pe.edu.ulima.isw2.tvendo.pattern.command.impl.AnularVentaCommand;
import pe.edu.ulima.isw2.tvendo.pattern.command.impl.RegistrarVentaCommand;
import pe.edu.ulima.isw2.tvendo.pattern.strategy.EstrategiaEnvio;
import pe.edu.ulima.isw2.tvendo.pattern.strategy.impl.EnvioEstandar;
import pe.edu.ulima.isw2.tvendo.pattern.strategy.impl.EnvioExpress;
import pe.edu.ulima.isw2.tvendo.pattern.strategy.impl.EnvioProgramado;
import pe.edu.ulima.isw2.tvendo.service.ProductoService;
import pe.edu.ulima.isw2.tvendo.service.VentaService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Controlador de la interfaz de usuario para la gestión de ventas.
 * Este controlador interactúa con los servicios de producto y venta, así como con el panel de caja,
 * para permitir la realización y anulación de ventas, así como la obtención de comprobantes.
 * Proporciona métodos para listar productos, registrar ventas, anular ventas y verificar la existencia de ventas.
 * @author Henry Wong
 */
public class VentaUIController {

    private final ProductoService productoService;
    private final VentaService ventaService;
    private final VentaFacade ventaFacade;
    private final PanelCaja panelCaja;

    public VentaUIController(
            ProductoService productoService,
            VentaService ventaService,
            VentaFacade ventaFacade,
            PanelCaja panelCaja
    ) {
        this.productoService = productoService;
        this.ventaService = ventaService;
        this.ventaFacade = ventaFacade;
        this.panelCaja = panelCaja;
    }

    public List<Producto> listarProductos() {
        return new ArrayList<>(productoService.listarProductos());
    }

    public Map<String, Producto> listarProductosPorCodigo() {
        return listarProductos().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        Producto::getCodigo,
                        producto -> producto,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
    }

    public void registrarVenta(
            String codigoVenta,
            Map<String, Integer> productos,
            String tipoEnvio,
            String tipoPago,
            String numeroDestino,
            String titular,
            String descripcionPago
    ) {
        validarCodigoVenta(codigoVenta);
        if (productos == null || productos.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un producto.");
        }
        EstrategiaEnvio estrategiaEnvio = resolverEstrategiaEnvio(tipoEnvio);
        String tipoPagoNormalizado = normalizarPago(tipoPago);
        Command command = new RegistrarVentaCommand(
                ventaFacade,
                codigoVenta.trim(),
                new LinkedHashMap<>(productos),
                estrategiaEnvio,
                tipoPagoNormalizado,
                numeroDestino == null ? "" : numeroDestino.trim(),
                titular == null ? "" : titular.trim(),
                descripcionPago == null ? "" : descripcionPago.trim()
        );
        ejecutarCommand(command);
    }

    public void anularVenta(String codigoVenta) {
        validarCodigoVenta(codigoVenta);
        Command command = new AnularVentaCommand(ventaFacade, codigoVenta.trim());
        ejecutarCommand(command);
    }

    public boolean existeVenta(String codigoVenta) {
        validarCodigoVenta(codigoVenta);
        return ventaService.buscarPorCodigo(codigoVenta.trim()).isPresent();
    }

    public String obtenerComprobante(String codigoVenta) {
        validarCodigoVenta(codigoVenta);
        return ventaFacade.obtenerComprobante(codigoVenta.trim());
    }

    private void ejecutarCommand(Command command) {
        panelCaja.setCommand(command);
        panelCaja.ejecutar();
    }

    private EstrategiaEnvio resolverEstrategiaEnvio(String tipoEnvio) {
        if (tipoEnvio == null || tipoEnvio.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de envío.");
        }

        return switch (tipoEnvio.trim().toUpperCase(Locale.ROOT)) {
            case "ESTANDAR" -> new EnvioEstandar();
            case "EXPRESS" -> new EnvioExpress();
            case "PROGRAMADO" -> new EnvioProgramado();
            default -> throw new IllegalArgumentException("Tipo de envío no válido: " + tipoEnvio);
        };
    }

    private String normalizarPago(String tipoPago) {
        if (tipoPago == null || tipoPago.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar una forma de pago.");
        }

        String normalizado = tipoPago.trim().toUpperCase(Locale.ROOT);
        if (!"YAPE".equals(normalizado) && !"PLIN".equals(normalizado)) {
            throw new IllegalArgumentException("Forma de pago no válida: " + tipoPago);
        }
        return normalizado;
    }

    private void validarCodigoVenta(String codigoVenta) {
        if (codigoVenta == null || codigoVenta.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar el código de venta.");
        }
    }
}
