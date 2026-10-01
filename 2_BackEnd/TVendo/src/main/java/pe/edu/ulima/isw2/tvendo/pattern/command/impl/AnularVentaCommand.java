package pe.edu.ulima.isw2.tvendo.pattern.command.impl;

import pe.edu.ulima.isw2.tvendo.facade.VentaFacade;
import pe.edu.ulima.isw2.tvendo.pattern.command.Command;

/**
 * Comando para anular una venta.
 * Implementa la interfaz Command y encapsula la acción de anular una venta específica.
 * Utiliza el facade de ventas para realizar la operación de anulación.
 * @author Henry Wong
 */
public class AnularVentaCommand implements Command {

    private final VentaFacade ventaFacade;
    private final String codigoVenta;

    public AnularVentaCommand(VentaFacade ventaFacade, String codigoVenta) {
        this.ventaFacade = ventaFacade;
        this.codigoVenta = codigoVenta;
    }

    @Override
    public void execute() {
        ventaFacade.anularVenta(codigoVenta);
    }
}
