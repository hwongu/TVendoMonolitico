package pe.edu.ulima.isw2.tvendoapi.dto.response;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para representar la respuesta de error en la API.
 * Contiene información sobre el estado HTTP, el mensaje de error, la ruta solicitada y la fecha del error.
 * Se utiliza para estandarizar las respuestas de error enviadas al cliente.
 * @author Henry Wong
 */
public record ErrorResponseDTO(
        int estadoHttp,
        String error,
        String mensaje,
        String ruta,
        LocalDateTime fecha
) {
}
