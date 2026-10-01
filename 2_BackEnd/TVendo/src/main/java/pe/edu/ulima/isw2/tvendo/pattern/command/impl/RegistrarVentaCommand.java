package pe.edu.ulima.isw2.tvendo.pattern.command.impl;

import pe.edu.ulima.isw2.tvendo.facade.VentaFacade;
import pe.edu.ulima.isw2.tvendo.pattern.command.Command;
import pe.edu.ulima.isw2.tvendo.pattern.strategy.EstrategiaEnvio;

import java.util.Map;

/**
 * Comando para registrar una venta en el sistema.
 * Implementa la interfaz Command del patrón Command.
 * Este comando encapsula la información necesaria para registrar una venta,
 * incluyendo el código de venta, los productos involucrados, la estrategia de envío,
 * el tipo de pago, el número de destino, el titular y la descripción del pago.
 * Al ejecutar este comando, se delega la responsabilidad de registrar la venta al facade correspondiente.
 */
public class RegistrarVentaCommand implements Command {

    private final VentaFacade ventaFacade;
    private final String codigoVenta;
    private final Map<String, Integer> productos;
    private final EstrategiaEnvio estrategiaEnvio;
    private final String tipoPago;
    private final String numeroDestino;
    private final String titular;
    private final String descripcionPago;

    public RegistrarVentaCommand(
            VentaFacade ventaFacade,
            String codigoVenta,
            Map<String, Integer> productos,
            EstrategiaEnvio estrategiaEnvio,
            String tipoPago,
            String numeroDestino,
            String titular,
            String descripcionPago
    ) {
        this.ventaFacade = ventaFacade;
        this.codigoVenta = codigoVenta;
        this.productos = productos;
        this.estrategiaEnvio = estrategiaEnvio;
        this.tipoPago = tipoPago;
        this.numeroDestino = numeroDestino;
        this.titular = titular;
        this.descripcionPago = descripcionPago;
    }

    @Override
    public void execute() {
        ventaFacade.registrarVenta(
                codigoVenta,
                productos,
                estrategiaEnvio,
                tipoPago,
                numeroDestino,
                titular,
                descripcionPago
        );
    }
}
