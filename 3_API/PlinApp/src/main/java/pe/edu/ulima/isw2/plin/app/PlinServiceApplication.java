package pe.edu.ulima.isw2.plin.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpServer;
import pe.edu.ulima.isw2.plin.config.DatabaseConfig;
import pe.edu.ulima.isw2.plin.connection.ConnectionFactory;
import pe.edu.ulima.isw2.plin.connection.PostgresConnectionFactory;
import pe.edu.ulima.isw2.plin.controller.PagoPlinController;
import pe.edu.ulima.isw2.plin.dao.PagoPlinDAO;
import pe.edu.ulima.isw2.plin.dao.impl.PagoPlinDAOImpl;
import pe.edu.ulima.isw2.plin.exception.GlobalExceptionHandler;
import pe.edu.ulima.isw2.plin.mapper.PagoPlinMapper;
import pe.edu.ulima.isw2.plin.service.PagoPlinService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class PlinServiceApplication {

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

        // Crea el DAO, Mapper y Service para el pago con Plin.
        // Al DAO le enviamos el connectionFactory para que pueda conectarse a la base de datos.
        // El mapper sirve para convertir entre entidades y DTOs, y el service contiene la lógica de negocio.
        PagoPlinDAO dao = new PagoPlinDAOImpl(connectionFactory);
        PagoPlinMapper mapper = new PagoPlinMapper();
        PagoPlinService service = new PagoPlinService(dao, mapper);

        // Crea el controller para manejar las solicitudes HTTP relacionadas con los pagos de Plin.
        // Esto no tiene que ver con temas del curso pero sirve para exponer como un servicio REST la funcionalidad
        // de pagos con Plin sin necesidad de un framework como Spring Boot.
        // Todo este código se optimiza con Spring Boot, pero lo hacemos a mano para que vean como funciona por debajo.
        PagoPlinController controller = new PagoPlinController(service, objectMapper, exceptionHandler);
        HttpServer server = HttpServer.create(new InetSocketAddress(config.getPuertoRest()), 0);
        server.createContext("/api/v1/plin/pagos", controller);
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
        System.out.println("Plin Service iniciado");
        System.out.println("http://localhost:" + config.getPuertoRest() + "/api/v1/plin/pagos");
    }
}
