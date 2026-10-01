package pe.edu.ulima.isw2.tvendo.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import pe.edu.ulima.isw2.tvendo.datasource.config.ApiConfig;
import pe.edu.ulima.isw2.tvendo.datasource.config.DatabaseConfig;
import pe.edu.ulima.isw2.tvendo.datasource.config.MotorBD;
import pe.edu.ulima.isw2.tvendo.datasource.connection.ConnectionFactory;
import pe.edu.ulima.isw2.tvendo.datasource.connection.impl.MySQLConnectionFactory;
import pe.edu.ulima.isw2.tvendo.datasource.connection.impl.PostgresConnectionFactory;
import pe.edu.ulima.isw2.tvendo.entity.Producto;
import pe.edu.ulima.isw2.tvendo.facade.VentaFacade;
import pe.edu.ulima.isw2.tvendo.invoker.PanelCaja;
import pe.edu.ulima.isw2.tvendo.pattern.command.impl.AnularVentaCommand;
import pe.edu.ulima.isw2.tvendo.pattern.command.impl.ImprimirComprobanteCommand;
import pe.edu.ulima.isw2.tvendo.pattern.command.impl.RegistrarVentaCommand;
import pe.edu.ulima.isw2.tvendo.pattern.strategy.EstrategiaEnvio;
import pe.edu.ulima.isw2.tvendo.pattern.strategy.impl.EnvioEstandar;
import pe.edu.ulima.isw2.tvendo.service.PagoService;
import pe.edu.ulima.isw2.tvendo.service.ProductoService;
import pe.edu.ulima.isw2.tvendo.service.VentaService;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        try {
            // Creación de los clientes y adaptadores de pago usando el patrón Adapter para poder cambiar
            // entre Yape y Plin
            ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

            // Implementación con singleton para la configuración de la base de datos,
            // permitiendo cambiar entre MySQL y PostgreSQL según la configuración.
            DatabaseConfig config = DatabaseConfig.getInstance();
            MotorBD motor = config.getMotorPrincipal();

            // Implementación con singleton para la configuración de las APIs de pago,
            // permitiendo cambiar entre Yape y Plin según la configuración.
            ApiConfig apiConfig = ApiConfig.getInstance();

            // Creación de la conexión usando el patrón Factory Method para poder cambiar entre MySQL y PostgreSQL
            // según la configuración.
            ConnectionFactory connectionFactory;
            DAOFactory daoFactory;
            if (motor == MotorBD.POSTGRES) {
                connectionFactory = new PostgresConnectionFactory(config);
                daoFactory = new PostgresDAOFactory(connectionFactory);
            } else {
                connectionFactory = new MySQLConnectionFactory(config);
                daoFactory = new MySQLDAOFactory(connectionFactory);
            }

            // Creación de los DAOs usando el patrón Factory Method para poder cambiar entre MySQL y PostgreSQL
            ProductoDAO productoDAO = daoFactory.crearProductoDAO();
            VentaDAO ventaDAO = daoFactory.crearVentaDAO();
            DetalleVentaDAO detalleVentaDAO = daoFactory.crearDetalleVentaDAO();
            PagoDAO pagoDAO = daoFactory.crearPagoDAO();

            // Creación de los servicios usando los DAOs y la conexión, siguiendo el patrón de diseño Service Layer.
            ProductoService productoService = new ProductoService(productoDAO);
            VentaService ventaService = new VentaService(ventaDAO, detalleVentaDAO, productoDAO, pagoDAO, connectionFactory);

            // Creación de los clientes y adaptadores de pago usando el patrón Adapter para poder cambiar entre Yape y Plin
            YapeClient yapeClient = new YapeClient(objectMapper, apiConfig.getYapeUrl());
            PlinClient plinClient = new PlinClient(objectMapper, apiConfig.getPlinUrl());
            YapeAdapter yapeAdapter = new YapeAdapter(yapeClient);
            PlinAdapter plinAdapter = new PlinAdapter(plinClient);
            Map<String, PagoGateway> gateways = Map.of("YAPE", yapeAdapter, "PLIN", plinAdapter);

            // Creación del servicio de pago usando los adaptadores de pago, siguiendo el patrón de diseño Service Layer.
            PagoService pagoService = new PagoService(pagoDAO, gateways);

            // Creación de la fachada de venta usando los servicios, siguiendo el patrón de diseño Facade.
            VentaFacade ventaFacade = new VentaFacade(productoService, ventaService, pagoService);

            // Creación del invocador de comandos usando el patrón Command para poder ejecutar las acciones de venta.
            PanelCaja panelCaja = new PanelCaja();

            System.out.println("Motor de base de datos en uso: " + motor.name());
            System.out.println("Productos disponibles:");
            List<Producto> productosDisponibles = productoService.listarProductos();
            for (Producto producto : productosDisponibles) {
                System.out.println("- " + producto.getCodigo() + " | " + producto.getNombre() + " | S/ " + producto.getPrecio() + " | stock: " + producto.getStock());
            }
            // Ejemplo de registro de venta con YAPE
            // Creación de un mapa de productos con sus cantidades para la venta.
            Map<String, Integer> productos = Map.of("P-001", 1, "P-002", 1);

            // Creación de la estrategia de envío usando el patrón Strategy para poder cambiar entre diferentes estrategias.
            EstrategiaEnvio estrategia = new EnvioEstandar();

            // Creación del comando de registro de venta usando el patrón Command para poder ejecutar la acción de venta.
            RegistrarVentaCommand registrarVentaCommand = new RegistrarVentaCommand(ventaFacade, "V-004", productos, estrategia, "YAPE", "999111222", "José Pérez", "Pago de venta V-002");
            panelCaja.setCommand(registrarVentaCommand);
            panelCaja.ejecutar();

            //Creación del comando de impresión de comprobante usando el patrón Command para poder ejecutar la acción de impresión.
            ImprimirComprobanteCommand imprimirComprobanteCommand = new ImprimirComprobanteCommand(ventaFacade, "V-002");
            panelCaja.setCommand(imprimirComprobanteCommand);
            panelCaja.ejecutar();

            // Ejemplo de anulación (comentado para conservar la venta registrada):
            // AnularVentaCommand anularVentaCommand = new AnularVentaCommand(ventaFacade, "V-002");
            // panelCaja.setCommand(anularVentaCommand);
            // panelCaja.ejecutar();

            // Ejemplo de pago con PLIN (solo cambia el tipo y número):
            // RegistrarVentaCommand registrarConPlin = new RegistrarVentaCommand(
            //         ventaFacade,
            //         "V-003",
            //         productos,
            //         estrategia,
            //         "PLIN",
            //         "988333444",
            //         "María Núñez",
            //         "Pago de venta V-003"
            // );
            // panelCaja.setCommand(registrarConPlin);
            // panelCaja.ejecutar();
        } catch (RuntimeException exception) {
            System.err.println("Error al ejecutar demo de TVendo: " + exception.getMessage());
            exception.printStackTrace();
        }
    }
}
