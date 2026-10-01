package pe.edu.ulima.isw2.plin.exception;

import pe.edu.ulima.isw2.plin.dto.response.ErrorResponseDTO;

import java.time.LocalDateTime;

/**
 * Clase que maneja las excepciones globales de la aplicación.
 * Proporciona métodos para manejar excepciones y generar respuestas de error adecuadas.
 * @author Henry Wong
 */
public class GlobalExceptionHandler {

    public ErrorResponseDTO manejar(Exception exception, String ruta) {
        int estadoHttp = obtenerStatusHttp(exception);
        String error = obtenerError(estadoHttp);
        String mensaje = exception.getMessage() != null
                ? exception.getMessage()
                : "Ocurrió un error inesperado";

        return new ErrorResponseDTO(
                estadoHttp,
                error,
                mensaje,
                ruta,
                LocalDateTime.now()
        );
    }

    public int obtenerStatusHttp(Exception exception) {
        if (exception instanceof PagoPlinNoEncontradoException) {
            return 404;
        }
        if (exception instanceof IllegalArgumentException) {
            return 400;
        }
        return 500;
    }

    private String obtenerError(int estadoHttp) {
        return switch (estadoHttp) {
            case 404 -> "No encontrado";
            case 400 -> "Solicitud inválida";
            default -> "Error interno del servidor";
        };
    }
}
