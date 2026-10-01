package pe.edu.ulima.isw2.yape.mapper;

import pe.edu.ulima.isw2.yape.dto.response.PagoYapeResponseDTO;
import pe.edu.ulima.isw2.yape.entity.PagoYape;

/**
 * Mapper para convertir entidades de PagoYape a DTOs de respuesta.
 * Facilita la transformación de datos entre la capa de persistencia y la capa de presentación.
 * @author Henry Wong
 */
public class PagoYapeMapper {

    public PagoYapeResponseDTO toResponseDTO(PagoYape pago) {
        return new PagoYapeResponseDTO(
                pago.getCodigoOperacion(),
                pago.getTelefono(),
                pago.getNombreCliente(),
                pago.getMonto(),
                pago.getMoneda(),
                pago.getEstado(),
                pago.getDescripcion(),
                pago.getFechaCreacion()
        );
    }
}
