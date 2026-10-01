package pe.edu.ulima.isw2.tvendo.dao;

import pe.edu.ulima.isw2.tvendo.entity.Venta;

import java.sql.Connection;
import java.util.Optional;

/**
 * Interfaz para el acceso a datos de ventas.
 * Implementa del patron DAO (Data Access Object) para separar la lógica de acceso a datos de la lógica de negocio.
 * Proporciona métodos para guardar, buscar y actualizar ventas.
 * @author Henry Wong
 */
public interface VentaDAO {
    Venta guardar(Venta venta);

    Venta guardar(Connection connection, Venta venta);

    Optional<Venta> buscarPorCodigo(String codigo);

    void actualizarEstado(Long ventaId, String estado);
}
