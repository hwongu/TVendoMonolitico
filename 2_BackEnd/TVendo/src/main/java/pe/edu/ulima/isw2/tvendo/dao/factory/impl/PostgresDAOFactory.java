package pe.edu.ulima.isw2.tvendo.dao.factory.impl;

import pe.edu.ulima.isw2.tvendo.dao.DetalleVentaDAO;
import pe.edu.ulima.isw2.tvendo.dao.PagoDAO;
import pe.edu.ulima.isw2.tvendo.dao.ProductoDAO;
import pe.edu.ulima.isw2.tvendo.dao.VentaDAO;
import pe.edu.ulima.isw2.tvendo.dao.factory.DAOFactory;
import pe.edu.ulima.isw2.tvendo.dao.impl.postgres.DetalleVentaDAOPostgres;
import pe.edu.ulima.isw2.tvendo.dao.impl.postgres.PagoDAOPostgres;
import pe.edu.ulima.isw2.tvendo.dao.impl.postgres.ProductoDAOPostgres;
import pe.edu.ulima.isw2.tvendo.dao.impl.postgres.VentaDAOPostgres;
import pe.edu.ulima.isw2.tvendo.datasource.connection.ConnectionFactory;

public class PostgresDAOFactory implements DAOFactory {

    private final ConnectionFactory connectionFactory;

    public PostgresDAOFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public ProductoDAO crearProductoDAO() {
        return new ProductoDAOPostgres(connectionFactory);
    }

    @Override
    public VentaDAO crearVentaDAO() {
        return new VentaDAOPostgres(connectionFactory);
    }

    @Override
    public DetalleVentaDAO crearDetalleVentaDAO() {
        return new DetalleVentaDAOPostgres(connectionFactory);
    }

    @Override
    public PagoDAO crearPagoDAO() {
        return new PagoDAOPostgres(connectionFactory);
    }
}
