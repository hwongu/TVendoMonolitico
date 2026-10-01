package pe.edu.ulima.isw2.tvendo.datasource.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Clase singleton que carga la configuración de las URLs de los servicios Yape y Plin desde un archivo properties.
 * @author Henry Wong
 */
public final class ApiConfig {

    private static final ApiConfig INSTANCE = new ApiConfig();
    private static final String ARCHIVO_CONFIG = "tvendo.properties";

    private final String yapeUrl;
    private final String plinUrl;

    private ApiConfig() {
        Properties properties = cargarProperties();
        String yapeUrlConfig = properties.getProperty("yape.url", "http://localhost:8091/api/v1/yape/pagos");
        String plinUrlConfig = properties.getProperty("plin.url", "http://localhost:8092/api/v1/plin/pagos");

        this.yapeUrl = System.getProperty("yape.url", yapeUrlConfig);
        this.plinUrl = System.getProperty("plin.url", plinUrlConfig);
    }

    public static ApiConfig getInstance() {
        return INSTANCE;
    }

    public String getYapeUrl() {
        return yapeUrl;
    }

    public String getPlinUrl() {
        return plinUrl;
    }

    private Properties cargarProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = ApiConfig.class.getClassLoader().getResourceAsStream(ARCHIVO_CONFIG)) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo " + ARCHIVO_CONFIG, e);
        }
    }
}
