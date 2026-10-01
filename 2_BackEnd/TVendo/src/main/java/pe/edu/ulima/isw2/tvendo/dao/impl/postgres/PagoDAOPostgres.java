package pe.edu.ulima.isw2.tvendo.dao.impl.postgres;

import pe.edu.ulima.isw2.tvendo.dao.PagoDAO;
import pe.edu.ulima.isw2.tvendo.datasource.connection.ConnectionFactory;
import pe.edu.ulima.isw2.tvendo.entity.Pago;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la interfaz PagoDAO para el acceso a datos de pagos en PostgreSQL.
 * Esta clase utiliza JDBC para interactuar con una base de datos PostgreSQL y realizar operaciones CRUD
 * sobre la entidad Pago.
 * @author Henry Wong
 */
public class PagoDAOPostgres implements PagoDAO {

    private final ConnectionFactory connectionFactory;

    public PagoDAOPostgres(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Pago guardar(Pago pago) {
        try (Connection connection = connectionFactory.crearConexion()) {
            return guardar(connection, pago);
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar pago en PostgreSQL", e);
        }
    }

    @Override
    public Pago guardar(Connection connection, Pago pago) {
        String sql = "INSERT INTO pago (venta_id, tipo, monto, estado, codigo_externo, fecha, observacion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, pago.getVentaId());
            statement.setString(2, pago.getTipo());
            statement.setBigDecimal(3, pago.getMonto());
            statement.setString(4, pago.getEstado());
            statement.setString(5, pago.getCodigoExterno());
            statement.setTimestamp(6, Timestamp.valueOf(pago.getFecha()));
            statement.setString(7, pago.getObservacion());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pago.setId(generatedKeys.getLong(1));
                }
            }
            return pago;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar pago en PostgreSQL", e);
        }
    }

    @Override
    public List<Pago> listarPorVentaId(Long ventaId) {
        String sql = "SELECT id, venta_id, tipo, monto, estado, codigo_externo, fecha, observacion FROM pago WHERE venta_id = ? ORDER BY id";
        List<Pago> pagos = new ArrayList<>();
        try (Connection connection = connectionFactory.crearConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, ventaId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    pagos.add(mapearPago(resultSet));
                }
            }
            return pagos;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar pagos de la venta en PostgreSQL", e);
        }
    }

    private Pago mapearPago(ResultSet resultSet) throws SQLException {
        return new Pago(
                resultSet.getLong("id"),
                resultSet.getLong("venta_id"),
                resultSet.getString("tipo"),
                resultSet.getBigDecimal("monto"),
                resultSet.getString("estado"),
                resultSet.getString("codigo_externo"),
                resultSet.getTimestamp("fecha").toLocalDateTime(),
                resultSet.getString("observacion")
        );
    }
}
