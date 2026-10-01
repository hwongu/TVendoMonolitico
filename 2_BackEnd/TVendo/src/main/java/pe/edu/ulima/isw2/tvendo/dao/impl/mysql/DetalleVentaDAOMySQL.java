package pe.edu.ulima.isw2.tvendo.dao.impl.mysql;

import pe.edu.ulima.isw2.tvendo.dao.DetalleVentaDAO;
import pe.edu.ulima.isw2.tvendo.datasource.connection.ConnectionFactory;
import pe.edu.ulima.isw2.tvendo.entity.DetalleVenta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la interfaz DetalleVentaDAO para el acceso a datos de detalles de ventas.
 * Esta clase utiliza JDBC para interactuar con una base de datos MySQL y realizar operaciones CRUD
 * sobre la entidad DetalleVenta.
 * @author Henry Wong
 */
public class DetalleVentaDAOMySQL implements DetalleVentaDAO {

    private final ConnectionFactory connectionFactory;

    public DetalleVentaDAOMySQL(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public DetalleVenta guardar(DetalleVenta detalle) {
        try (Connection connection = connectionFactory.crearConexion()) {
            return guardar(connection, detalle);
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar detalle de venta en MySQL", e);
        }
    }

    @Override
    public DetalleVenta guardar(Connection connection, DetalleVenta detalle) {
        String sql = "INSERT INTO detalle_venta (venta_id, producto_id, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, detalle.getVentaId());
            statement.setLong(2, detalle.getProductoId());
            statement.setInt(3, detalle.getCantidad());
            statement.setBigDecimal(4, detalle.getPrecioUnitario());
            statement.setBigDecimal(5, detalle.getSubtotal());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    detalle.setId(generatedKeys.getLong(1));
                }
            }
            return detalle;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar detalle de venta en MySQL", e);
        }
    }

    @Override
    public List<DetalleVenta> listarPorVentaId(Long ventaId) {
        String sql = "SELECT id, venta_id, producto_id, cantidad, precio_unitario, subtotal FROM detalle_venta WHERE venta_id = ? ORDER BY id";
        List<DetalleVenta> detalles = new ArrayList<>();
        try (Connection connection = connectionFactory.crearConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, ventaId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    detalles.add(mapearDetalle(resultSet));
                }
            }
            return detalles;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar detalle de venta en MySQL", e);
        }
    }

    private DetalleVenta mapearDetalle(ResultSet resultSet) throws SQLException {
        return new DetalleVenta(
                resultSet.getLong("id"),
                resultSet.getLong("venta_id"),
                resultSet.getLong("producto_id"),
                resultSet.getInt("cantidad"),
                resultSet.getBigDecimal("precio_unitario"),
                resultSet.getBigDecimal("subtotal")
        );
    }
}
