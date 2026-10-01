package pe.edu.ulima.isw2.tvendo.dao;

import pe.edu.ulima.isw2.tvendo.entity.DetalleVenta;

import java.sql.Connection;
import java.util.List;

/**
 * Interfaz para el acceso a datos de los detalles de venta.
 * Implementa del patron DAO (Data Access Object) para separar la lógica de acceso a datos de la lógica de negocio.
 * Proporciona métodos para guardar y listar detalles de venta.
 * @author Henry Wong
 */
public interface DetalleVentaDAO {
    DetalleVenta guardar(DetalleVenta detalle);

    DetalleVenta guardar(Connection connection, DetalleVenta detalle);

    List<DetalleVenta> listarPorVentaId(Long ventaId);
}
