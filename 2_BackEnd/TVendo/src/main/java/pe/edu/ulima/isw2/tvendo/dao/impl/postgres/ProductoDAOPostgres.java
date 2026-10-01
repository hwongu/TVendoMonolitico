package pe.edu.ulima.isw2.tvendo.dao.impl.postgres;

import pe.edu.ulima.isw2.tvendo.dao.ProductoDAO;
import pe.edu.ulima.isw2.tvendo.datasource.connection.ConnectionFactory;
import pe.edu.ulima.isw2.tvendo.entity.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de la interfaz ProductoDAO para el acceso a datos de productos en una base de datos PostgreSQL.
 * Esta clase utiliza JDBC para interactuar con la base de datos y realizar operaciones CRUD sobre la entidad Producto.
 * @author Henry Wong
 */
public class ProductoDAOPostgres implements ProductoDAO {

    private final ConnectionFactory connectionFactory;

    public ProductoDAOPostgres(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Optional<Producto> buscarPorCodigo(String codigo) {
        String sql = "SELECT id, codigo, nombre, descripcion, precio, stock, activo FROM producto WHERE codigo = ?";
        try (Connection connection = connectionFactory.crearConexion();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, codigo);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapearProducto(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar producto por código en PostgreSQL", e);
        }
    }

    @Override
    public List<Producto> listar() {
        String sql = "SELECT id, codigo, nombre, descripcion, precio, stock, activo FROM producto ORDER BY id";
        List<Producto> productos = new ArrayList<>();
        try (Connection connection = connectionFactory.crearConexion();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                productos.add(mapearProducto(resultSet));
            }
            return productos;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar productos en PostgreSQL", e);
        }
    }

    @Override
    public void actualizarStock(Long productoId, Integer nuevoStock) {
        try (Connection connection = connectionFactory.crearConexion()) {
            actualizarStock(connection, productoId, nuevoStock);
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar stock en PostgreSQL", e);
        }
    }

    @Override
    public void actualizarStock(Connection connection, Long productoId, Integer nuevoStock) {
        String sql = "UPDATE producto SET stock = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, nuevoStock);
            statement.setLong(2, productoId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar stock en PostgreSQL", e);
        }
    }

    private Producto mapearProducto(ResultSet resultSet) throws SQLException {
        return new Producto(
                resultSet.getLong("id"),
                resultSet.getString("codigo"),
                resultSet.getString("nombre"),
                resultSet.getString("descripcion"),
                resultSet.getBigDecimal("precio"),
                resultSet.getInt("stock"),
                resultSet.getBoolean("activo")
        );
    }
}
