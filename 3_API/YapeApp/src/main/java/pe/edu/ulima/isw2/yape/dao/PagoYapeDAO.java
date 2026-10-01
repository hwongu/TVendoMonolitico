package pe.edu.ulima.isw2.yape.dao;

import pe.edu.ulima.isw2.yape.entity.PagoYape;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el acceso a datos de pagos realizados mediante Yape.
 * Implementa del patron DAO (Data Access Object) para separar la lógica de acceso a datos de la lógica de negocio.
 * Proporciona métodos para guardar, buscar y listar pagos de Yape.
 * @author Henry Wong
 */
public interface PagoYapeDAO {

    PagoYape guardar(PagoYape pago);

    Optional<PagoYape> buscarPorCodigoOperacion(String codigoOperacion);

    List<PagoYape> listar();
}
