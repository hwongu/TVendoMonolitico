package pe.edu.ulima.isw2.yape.config;

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
        Properties propiedades = new Properties();
        try (InputStream inputStream = DatabaseConfig.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (inputStream == null) {
                throw new IllegalStateException("No se encontró el archivo application.properties");
            }
            propiedades.load(inputStream);
        } catch (IOException exception) {
            throw new IllegalStateException("Error al cargar application.properties", exception);
        }
        this.url = leerTextoObligatorio(propiedades, "db.url");
        this.usuario = leerTextoObligatorio(propiedades, "db.user");
        this.password = leerTextoObligatorio(propiedades, "db.password");
        this.puertoRest = leerEnteroObligatorio(propiedades, "rest.port");
    }

    public static DatabaseConfig getInstance() {
        return INSTANCE;
    }

    private String leerTextoObligatorio(Properties propiedades, String clave) {
        String valor = propiedades.getProperty(clave);
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("La propiedad es obligatoria: " + clave);
        }
        return valor.trim();
    }

    private int leerEnteroObligatorio(Properties propiedades, String clave) {
        String valor = leerTextoObligatorio(propiedades, clave);
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("La propiedad debe ser numérica: " + clave, exception);
        }
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
}
