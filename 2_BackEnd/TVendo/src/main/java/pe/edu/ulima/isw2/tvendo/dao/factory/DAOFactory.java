package pe.edu.ulima.isw2.tvendo.dao.factory;

import pe.edu.ulima.isw2.tvendo.dao.DetalleVentaDAO;
import pe.edu.ulima.isw2.tvendo.dao.PagoDAO;
import pe.edu.ulima.isw2.tvendo.dao.ProductoDAO;
import pe.edu.ulima.isw2.tvendo.dao.VentaDAO;

public interface DAOFactory {
    ProductoDAO crearProductoDAO();

    VentaDAO crearVentaDAO();

    DetalleVentaDAO crearDetalleVentaDAO();

    PagoDAO crearPagoDAO();
}
