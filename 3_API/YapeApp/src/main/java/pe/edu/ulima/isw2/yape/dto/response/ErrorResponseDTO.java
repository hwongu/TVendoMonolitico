package pe.edu.ulima.isw2.yape.dto.response;

import java.time.LocalDateTime;

/**
 * DTO para la respuesta de error en la API.
 * Contiene información sobre el estado HTTP, el mensaje de error, la ruta de la solicitud y la fecha del error.
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
