package pe.edu.ulima.isw2.tvendodesktop.app;

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
import pe.edu.ulima.isw2.tvendo.datasource.config.DatabaseConfig;
import pe.edu.ulima.isw2.tvendo.datasource.connection.ConnectionFactory;
import pe.edu.ulima.isw2.tvendo.datasource.connection.impl.MySQLConnectionFactory;
import pe.edu.ulima.isw2.tvendo.datasource.connection.impl.PostgresConnectionFactory;
import pe.edu.ulima.isw2.tvendo.facade.VentaFacade;
import pe.edu.ulima.isw2.tvendo.invoker.PanelCaja;
import pe.edu.ulima.isw2.tvendo.service.PagoService;
import pe.edu.ulima.isw2.tvendo.service.ProductoService;
import pe.edu.ulima.isw2.tvendo.service.VentaService;
import pe.edu.ulima.isw2.tvendodesktop.controller.VentaUIController;
import pe.edu.ulima.isw2.tvendodesktop.view.VentanaPrincipal;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.util.Map;

public final class MainDesktop {

    private MainDesktop() {
    }

    public static void main(String[] args) {
        try {
            DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
            String motorPrincipal = databaseConfig.getMotorPrincipal();

            ConnectionFactory connectionFactory;
            DAOFactory daoFactory;
            if ("POSTGRES".equalsIgnoreCase(motorPrincipal)) {
                connectionFactory = new PostgresConnectionFactory(databaseConfig);
                daoFactory = new PostgresDAOFactory(connectionFactory);
            } else {
                connectionFactory = new MySQLConnectionFactory(databaseConfig);
                daoFactory = new MySQLDAOFactory(connectionFactory);
            }

            ProductoDAO productoDAO = daoFactory.crearProductoDAO();
            VentaDAO ventaDAO = daoFactory.crearVentaDAO();
            DetalleVentaDAO detalleVentaDAO = daoFactory.crearDetalleVentaDAO();
            PagoDAO pagoDAO = daoFactory.crearPagoDAO();

            ProductoService productoService = new ProductoService(productoDAO);
            VentaService ventaService = new VentaService(
                    ventaDAO,
                    detalleVentaDAO,
                    productoDAO,
                    pagoDAO,
                    connectionFactory
            );

            ObjectMapper objectMapper = JsonMapper.builder()
                    .addModule(new JavaTimeModule())
                    .build();
            YapeClient yapeClient = new YapeClient(objectMapper);
            PlinClient plinClient = new PlinClient(objectMapper);
            YapeAdapter yapeAdapter = new YapeAdapter(yapeClient);
            PlinAdapter plinAdapter = new PlinAdapter(plinClient);

            Map<String, PagoGateway> pagosPorTipo = Map.of(
                    "YAPE", yapeAdapter,
                    "PLIN", plinAdapter
            );

            PagoService pagoService = new PagoService(pagoDAO, pagosPorTipo);
            VentaFacade ventaFacade = new VentaFacade(productoService, ventaService, pagoService);
            PanelCaja panelCaja = new PanelCaja();

            VentaUIController controller = new VentaUIController(
                    productoService,
                    ventaService,
                    ventaFacade,
                    panelCaja
            );

            SwingUtilities.invokeLater(() -> {
                VentanaPrincipal ventanaPrincipal = new VentanaPrincipal(controller);
                ventanaPrincipal.setVisible(true);
            });
        } catch (RuntimeException ex) {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error al iniciar TVendo Desktop",
                    JOptionPane.ERROR_MESSAGE
            ));
        }
    }
}
