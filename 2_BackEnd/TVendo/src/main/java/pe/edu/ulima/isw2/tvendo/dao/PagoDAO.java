package pe.edu.ulima.isw2.tvendo.dao;

import pe.edu.ulima.isw2.tvendo.entity.Pago;

import java.sql.Connection;
import java.util.List;

/**
 * Interfaz para el acceso a datos de pagos.
 * Implementa del patron DAO (Data Access Object) para separar la lógica de acceso a datos de la lógica de negocio.
 * Proporciona métodos para guardar y listar pagos.
 * @author Henry Wong
 */
public interface PagoDAO {
    Pago guardar(Pago pago);

    Pago guardar(Connection connection, Pago pago);

    List<Pago> listarPorVentaId(Long ventaId);
}
