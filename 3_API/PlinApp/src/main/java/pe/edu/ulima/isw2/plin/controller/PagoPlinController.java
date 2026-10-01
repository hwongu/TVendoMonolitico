package pe.edu.ulima.isw2.plin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import pe.edu.ulima.isw2.plin.dto.request.CrearPagoPlinRequestDTO;
import pe.edu.ulima.isw2.plin.dto.response.ErrorResponseDTO;
import pe.edu.ulima.isw2.plin.dto.response.PagoPlinResponseDTO;
import pe.edu.ulima.isw2.plin.exception.GlobalExceptionHandler;
import pe.edu.ulima.isw2.plin.service.PagoPlinService;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Controlador para manejar las solicitudes HTTP relacionadas con los pagos a través de Plin.
 * <p>
 * Esta clase implementa la interfaz {@link HttpHandler} y se encarga de procesar las solicitudes
 * entrantes, delegando la lógica de negocio al servicio {@link PagoPlinService}.
 * </p>
 * Esto se simplifica usando Spring Boot, pero lo hacemos a mano para que vean como funciona por debajo.
 * @author Henry Wong
 */
public class PagoPlinController implements HttpHandler {

    private static final String BASE_PATH = "/api/v1/plin/pagos";

    private final PagoPlinService pagoPlinService;
    private final ObjectMapper objectMapper;
    private final GlobalExceptionHandler globalExceptionHandler;

    public PagoPlinController(
            PagoPlinService pagoPlinService,
            ObjectMapper objectMapper,
            GlobalExceptionHandler globalExceptionHandler
    ) {
        if (pagoPlinService == null) {
            throw new IllegalArgumentException("El servicio es obligatorio");
        }
        if (objectMapper == null) {
            throw new IllegalArgumentException("El ObjectMapper es obligatorio");
        }
        if (globalExceptionHandler == null) {
            throw new IllegalArgumentException("El manejador de excepciones es obligatorio");
        }
        this.pagoPlinService = pagoPlinService;
        this.objectMapper = objectMapper;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String ruta = exchange.getRequestURI().getPath();

        try {
            String metodo = exchange.getRequestMethod();

            if ("POST".equalsIgnoreCase(metodo) && esRutaBase(ruta)) {
                crearPago(exchange);
                return;
            }

            if ("GET".equalsIgnoreCase(metodo) && esRutaBase(ruta)) {
                listarPagos(exchange);
                return;
            }

            if ("GET".equalsIgnoreCase(metodo) && esRutaBusqueda(ruta)) {
                buscarPago(exchange, ruta);
                return;
            }

            throw new IllegalArgumentException("Ruta o método no soportado");
        } catch (Exception exception) {
            manejarError(exchange, exception, ruta);
        } finally {
            exchange.close();
        }
    }

    private void crearPago(HttpExchange exchange) throws IOException {
        CrearPagoPlinRequestDTO request = leerRequest(exchange);
        PagoPlinResponseDTO response = pagoPlinService.procesar(request);
        escribirJson(exchange, 201, response);
    }

    private void listarPagos(HttpExchange exchange) throws IOException {
        List<PagoPlinResponseDTO> response = pagoPlinService.listar();
        escribirJson(exchange, 200, response);
    }

    private void buscarPago(HttpExchange exchange, String ruta) throws IOException {
        String codigoOperacion = extraerCodigoOperacion(ruta);
        PagoPlinResponseDTO response = pagoPlinService.buscar(codigoOperacion);
        escribirJson(exchange, 200, response);
    }

    private CrearPagoPlinRequestDTO leerRequest(HttpExchange exchange) throws IOException {
        String cuerpo = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (cuerpo.isBlank()) {
            throw new IllegalArgumentException("El cuerpo JSON es obligatorio");
        }

        try {
            return objectMapper.readValue(cuerpo, CrearPagoPlinRequestDTO.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("El JSON enviado no es válido");
        }
    }

    private void manejarError(HttpExchange exchange, Exception exception, String ruta) throws IOException {
        ErrorResponseDTO errorResponse = globalExceptionHandler.manejar(exception, ruta);
        escribirJson(exchange, errorResponse.estadoHttp(), errorResponse);
    }

    private void escribirJson(HttpExchange exchange, int statusCode, Object response) throws IOException {
        byte[] responseBytes = objectMapper.writeValueAsBytes(response);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        exchange.getResponseBody().write(responseBytes);
    }

    private boolean esRutaBase(String ruta) {
        return BASE_PATH.equals(ruta) || (BASE_PATH + "/").equals(ruta);
    }

    private boolean esRutaBusqueda(String ruta) {
        if (!ruta.startsWith(BASE_PATH + "/")) {
            return false;
        }

        String segmento = ruta.substring((BASE_PATH + "/").length());
        return !segmento.isBlank() && !segmento.contains("/");
    }

    private String extraerCodigoOperacion(String ruta) {
        String codigo = ruta.substring((BASE_PATH + "/").length());
        return URLDecoder.decode(codigo, StandardCharsets.UTF_8);
    }
}
