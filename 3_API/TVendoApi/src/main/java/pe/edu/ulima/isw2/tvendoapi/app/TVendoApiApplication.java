package pe.edu.ulima.isw2.tvendoapi.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpServer;
import pe.edu.ulima.isw2.tvendo.adapter.payment.PagoGateway;
import pe.edu.ulima.isw2.tvendo.adapter.payment.plin.PlinAdapter;
import pe.edu.ulima.isw2.tvendo.adapter.payment.plin.PlinClient;
import pe.edu.ulima.isw2.tvendo.adapter.payment.yape.YapeAdapter;
import pe.edu.ulima.isw2.tvendo.adapter.payment.yape.YapeClient;
import pe.edu.ulima.isw2.tvendo.dao.DetalleVentaDAO;
import pe.edu.ulima.isw2.tvendo.dao.PagoDAO;
import pe.edu.ulima.isw2.tvendo.dao.ProductoDAO;
import pe.edu.ulima.isw2.tvendo.dao.VentaDAO;
import pe.edu.ulima.isw2.tvendo.dao.factory.DAOFactory;
import pe.edu.ulima.isw2.tvendo.dao.factory.impl.MySQLDAOFactory;
import pe.edu.ulima.isw2.tvendo.dao.factory.impl.PostgresDAOFactory;
import pe.edu.ulima.isw2.tvendo.datasource.config.DatabaseConfig;
import pe.edu.ulima.isw2.tvendo.datasource.connection.ConnectionFactory;
import pe.edu.ulima.isw2.tvendo.datasource.connection.impl.MySQLConnectionFactory;
import pe.edu.ulima.isw2.tvendo.datasource.connection.impl.PostgresConnectionFactory;
import pe.edu.ulima.isw2.tvendo.facade.VentaFacade;
import pe.edu.ulima.isw2.tvendo.invoker.PanelCaja;
import pe.edu.ulima.isw2.tvendo.service.PagoService;
import pe.edu.ulima.isw2.tvendo.service.ProductoService;
import pe.edu.ulima.isw2.tvendo.service.VentaService;
import pe.edu.ulima.isw2.tvendoapi.controller.ProductoController;
import pe.edu.ulima.isw2.tvendoapi.controller.VentaController;
import pe.edu.ulima.isw2.tvendoapi.mapper.ProductoMapper;
import pe.edu.ulima.isw2.tvendoapi.mapper.VentaMapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;

public class TVendoApiApplication {

    private TVendoApiApplication() {
    }

    public static void main(String[] args) {
        try {
            HttpServer server = createServer();
            server.start();
            System.out.println("TVendo API iniciada");
            System.out.println("http://localhost:8090/api");
        } catch (IOException exception) {
            throw new RuntimeException("No se pudo iniciar TVendo API", exception);
        }
    }

    private static HttpServer createServer() throws IOException {
        // Creación de los clientes y adaptadores para los gateways de pago
        // no usa un patrón de diseño, es solo para poder cambiar entre Yape y Plin según la configuración.
        ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

        // Implementación con singleton para la configuración de la base de datos,
        DatabaseConfig config = DatabaseConfig.getInstance();
        String motor = config.getMotorPrincipal();
        MotorBd motorBd = MotorBd.from(motor);

        // Creación de la conexión usando el patrón Factory Method para poder cambiar entre MySQL y PostgreSQL
        InfraestructuraPersistencia infraestructura = crearInfraestructuraPersistencia(motorBd, config);
        ConnectionFactory connectionFactory = infraestructura.connectionFactory();

        // Creación de la fábrica de DAOs usando el patrón abstract factory para poder cambiar entre MySQL y PostgreSQL
        // según la configuración.
        DAOFactory daoFactory = infraestructura.daoFactory();

        // Creación de los DAOs usando el patron abstract factory para crear los DAOs según el motor de base de datos configurado.
        ProductoDAO productoDAO = daoFactory.crearProductoDAO();
        VentaDAO ventaDAO = daoFactory.crearVentaDAO();
        DetalleVentaDAO detalleVentaDAO = daoFactory.crearDetalleVentaDAO();
        PagoDAO pagoDAO = daoFactory.crearPagoDAO();

        // Creación de los servicios usando los DAOs creados anteriormente y sirve para encapsular la lógica de negocio y las reglas de negocio.
        ProductoService productoService = new ProductoService(productoDAO);
        VentaService ventaService = new VentaService(ventaDAO, detalleVentaDAO, productoDAO, pagoDAO, connectionFactory);

        // Creación de los clientes y adaptadores para los gateways de pago usando el patrón adapter para poder cambiar
        // entre Yape y Plin según la configuración.
        YapeClient yapeClient = new YapeClient(objectMapper);
        PlinClient plinClient = new PlinClient(objectMapper);
        YapeAdapter yapeAdapter = new YapeAdapter(yapeClient);
        PlinAdapter plinAdapter = new PlinAdapter(plinClient);
        // Creación de un mapa de gateways de pago para poder acceder a ellos por su nombre.
        Map<String, PagoGateway> gateways = Map.of("YAPE", yapeAdapter, "PLIN", plinAdapter);

        // Creación del servicio de pagos usando el DAO de pagos y el mapa de gateways de pago.
        PagoService pagoService = new PagoService(pagoDAO, gateways);

        // Creación de la fachada de ventas usando los servicios de productos, ventas y pagos.
        VentaFacade ventaFacade = new VentaFacade(productoService, ventaService, pagoService);

        // Creación del panel de caja que se usará para invocar los comandos de ventas y usar el patrón command
        // para registrar, anular e imprimir comprobantes de ventas.
        PanelCaja panelCaja = new PanelCaja();

        // Creación de los mappers para convertir entre entidades y DTOs.
        ProductoMapper productoMapper = new ProductoMapper();
        VentaMapper ventaMapper = new VentaMapper();

        // Creación de los controllers para manejar las solicitudes HTTP relacionadas con los productos y ventas.
        // No tiene nada que ver con temas del curso pero sirve para exponer como un servicio REST la funcionalidad
        // de productos y ventas sin necesidad de un framework como Spring Boot.
        ProductoController productoController = new ProductoController(productoService, productoMapper, objectMapper);
        VentaController ventaController = new VentaController(ventaFacade, ventaService, pagoService, panelCaja, ventaMapper, objectMapper);

        // Creación del servidor HTTP usando el patrón singleton para poder manejar las solicitudes HTTP
        // y exponer los endpoints de la API.
        // No tiene nada que ver con temas del curso pero sirve para exponer como un servicio REST la funcionalidad
        // de productos y ventas sin necesidad de un framework como Spring Boot.
        HttpServer server = HttpServer.create(new InetSocketAddress(8090), 0);
        server.createContext("/api/productos", productoController);
        server.createContext("/api/ventas", ventaController);
        server.setExecutor(Executors.newFixedThreadPool(8));
        return server;
    }

    private static InfraestructuraPersistencia crearInfraestructuraPersistencia(MotorBd motorBd, DatabaseConfig config) {
        return switch (motorBd) {
            case POSTGRES -> {
                ConnectionFactory connectionFactory = new PostgresConnectionFactory(config);
                yield new InfraestructuraPersistencia(connectionFactory, new PostgresDAOFactory(connectionFactory));
            }
            case MYSQL -> {
                ConnectionFactory connectionFactory = new MySQLConnectionFactory(config);
                yield new InfraestructuraPersistencia(connectionFactory, new MySQLDAOFactory(connectionFactory));
            }
        };
    }
}
