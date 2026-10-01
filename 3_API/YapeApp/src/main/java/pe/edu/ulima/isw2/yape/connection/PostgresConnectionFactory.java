package pe.edu.ulima.isw2.yape.connection;

import pe.edu.ulima.isw2.yape.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Implementación de ConnectionFactory para PostgreSQL.
 * Esta clase proporciona la lógica para crear conexiones a una base de datos PostgreSQL
 * utilizando la configuración proporcionada en DatabaseConfig.
 * @author Henry Wong
 */
public class PostgresConnectionFactory implements ConnectionFactory {

    private final DatabaseConfig databaseConfig;

    public PostgresConnectionFactory(DatabaseConfig databaseConfig) {
        this.databaseConfig = Objects.requireNonNull(databaseConfig, "DatabaseConfig es obligatorio");
    }

    @Override
    public Connection crearConexion() throws SQLException {
        return DriverManager.getConnection(databaseConfig.getUrl(), databaseConfig.getUsuario(), databaseConfig.getPassword());
    }
}
