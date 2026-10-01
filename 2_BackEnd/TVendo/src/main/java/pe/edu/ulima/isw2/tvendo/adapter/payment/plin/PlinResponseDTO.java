package pe.edu.ulima.isw2.tvendo.adapter.payment.plin;

import java.time.LocalDateTime;

/**
 * DTO para la respuesta de un pago realizado mediante Plin.
 * Contiene la información relevante del pago, incluyendo el código de operación, número de destino,
 * titular, monto en céntimos, moneda, estado del pago, detalle y fecha de creación.
 * @author Henry Wong
 */
public record PlinResponseDTO(
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
