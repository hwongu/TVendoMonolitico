package pe.edu.ulima.isw2.tvendoapi.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para representar la respuesta de un pago.
 * Contiene información relevante sobre el pago, como tipo, monto, estado, código externo, fecha y observación.
 * Este DTO se utiliza para transferir datos entre capas de la aplicación, especialmente en respuestas de API.
 * @author Henry Wong
 */
public record PagoResponseDTO(
        String tipo,
        BigDecimal monto,
        String estado,
        String codigoExterno,
        LocalDateTime fecha,
        String observacion
) {
}
