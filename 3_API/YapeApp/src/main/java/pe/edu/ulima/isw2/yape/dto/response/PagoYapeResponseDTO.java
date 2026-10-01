package pe.edu.ulima.isw2.yape.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para la respuesta de un pago realizado mediante Yape.
 * Contiene la información del pago, incluyendo el código de operación, teléfono del cliente,
 * nombre del cliente, monto, moneda, estado del pago, descripción y fecha de creación.
 * @author Henry Wong
 */
public record PagoYapeResponseDTO(
        String codigoOperacion,
        String telefono,
        String nombreCliente,
        BigDecimal monto,
        String moneda,
        String estado,
        String descripcion,
        LocalDateTime fechaCreacion
) {
}
