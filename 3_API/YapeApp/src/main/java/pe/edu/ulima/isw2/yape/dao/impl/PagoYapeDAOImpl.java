package pe.edu.ulima.isw2.yape.dao.impl;

import pe.edu.ulima.isw2.yape.connection.ConnectionFactory;
import pe.edu.ulima.isw2.yape.dao.PagoYapeDAO;
import pe.edu.ulima.isw2.yape.entity.PagoYape;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementación de la interfaz PagoYapeDAO para el acceso a datos de pagos realizados mediante Yape.
 * Esta clase utiliza JDBC para interactuar con una base de datos relacional y realizar operaciones CRUD
 * sobre la entidad PagoYape.
 * @author Henry Wong
 */
public class PagoYapeDAOImpl implements PagoYapeDAO {

    private final ConnectionFactory connectionFactory;

    public PagoYapeDAOImpl(ConnectionFactory connectionFactory) {
        this.connectionFactory = Objects.requireNonNull(connectionFactory, "ConnectionFactory es obligatorio");
    }

    @Override
    public PagoYape guardar(PagoYape pago) {
        String sql = """
                INSERT INTO pago_yape (
                    codigo_operacion,
                    telefono,
                    nombre_cliente,
                    monto,
                    moneda,
                    estado,
                    descripcion,
                    fecha_creacion
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING
                    id,
                    codigo_operacion,
                    telefono,
                    nombre_cliente,
                    monto,
                    moneda,
                    estado,
                    descripcion,
                    fecha_creacion
                """;

        try (Connection connection = connectionFactory.crearConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, pago.getCodigoOperacion());
            statement.setString(2, pago.getTelefono());
            statement.setString(3, pago.getNombreCliente());
            statement.setBigDecimal(4, pago.getMonto());
            statement.setString(5, pago.getMoneda());
            statement.setString(6, pago.getEstado());
            statement.setString(7, pago.getDescripcion());
            statement.setTimestamp(8, Timestamp.valueOf(pago.getFechaCreacion()));

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
            throw new RuntimeException("No se pudo guardar el pago Yape");
        } catch (SQLException exception) {
            throw new RuntimeException("Error al guardar pago Yape", exception);
        }
    }

    @Override
    public Optional<PagoYape> buscarPorCodigoOperacion(String codigoOperacion) {
        String sql = """
                SELECT
                    id,
                    codigo_operacion,
                    telefono,
                    nombre_cliente,
                    monto,
                    moneda,
                    estado,
                    descripcion,
                    fecha_creacion
                FROM pago_yape
                WHERE codigo_operacion = ?
                """;

        try (Connection connection = connectionFactory.crearConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, codigoOperacion);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException exception) {
            throw new RuntimeException("Error al buscar pago Yape por código", exception);
        }
    }

    @Override
    public List<PagoYape> listar() {
        String sql = """
                SELECT
                    id,
                    codigo_operacion,
                    telefono,
                    nombre_cliente,
                    monto,
                    moneda,
                    estado,
                    descripcion,
                    fecha_creacion
                FROM pago_yape
                ORDER BY id
                """;

        List<PagoYape> resultados = new ArrayList<>();

        try (Connection connection = connectionFactory.crearConexion();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                resultados.add(mapear(rs));
            }
            return resultados;
        } catch (SQLException exception) {
            throw new RuntimeException("Error al listar pagos Yape", exception);
        }
    }

    private PagoYape mapear(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("fecha_creacion");
        return new PagoYape(
                rs.getLong("id"),
                rs.getString("codigo_operacion"),
                rs.getString("telefono"),
                rs.getString("nombre_cliente"),
                rs.getBigDecimal("monto"),
                rs.getString("moneda"),
                rs.getString("estado"),
                rs.getString("descripcion"),
                timestamp != null ? timestamp.toLocalDateTime() : null
        );
    }
}
