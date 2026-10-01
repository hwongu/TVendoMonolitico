package pe.edu.ulima.isw2.tvendo.dao.impl.postgres;

import pe.edu.ulima.isw2.tvendo.dao.VentaDAO;
import pe.edu.ulima.isw2.tvendo.datasource.connection.ConnectionFactory;
import pe.edu.ulima.isw2.tvendo.entity.Venta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

/**
 * Implementación de la interfaz VentaDAO para el acceso a datos de ventas.
 * Esta clase utiliza JDBC para interactuar con una base de datos PostgreSQL y realizar operaciones CRUD
 * sobre la entidad Venta.
 * @author Henry Wong
 */
public class VentaDAOPostgres implements VentaDAO {

    private final ConnectionFactory connectionFactory;

    public VentaDAOPostgres(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Venta guardar(Venta venta) {
        try (Connection connection = connectionFactory.crearConexion()) {
            return guardar(connection, venta);
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar venta en PostgreSQL", e);
        }
    }

    @Override
    public Venta guardar(Connection connection, Venta venta) {
        String sql = "INSERT INTO venta (codigo, fecha, subtotal, costo_envio, total, estado, observacion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, venta.getCodigo());
            statement.setTimestamp(2, Timestamp.valueOf(venta.getFecha()));
            statement.setBigDecimal(3, venta.getSubtotal());
            statement.setBigDecimal(4, venta.getCostoEnvio());
            statement.setBigDecimal(5, venta.getTotal());
            statement.setString(6, venta.getEstado());
            statement.setString(7, venta.getObservacion());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    venta.setId(generatedKeys.getLong(1));
                }
            }
            return venta;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar venta en PostgreSQL", e);
        }
    }

    @Override
    public Optional<Venta> buscarPorCodigo(String codigo) {
        String sql = "SELECT id, codigo, fecha, subtotal, costo_envio, total, estado, observacion FROM venta WHERE codigo = ?";
        try (Connection connection = connectionFactory.crearConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, codigo);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapearVenta(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar venta por código en PostgreSQL", e);
        }
    }

    @Override
    public void actualizarEstado(Long ventaId, String estado) {
        String sql = "UPDATE venta SET estado = ? WHERE id = ?";
        try (Connection connection = connectionFactory.crearConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, estado);
            statement.setLong(2, ventaId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar estado de venta en PostgreSQL", e);
        }
    }

    private Venta mapearVenta(ResultSet resultSet) throws SQLException {
        return new Venta(
                resultSet.getLong("id"),
                resultSet.getString("codigo"),
                resultSet.getTimestamp("fecha").toLocalDateTime(),
                resultSet.getBigDecimal("subtotal"),
                resultSet.getBigDecimal("costo_envio"),
                resultSet.getBigDecimal("total"),
                resultSet.getString("estado"),
                resultSet.getString("observacion")
        );
    }
}
