package pe.edu.ulima.isw2.tvendoapi.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import pe.edu.ulima.isw2.tvendoapi.dto.response.ErrorResponseDTO;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.NoSuchElementException;

/**
 * Clase que maneja las excepciones globales en la aplicación.
 * Proporciona métodos para manejar excepciones y escribir respuestas de error en formato JSON.
 * Esto se simplifica usando Spring Boot, pero lo hacemos a mano para que vean como funciona por debajo.
 * @author Henry Wong
 */
public final class GlobalExceptionHandler {

    private GlobalExceptionHandler() {
    }

    public static void handle(HttpExchange exchange, Exception exception, ObjectMapper objectMapper) {
        int estado = resolverEstado(exception);
        writeError(exchange, estado, exception.getMessage(), objectMapper);
    }

    public static void writeError(HttpExchange exchange, int estadoHttp, String mensaje, ObjectMapper objectMapper) {
        String ruta = exchange.getRequestURI() == null ? "" : exchange.getRequestURI().getPath();
        String textoMensaje = (mensaje == null || mensaje.isBlank()) ? "Error inesperado" : mensaje;
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                estadoHttp,
                resolverTituloError(estadoHttp),
                textoMensaje,
                ruta,
                LocalDateTime.now()
        );

        try {
            byte[] body = objectMapper.writeValueAsBytes(errorResponseDTO);
            Headers headers = exchange.getResponseHeaders();
            addCorsHeaders(headers);
            headers.set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(estadoHttp, body.length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(body);
            }
        } catch (IOException ioException) {
            responderFallback(exchange, estadoHttp);
        }
    }

    public static void addCorsHeaders(Headers headers) {
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        headers.set("Access-Control-Allow-Headers", "Content-Type");
    }

    private static int resolverEstado(Exception exception) {
        if (exception instanceof NoSuchElementException) {
            return 404;
        }
        if (exception instanceof IllegalArgumentException illegalArgumentException) {
            String mensaje = illegalArgumentException.getMessage();
            if (mensaje != null && mensaje.toLowerCase(Locale.ROOT).contains("no existe")) {
                return 404;
            }
            return 400;
        }
        if (exception instanceof IllegalStateException) {
            return 409;
        }
        if (exception instanceof RuntimeException) {
            return 500;
        }
        return 500;
    }

    private static String resolverTituloError(int estadoHttp) {
        return switch (estadoHttp) {
            case 400 -> "Solicitud inválida";
            case 404 -> "No encontrado";
            case 405 -> "Método no permitido";
            case 409 -> "Conflicto";
            default -> "Error interno del servidor";
        };
    }

    private static void responderFallback(HttpExchange exchange, int estadoHttp) {
        try {
            String fallbackBody = "{\"estadoHttp\":" + estadoHttp
                    + ",\"error\":\"" + resolverTituloError(estadoHttp)
                    + "\",\"mensaje\":\"No se pudo serializar la respuesta de error\",\"ruta\":\"\",\"fecha\":\"\"}";
            byte[] body = fallbackBody.getBytes(StandardCharsets.UTF_8);
            Headers headers = exchange.getResponseHeaders();
            addCorsHeaders(headers);
            headers.set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(estadoHttp, body.length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(body);
            }
        } catch (IOException ignored) {
        }
    }
}
