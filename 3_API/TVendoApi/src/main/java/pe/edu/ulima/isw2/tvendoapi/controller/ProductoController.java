package pe.edu.ulima.isw2.tvendoapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import pe.edu.ulima.isw2.tvendo.entity.Producto;
import pe.edu.ulima.isw2.tvendo.service.ProductoService;
import pe.edu.ulima.isw2.tvendoapi.dto.response.ProductoResponseDTO;
import pe.edu.ulima.isw2.tvendoapi.exception.GlobalExceptionHandler;
import pe.edu.ulima.isw2.tvendoapi.mapper.ProductoMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para manejar las solicitudes HTTP relacionadas con los productos.
 * <p>
 * Esta clase implementa la interfaz {@link HttpHandler} y se encarga de procesar las solicitudes
 * entrantes, delegando la lógica de negocio al servicio {@link ProductoService}.
 * </p>
 * Esto se simplifica usando Spring Boot, pero lo hacemos a mano para que vean como funciona por debajo.
 * @author Henry Wong
 */
public class ProductoController implements HttpHandler {

    private static final String BASE_PATH = "/api/productos";

    private final ProductoService productoService;
    private final ProductoMapper productoMapper;
    private final ObjectMapper objectMapper;

    public ProductoController(ProductoService productoService, ProductoMapper productoMapper, ObjectMapper objectMapper) {
        this.productoService = productoService;
        this.productoMapper = productoMapper;
        this.objectMapper = objectMapper;
    }

    /**
     * Maneja las solicitudes HTTP entrantes.
     *
     * @param exchange el objeto HttpExchange que representa la solicitud y respuesta HTTP
     */
    @Override
    public void handle(HttpExchange exchange) {
        try {
            String method = exchange.getRequestMethod();
            if ("OPTIONS".equalsIgnoreCase(method)) {
                responderOptions(exchange);
                return;
            }

            List<String> segmentos = extraerSegmentos(exchange.getRequestURI().getPath());
            if (segmentos == null) {
                GlobalExceptionHandler.writeError(exchange, 404, "Ruta no encontrada", objectMapper);
                return;
            }

            if (!"GET".equalsIgnoreCase(method)) {
                GlobalExceptionHandler.writeError(exchange, 405, "Método no permitido para esta ruta", objectMapper);
                return;
            }

            if (segmentos.isEmpty()) {
                listarProductos(exchange);
                return;
            }

            if (segmentos.size() == 1) {
                obtenerProductoPorCodigo(exchange, segmentos.getFirst());
                return;
            }

            GlobalExceptionHandler.writeError(exchange, 404, "Ruta no encontrada", objectMapper);
        } catch (Exception exception) {
            GlobalExceptionHandler.handle(exchange, exception, objectMapper);
        }
    }

    /**
     * Lista todos los productos disponibles.
     *
     * @param exchange el objeto HttpExchange que representa la solicitud y respuesta HTTP
     * @throws IOException si ocurre un error de entrada/salida
     */
    private void listarProductos(HttpExchange exchange) throws IOException {
        List<Producto> productos = productoService.listarProductos();
        List<ProductoResponseDTO> response = productoMapper.toResponseList(productos);
        escribirJson(exchange, 200, response);
    }

    /**
     * Obtiene un producto por su código.
     *
     * @param exchange el objeto HttpExchange que representa la solicitud y respuesta HTTP
     * @param codigo el código del producto a obtener
     * @throws IOException si ocurre un error de entrada/salida
     */
    private void obtenerProductoPorCodigo(HttpExchange exchange, String codigo) throws IOException {
        if (codigo.isBlank()) {
            GlobalExceptionHandler.writeError(exchange, 400, "El código del producto es obligatorio", objectMapper);
            return;
        }

        Optional<Producto> producto = productoService.buscarPorCodigo(codigo);
        if (producto.isEmpty()) {
            GlobalExceptionHandler.writeError(exchange, 404, "No existe producto con código: " + codigo, objectMapper);
            return;
        }

        escribirJson(exchange, 200, productoMapper.toResponse(producto.get()));
    }

    /**
     * Responde a las solicitudes OPTIONS.
     *
     * @param exchange el objeto HttpExchange que representa la solicitud y respuesta HTTP
     * @throws IOException si ocurre un error de entrada/salida
     */
    private void responderOptions(HttpExchange exchange) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        GlobalExceptionHandler.addCorsHeaders(headers);
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
    }

    /**
     * Escribe una respuesta JSON.
     *
     * @param exchange el objeto HttpExchange que representa la solicitud y respuesta HTTP
     * @param status el código de estado HTTP
     * @param body el cuerpo de la respuesta
     * @throws IOException si ocurre un error de entrada/salida
     */
    private void escribirJson(HttpExchange exchange, int status, Object body) throws IOException {
        byte[] responseBody = objectMapper.writeValueAsBytes(body);
        Headers headers = exchange.getResponseHeaders();
        GlobalExceptionHandler.addCorsHeaders(headers);
        headers.set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, responseBody.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(responseBody);
        }
    }

    /**
     * Extrae los segmentos de la ruta.
     *
     * @param path la ruta completa
     * @return una lista con los segmentos de la ruta, o null si la ruta no es válida
     */
    private List<String> extraerSegmentos(String path) {
        if (path == null || !path.startsWith(BASE_PATH)) {
            return null;
        }

        String sufijo = path.substring(BASE_PATH.length());
        if (sufijo.isBlank() || "/".equals(sufijo)) {
            return List.of();
        }

        if (sufijo.startsWith("/")) {
            sufijo = sufijo.substring(1);
        }
        if (sufijo.endsWith("/")) {
            sufijo = sufijo.substring(0, sufijo.length() - 1);
        }
        if (sufijo.isBlank()) {
            return List.of();
        }

        return Arrays.stream(sufijo.split("/"))
                .filter(segmento -> !segmento.isBlank())
                .map(segmento -> URLDecoder.decode(segmento, StandardCharsets.UTF_8))
                .toList();
    }
}
