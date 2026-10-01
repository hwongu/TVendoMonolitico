package pe.edu.ulima.isw2.plin.connection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interfaz que define un contrato para la creación de conexiones a una base de datos.
 * Implementaciones de esta interfaz deben proporcionar la lógica para establecer y devolver
 * una conexión válida a la base de datos y usa el patrón Factory Method para permitir la creación
 * de diferentes tipos de conexiones según la configuración.
 * @author Henry Wong
 */
public interface ConnectionFactory {

    Connection crearConexion() throws SQLException;
}
