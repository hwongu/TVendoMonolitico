package pe.edu.ulima.isw2.tvendo.service;

import pe.edu.ulima.isw2.tvendo.dao.DetalleVentaDAO;
import pe.edu.ulima.isw2.tvendo.dao.PagoDAO;
import pe.edu.ulima.isw2.tvendo.dao.ProductoDAO;
import pe.edu.ulima.isw2.tvendo.dao.VentaDAO;
import pe.edu.ulima.isw2.tvendo.datasource.connection.ConnectionFactory;
import pe.edu.ulima.isw2.tvendo.entity.DetalleVenta;
import pe.edu.ulima.isw2.tvendo.entity.Pago;
import pe.edu.ulima.isw2.tvendo.entity.Venta;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para gestionar operaciones relacionadas con ventas.
 * Este servicio actúa como una capa de negocio que interactúa con los DAOs para realizar operaciones de venta,
 * incluyendo la creación de ventas, detalles de ventas, pagos y la gestión de transacciones.
 * Proporciona métodos para registrar ventas, buscar ventas por código, listar detalles de ventas y anular ventas.
 * Además, permite registrar una venta completa de manera transaccional, asegurando la consistencia de los datos.
 * @author Henry Wong
 */
public class VentaService {

    private final VentaDAO ventaDAO;
    private final DetalleVentaDAO detalleVentaDAO;
    private final ProductoDAO productoDAO;
    private final PagoDAO pagoDAO;
    private final ConnectionFactory connectionFactory;

    public VentaService(VentaDAO ventaDAO, DetalleVentaDAO detalleVentaDAO, ProductoDAO productoDAO, PagoDAO pagoDAO, ConnectionFactory connectionFactory) {
        this.ventaDAO = ventaDAO;
        this.detalleVentaDAO = detalleVentaDAO;
        this.productoDAO = productoDAO;
        this.pagoDAO = pagoDAO;
        this.connectionFactory = connectionFactory;
    }

    public Venta registrarVenta(Venta venta) {
        return ventaDAO.guardar(venta);
    }

    public DetalleVenta registrarDetalle(DetalleVenta detalleVenta) {
        return detalleVentaDAO.guardar(detalleVenta);
    }

    public Optional<Venta> buscarPorCodigo(String codigoVenta) {
        return ventaDAO.buscarPorCodigo(codigoVenta);
    }

    public List<DetalleVenta> listarDetallesPorVentaId(Long ventaId) {
        return detalleVentaDAO.listarPorVentaId(ventaId);
    }

    public void anularVenta(Long ventaId) {
        ventaDAO.actualizarEstado(ventaId, "ANULADA");
    }

    public Venta registrarVentaCompletaTransaccional(Venta venta, List<DetalleVenta> detalles, Map<Long, Integer> nuevoStockPorProducto, Pago pago) {
        try (Connection connection = connectionFactory.crearConexion()) {
            boolean autoCommitOriginal = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                Venta ventaGuardada = ventaDAO.guardar(connection, venta);
                for (DetalleVenta detalle : detalles) {
                    detalle.setVentaId(ventaGuardada.getId());
                    detalleVentaDAO.guardar(connection, detalle);
                }
                for (Map.Entry<Long, Integer> entry : nuevoStockPorProducto.entrySet()) {
                    productoDAO.actualizarStock(connection, entry.getKey(), entry.getValue());
                }
                pago.setVentaId(ventaGuardada.getId());
                pagoDAO.guardar(connection, pago);
                connection.commit();
                connection.setAutoCommit(autoCommitOriginal);
                return ventaGuardada;
            } catch (RuntimeException exception) {
                hacerRollbackSilencioso(connection);
                restaurarAutoCommitSilencioso(connection, autoCommitOriginal);
                throw exception;
            } catch (SQLException exception) {
                hacerRollbackSilencioso(connection);
                restaurarAutoCommitSilencioso(connection, autoCommitOriginal);
                throw new RuntimeException("Error al confirmar la venta de forma transaccional", exception);
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Error al iniciar transacción de venta", exception);
        }
    }

    private void hacerRollbackSilencioso(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException rollbackException) {
            throw new RuntimeException("Error al realizar rollback de la venta", rollbackException);
        }
    }

    private void restaurarAutoCommitSilencioso(Connection connection, boolean autoCommitOriginal) {
        try {
            connection.setAutoCommit(autoCommitOriginal);
        } catch (SQLException exception) {
            throw new RuntimeException("Error al restaurar auto-commit de la conexión", exception);
        }
    }
}
