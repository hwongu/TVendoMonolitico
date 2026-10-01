package pe.edu.ulima.isw2.yape.dto.request;

import java.math.BigDecimal;

/**
 * DTO para la creación de un pago mediante Yape.
 * Contiene la información necesaria para procesar un pago, incluyendo el teléfono del cliente,
 * el nombre del cliente, el monto a pagar y una descripción del pago.
 * @author Henry Wong
 */
public record CrearPagoYapeRequestDTO(
        String telefono,
        String nombreCliente,
        BigDecimal monto,
        String descripcion
) {
}
