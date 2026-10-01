package pe.edu.ulima.isw2.tvendo.pattern.command.impl;

import pe.edu.ulima.isw2.tvendo.facade.VentaFacade;
import pe.edu.ulima.isw2.tvendo.pattern.command.Command;

/**
 * Comando para imprimir el comprobante de una venta.
 * Este comando utiliza la fachada de ventas para realizar la acción de impresión del comprobante
 * correspondiente a un código de venta específico.
 */
public class ImprimirComprobanteCommand implements Command {

    private final VentaFacade ventaFacade;
    private final String codigoVenta;

    public ImprimirComprobanteCommand(VentaFacade ventaFacade, String codigoVenta) {
        this.ventaFacade = ventaFacade;
        this.codigoVenta = codigoVenta;
    }

    @Override
    public void execute() {
        ventaFacade.imprimirComprobante(codigoVenta);
    }
}
