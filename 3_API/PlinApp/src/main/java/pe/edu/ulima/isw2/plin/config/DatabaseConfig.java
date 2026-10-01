package pe.edu.ulima.isw2.plin.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Clase singleton que carga la configuración de la base de datos desde un archivo properties.
 * @author Henry Wong
 *
 */
public final class DatabaseConfig {

    private static final DatabaseConfig INSTANCE = new DatabaseConfig();

    private final String url;
    private final String usuario;
    private final String password;
    private final int puertoRest;

    private DatabaseConfig() {
        Properties properties = cargarProperties();
        this.url = obtenerRequerido(properties, "db.url");
        this.usuario = obtenerRequerido(properties, "db.usuario");
        this.password = obtenerRequerido(properties, "db.password");
        this.puertoRest = Integer.parseInt(obtenerRequerido(properties, "server.puerto"));
    }

    public static DatabaseConfig getInstance() {
        return INSTANCE;
    }

    public String getUrl() {
        return url;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getPassword() {
        return password;
    }

    public int getPuertoRest() {
        return puertoRest;
    }

    private Properties cargarProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = DatabaseConfig.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (inputStream == null) {
                throw new IllegalStateException("No se encontró application.properties en classpath");
            }
            properties.load(inputStream);
            return properties;
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo leer application.properties", exception);
        }
    }

    private String obtenerRequerido(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Falta la propiedad obligatoria: " + key);
        }
        return value;
    }
}
