package pe.edu.ulima.isw2.yape.exception;

import pe.edu.ulima.isw2.yape.dto.response.ErrorResponseDTO;

import java.time.LocalDateTime;

/**
 * Clase que maneja las excepciones globales de la aplicación.
 * Proporciona métodos para manejar excepciones y generar respuestas de error adecuadas.
 * @author Henry Wong
 */
public class GlobalExceptionHandler {

    public ErrorResponseDTO manejar(Exception exception, String ruta) {
        int estadoHttp = obtenerEstadoHttp(exception);
        String error = obtenerError(estadoHttp);
        String mensaje = obtenerMensaje(exception, estadoHttp);
        return new ErrorResponseDTO(
                estadoHttp,
                error,
                mensaje,
                ruta,
                LocalDateTime.now()
        );
    }

    public int obtenerEstadoHttp(Exception exception) {
        if (exception instanceof PagoYapeNoEncontradoException) {
            return 404;
        }
        if (exception instanceof IllegalArgumentException) {
            return 400;
        }
        return 500;
    }

    private String obtenerError(int estadoHttp) {
        if (estadoHttp == 404) {
            return "Recurso no encontrado";
        }
        if (estadoHttp == 400) {
            return "Solicitud inválida";
        }
        return "Error interno del servidor";
    }

    private String obtenerMensaje(Exception exception, int estadoHttp) {
        if (estadoHttp == 500) {
            return "Ocurrió un error inesperado";
        }
        return exception.getMessage();
    }
}
