package pe.edu.ulima.isw2.tvendo.datasource.connection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Clase abstracta que define un contrato para la creación de conexiones a una base de datos.
 * Implementaciones de esta clase deben proporcionar la lógica para establecer y devolver
 * una conexión válida a la base de datos y usa el patrón Factory Method para permitir la creación
 * de diferentes tipos de conexiones según la configuración.
 * Implementa el patrón de diseño Factory Method para permitir la creación de diferentes
 * tipos de conexiones según la configuración.
 * @author Henry Wong
 */
public abstract class ConnectionFactory {
    public abstract Connection crearConexion() throws SQLException;
}
