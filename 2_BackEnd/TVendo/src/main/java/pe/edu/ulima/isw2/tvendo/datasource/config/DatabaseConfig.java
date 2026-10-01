package pe.edu.ulima.isw2.tvendo.datasource.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

/**
 * Clase singleton que carga la configuración de la base de datos desde un archivo properties.
 * Permite obtener la configuración para MySQL y PostgreSQL, así como el motor principal a utilizar.
 * @author Henry Wong
 */
public final class DatabaseConfig {

    private static final DatabaseConfig INSTANCE = new DatabaseConfig();
    private static final String ARCHIVO_CONFIG = "tvendo.properties";

    private final String mysqlUrl;
    private final String mysqlUser;
    private final String mysqlPassword;
    private final String postgresUrl;
    private final String postgresUser;
    private final String postgresPassword;
    private final MotorBD motorPrincipal;

    private DatabaseConfig() {
        Properties properties = cargarProperties();

        this.mysqlUrl = properties.getProperty("db.mysql.url", "jdbc:mysql://localhost:3307/isw2_mysql?useUnicode=true&characterEncoding=UTF-8");
        this.mysqlUser = properties.getProperty("db.mysql.user", "root");
        this.mysqlPassword = properties.getProperty("db.mysql.password", "clave123");
        this.postgresUrl = properties.getProperty("db.postgres.url", "jdbc:postgresql://localhost:5434/isw2_postgres");
        this.postgresUser = properties.getProperty("db.postgres.user", "postgres");
        this.postgresPassword = properties.getProperty("db.postgres.password", "clave123");
        String motorConfig = properties.getProperty("db.motor", MotorBD.MYSQL.name());
        String motorFinal = System.getProperty("db.motor", motorConfig).toUpperCase(Locale.ROOT);
        try {
            this.motorPrincipal = MotorBD.valueOf(motorFinal);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Motor de base de datos no soportado: " + motorFinal, exception);
        }
    }

    private Properties cargarProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = DatabaseConfig.class.getClassLoader().getResourceAsStream(ARCHIVO_CONFIG)) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo " + ARCHIVO_CONFIG, e);
        }
    }

    public static DatabaseConfig getInstance() {
        return INSTANCE;
    }

    public String getMysqlUrl() {
        return mysqlUrl;
    }

    public String getMysqlUser() {
        return mysqlUser;
    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }

    public String getPostgresUrl() {
        return postgresUrl;
    }

    public String getPostgresUser() {
        return postgresUser;
    }

    public String getPostgresPassword() {
        return postgresPassword;
    }

    public MotorBD getMotorPrincipal() {
        return motorPrincipal;
    }
}
