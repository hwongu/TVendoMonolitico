package pe.edu.ulima.isw2.tvendoapi.app;

import pe.edu.ulima.isw2.tvendo.dao.factory.DAOFactory;
import pe.edu.ulima.isw2.tvendo.datasource.connection.ConnectionFactory;

public record InfraestructuraPersistencia(
        ConnectionFactory connectionFactory,
        DAOFactory daoFactory
) {
}
