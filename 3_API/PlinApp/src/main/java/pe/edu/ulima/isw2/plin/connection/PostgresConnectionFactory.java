package pe.edu.ulima.isw2.plin.connection;

import pe.edu.ulima.isw2.plin.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Implementación de ConnectionFactory para PostgreSQL.
 * Esta clase proporciona la lógica para crear conexiones a una base de datos PostgreSQL
 * utilizando la configuración proporcionada en DatabaseConfig.
 * @author Henry Wong
 */
public class PostgresConnectionFactory implements ConnectionFactory {

    private final DatabaseConfig config;

    public PostgresConnectionFactory(DatabaseConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("La configuración de base de datos es obligatoria");
        }
        this.config = config;
    }

    @Override
    public Connection crearConexion() throws SQLException {
        return DriverManager.getConnection(
                config.getUrl(),
                config.getUsuario(),
                config.getPassword()
        );
    }
}
