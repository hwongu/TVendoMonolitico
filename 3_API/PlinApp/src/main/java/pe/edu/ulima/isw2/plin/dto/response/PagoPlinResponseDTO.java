package pe.edu.ulima.isw2.plin.dto.response;

import java.time.LocalDateTime;

/**
 * DTO para la respuesta de un pago realizado mediante Plin.
 * Contiene la información del pago, incluyendo el código de operación, teléfono del cliente,
 * nombre del cliente, monto, moneda, estado del pago, descripción y fecha de creación.
 * @author Henry Wong
 */
public record PagoPlinResponseDTO(
        String codigoOperacion,
        String numeroDestino,
        String titular,
        Integer montoCentimos,
        String moneda,
        String estado,
        String detalle,
        LocalDateTime fechaCreacion
) {
}
