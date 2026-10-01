package pe.edu.ulima.isw2.tvendo.adapter.payment.yape;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para la respuesta de un pago realizado mediante Yape.
 * Contiene la información devuelta por el sistema de Yape después de procesar un pago,
 * incluyendo el código de operación, el teléfono del cliente, el nombre del cliente,
 * el monto pagado, la moneda utilizada, el estado del pago, una descripción y la fecha de creación.
 * @author Henry Wong
 */
public record YapeResponseDTO(
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
