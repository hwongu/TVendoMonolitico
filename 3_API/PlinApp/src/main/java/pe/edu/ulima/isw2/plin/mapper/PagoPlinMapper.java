package pe.edu.ulima.isw2.plin.mapper;

import pe.edu.ulima.isw2.plin.dto.response.PagoPlinResponseDTO;
import pe.edu.ulima.isw2.plin.entity.PagoPlin;

/**
 * Mapper para convertir entidades de PagoPlin a DTOs de respuesta.
 * Facilita la transformación de datos entre la capa de persistencia y la capa de presentación.
 * @author Henry Wong
 */
public class PagoPlinMapper {

    public PagoPlinResponseDTO toResponseDTO(PagoPlin pago) {
        if (pago == null) {
            throw new IllegalArgumentException("El pago es obligatorio");
        }

        return new PagoPlinResponseDTO(
                pago.getCodigoOperacion(),
                pago.getNumeroDestino(),
                pago.getTitular(),
                pago.getMontoCentimos(),
                pago.getMoneda(),
                pago.getEstado(),
                pago.getDetalle(),
                pago.getFechaCreacion()
        );
    }
}
