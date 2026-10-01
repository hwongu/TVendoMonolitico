package pe.edu.ulima.isw2.tvendo.invoker;

import pe.edu.ulima.isw2.tvendo.pattern.command.Command;

/**
 * Clase que representa un panel de caja en un sistema de ventas.
 * Implementa el patrón Command para ejecutar comandos relacionados con la caja.
 * Permite configurar un comando y ejecutarlo cuando sea necesario.
 * @author Henry Wong
 */
public class PanelCaja {

    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void ejecutar() {
        if (command == null) {
            throw new IllegalStateException("No existe un comando configurado");
        }
        command.execute();
    }
}
