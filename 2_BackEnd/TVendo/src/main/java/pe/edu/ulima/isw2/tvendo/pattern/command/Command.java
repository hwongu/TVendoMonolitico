package pe.edu.ulima.isw2.tvendo.pattern.command;

/**
 * Interfaz que define el contrato para los comandos en el patrón Command.
 * Cada comando debe implementar el método execute() que encapsula la acción a realizar.
 * Este patrón permite desacoplar el objeto que invoca la operación del objeto que conoce cómo realizarla.
 * @author Henry Wong
 */
public interface Command {
    void execute();
}
