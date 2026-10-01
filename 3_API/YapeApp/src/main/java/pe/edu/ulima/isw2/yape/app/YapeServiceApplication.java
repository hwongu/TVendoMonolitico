package pe.edu.ulima.isw2.yape.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpServer;
import pe.edu.ulima.isw2.yape.config.DatabaseConfig;
import pe.edu.ulima.isw2.yape.connection.ConnectionFactory;
import pe.edu.ulima.isw2.yape.connection.PostgresConnectionFactory;
import pe.edu.ulima.isw2.yape.controller.PagoYapeController;
import pe.edu.ulima.isw2.yape.dao.PagoYapeDAO;
import pe.edu.ulima.isw2.yape.dao.impl.PagoYapeDAOImpl;
import pe.edu.ulima.isw2.yape.exception.GlobalExceptionHandler;
import pe.edu.ulima.isw2.yape.mapper.PagoYapeMapper;
import pe.edu.ulima.isw2.yape.service.PagoYapeService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class YapeServiceApplication {

    public static void main(String[] args) throws IOException {
        // Estamos usando JsonMapper para poder registrar el módulo JavaTimeModule y
        // deshabilitar la serialización de fechas como timestamps.
        // No tiene nada que ver con los patrones, es solo una forma de configurar el ObjectMapper.
        ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Creo una instancia del manejador de excepciones global para poder usarlo en el controller.
        // Estos conceptos lo vimos en la Semana 01 de POO de crear excepciones personalizadas.
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

        // Obtiene una instancia de la configuración de la base de datos usando el patrón Singleton.
        DatabaseConfig config = DatabaseConfig.getInstance();

        // Establece la conexión con la base de datos usando el patrón Factory Method,
        // en este caso para PostgreSQL.
        ConnectionFactory connectionFactory = new PostgresConnectionFactory(config);

        // Crea el DAO, Mapper y Service para el pago con Yape.
        // Al DAO le enviamos el connectionFactory para que pueda conectarse a la base de datos.
        // El mapper sirve para convertir entre entidades y DTOs, y el service contiene la lógica de negocio.
        PagoYapeDAO dao = new PagoYapeDAOImpl(connectionFactory);
        PagoYapeMapper mapper = new PagoYapeMapper();
        PagoYapeService service = new PagoYapeService(dao, mapper);

        // Crea el controller para manejar las solicitudes HTTP relacionadas con los pagos de Yape.
        // Esto no tiene que ver con temas del curso pero sirve para exponer como un servicio REST la funcionalidad
        // de pagos con Yape sin necesidad de un framework como Spring Boot.
        // Todo este código se optimiza con Spring Boot, pero lo hacemos a mano para que vean como funciona por debajo.
        PagoYapeController controller = new PagoYapeController(service, objectMapper, exceptionHandler);
        HttpServer server = HttpServer.create(new InetSocketAddress(config.getPuertoRest()), 0);
        server.createContext("/api/v1/yape/pagos", controller);
        ExecutorService executor = Executors.newFixedThreadPool(4);
        server.setExecutor(executor);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop(0);
            executor.shutdown();
        }));
        server.start();
        System.out.println("Yape Service iniciado");
        System.out.println("http://localhost:8091/api/v1/yape/pagos");
    }
}
