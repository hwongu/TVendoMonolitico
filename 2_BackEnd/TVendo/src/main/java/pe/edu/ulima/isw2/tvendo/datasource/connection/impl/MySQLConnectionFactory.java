package pe.edu.ulima.isw2.tvendo.datasource.connection.impl;

import pe.edu.ulima.isw2.tvendo.datasource.config.DatabaseConfig;
import pe.edu.ulima.isw2.tvendo.datasource.connection.ConnectionFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Implementación de ConnectionFactory para MySQL.
 * Esta clase proporciona la lógica para crear conexiones a una base de datos MySQL
 * utilizando la configuración proporcionada en DatabaseConfig.
 * @author Henry Wong
 */
public class MySQLConnectionFactory extends ConnectionFactory {

    private final DatabaseConfig databaseConfig;

    public MySQLConnectionFactory(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    @Override
    public Connection crearConexion() throws SQLException {
        return DriverManager.getConnection(
                databaseConfig.getMysqlUrl(),
                databaseConfig.getMysqlUser(),
                databaseConfig.getMysqlPassword()
        );
    }
}
