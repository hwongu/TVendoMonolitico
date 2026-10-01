package pe.edu.ulima.isw2.tvendo.adapter.payment.plin;

/**
 * DTO para la solicitud de un pago mediante Plin.
 * Contiene la información necesaria para procesar un pago, incluyendo el teléfono del cliente,
 * el nombre del cliente, el monto a pagar y una descripción del pago.
 * @author Henry Wong
 */
public record PlinRequestDTO(
        String numeroDestino,
        String titular,
        Integer montoCentimos,
        String detalle
) {
}
