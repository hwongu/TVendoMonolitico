package pe.edu.ulima.isw2.plin.dao;

import pe.edu.ulima.isw2.plin.entity.PagoPlin;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el acceso a datos de pagos realizados mediante Plin.
 * Implementa del patron DAO (Data Access Object) para separar la lógica de acceso a datos de la lógica de negocio.
 * Proporciona métodos para guardar, buscar y listar pagos de Plin.
 * @author Henry Wong
 */
public interface PagoPlinDAO {

    PagoPlin guardar(PagoPlin pago);

    Optional<PagoPlin> buscarPorCodigoOperacion(String codigoOperacion);

    List<PagoPlin> listar();
}
