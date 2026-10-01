package pe.edu.ulima.isw2.tvendo.dao.factory.impl;

import pe.edu.ulima.isw2.tvendo.dao.DetalleVentaDAO;
import pe.edu.ulima.isw2.tvendo.dao.PagoDAO;
import pe.edu.ulima.isw2.tvendo.dao.ProductoDAO;
import pe.edu.ulima.isw2.tvendo.dao.VentaDAO;
import pe.edu.ulima.isw2.tvendo.dao.factory.DAOFactory;
import pe.edu.ulima.isw2.tvendo.dao.impl.mysql.DetalleVentaDAOMySQL;
import pe.edu.ulima.isw2.tvendo.dao.impl.mysql.PagoDAOMySQL;
import pe.edu.ulima.isw2.tvendo.dao.impl.mysql.ProductoDAOMySQL;
import pe.edu.ulima.isw2.tvendo.dao.impl.mysql.VentaDAOMySQL;
import pe.edu.ulima.isw2.tvendo.datasource.connection.ConnectionFactory;

public class MySQLDAOFactory implements DAOFactory {

    private final ConnectionFactory connectionFactory;

    public MySQLDAOFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public ProductoDAO crearProductoDAO() {
        return new ProductoDAOMySQL(connectionFactory);
    }

    @Override
    public VentaDAO crearVentaDAO() {
        return new VentaDAOMySQL(connectionFactory);
    }

    @Override
    public DetalleVentaDAO crearDetalleVentaDAO() {
        return new DetalleVentaDAOMySQL(connectionFactory);
    }

    @Override
    public PagoDAO crearPagoDAO() {
        return new PagoDAOMySQL(connectionFactory);
    }
}
