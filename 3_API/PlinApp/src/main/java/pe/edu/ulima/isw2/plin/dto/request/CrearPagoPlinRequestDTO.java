package pe.edu.ulima.isw2.plin.dto.request;

/**
 * DTO para la creación de un pago mediante Plin.
 * Contiene la información necesaria para procesar un pago, incluyendo el teléfono del cliente,
 * el nombre del cliente, el monto a pagar y una descripción del pago.
 * @author Henry Wong
 */
public record CrearPagoPlinRequestDTO(
        String numeroDestino,
        String titular,
        Integer montoCentimos,
        String detalle
) {
}
