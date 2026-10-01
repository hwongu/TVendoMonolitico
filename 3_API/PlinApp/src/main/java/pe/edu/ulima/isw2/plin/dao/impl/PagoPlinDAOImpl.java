package pe.edu.ulima.isw2.plin.dao.impl;

import pe.edu.ulima.isw2.plin.connection.ConnectionFactory;
import pe.edu.ulima.isw2.plin.dao.PagoPlinDAO;
import pe.edu.ulima.isw2.plin.entity.PagoPlin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de la interfaz PagoPlinDAO para el acceso a datos de pagos realizados mediante Plin.
 * Esta clase utiliza JDBC para interactuar con una base de datos relacional y realizar operaciones CRUD
 * sobre la entidad PagoPlin.
 * @author Henry Wong
 */
public class PagoPlinDAOImpl implements PagoPlinDAO {

    private final ConnectionFactory connectionFactory;

    public PagoPlinDAOImpl(ConnectionFactory connectionFactory) {
        if (connectionFactory == null) {
            throw new IllegalArgumentException("La fábrica de conexiones es obligatoria");
        }
        this.connectionFactory = connectionFactory;
    }

    @Override
    public PagoPlin guardar(PagoPlin pago) {
        String sql = """
                INSERT INTO pago_plin (
                    codigo_operacion,
                    numero_destino,
                    titular,
                    monto_centimos,
                    moneda,
                    estado,
                    detalle,
                    fecha_creacion
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING
                    id,
                    codigo_operacion,
                    numero_destino,
                    titular,
                    monto_centimos,
                    moneda,
                    estado,
                    detalle,
                    fecha_creacion
                """;

        try (Connection connection = connectionFactory.crearConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, pago.getCodigoOperacion());
            statement.setString(2, pago.getNumeroDestino());
            statement.setString(3, pago.getTitular());
            statement.setInt(4, pago.getMontoCentimos());
            statement.setString(5, pago.getMoneda());
            statement.setString(6, pago.getEstado());
            statement.setString(7, pago.getDetalle());
            statement.setTimestamp(8, Timestamp.valueOf(pago.getFechaCreacion()));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapear(resultSet);
                }
            }

            throw new RuntimeException("No se pudo guardar la operación Plin");
        } catch (SQLException exception) {
            throw new RuntimeException("Error al guardar la operación Plin", exception);
        }
    }

    @Override
    public Optional<PagoPlin> buscarPorCodigoOperacion(String codigoOperacion) {
        String sql = """
                SELECT
                    id,
                    codigo_operacion,
                    numero_destino,
                    titular,
                    monto_centimos,
                    moneda,
                    estado,
                    detalle,
                    fecha_creacion
                FROM pago_plin
                WHERE codigo_operacion = ?
                """;

        try (Connection connection = connectionFactory.crearConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, codigoOperacion);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapear(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Error al buscar la operación Plin", exception);
        }
    }

    @Override
    public List<PagoPlin> listar() {
        String sql = """
                SELECT
                    id,
                    codigo_operacion,
                    numero_destino,
                    titular,
                    monto_centimos,
                    moneda,
                    estado,
                    detalle,
                    fecha_creacion
                FROM pago_plin
                ORDER BY id
                """;

        List<PagoPlin> pagos = new ArrayList<>();

        try (Connection connection = connectionFactory.crearConexion();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                pagos.add(mapear(resultSet));
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Error al listar las operaciones Plin", exception);
        }

        return pagos;
    }

    private PagoPlin mapear(ResultSet rs) throws SQLException {
        Timestamp fecha = rs.getTimestamp("fecha_creacion");
        LocalDateTime fechaCreacion = fecha != null ? fecha.toLocalDateTime() : null;

        return new PagoPlin(
                rs.getLong("id"),
                rs.getString("codigo_operacion"),
                rs.getString("numero_destino"),
                rs.getString("titular"),
                rs.getInt("monto_centimos"),
                rs.getString("moneda"),
                rs.getString("estado"),
                rs.getString("detalle"),
                fechaCreacion
        );
    }
}
