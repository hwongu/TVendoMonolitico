package pe.edu.ulima.isw2.tvendo.dao;

import pe.edu.ulima.isw2.tvendo.entity.Producto;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el acceso a datos de productos.
 * Implementa del patron DAO (Data Access Object) para separar la lógica de acceso a datos de la lógica de negocio.
 * Proporciona métodos para buscar, listar y actualizar el stock de productos.
 * @author Henry Wong
 */
public interface ProductoDAO {
    Optional<Producto> buscarPorCodigo(String codigo);

    List<Producto> listar();

    void actualizarStock(Long productoId, Integer nuevoStock);

    void actualizarStock(Connection connection, Long productoId, Integer nuevoStock);
}
