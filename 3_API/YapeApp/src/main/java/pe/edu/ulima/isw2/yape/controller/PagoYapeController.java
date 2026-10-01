package pe.edu.ulima.isw2.yape.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import pe.edu.ulima.isw2.yape.dto.request.CrearPagoYapeRequestDTO;
import pe.edu.ulima.isw2.yape.dto.response.ErrorResponseDTO;
import pe.edu.ulima.isw2.yape.dto.response.PagoYapeResponseDTO;
import pe.edu.ulima.isw2.yape.exception.GlobalExceptionHandler;
import pe.edu.ulima.isw2.yape.service.PagoYapeService;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Controlador para manejar las solicitudes HTTP relacionadas con los pagos a través de Yape.
 * <p>
 * Esta clase implementa la interfaz {@link HttpHandler} y se encarga de procesar las solicitudes
 * entrantes, delegando la lógica de negocio al servicio {@link PagoYapeService}.
 * </p>
 * Esto se simplifica usando Spring Boot, pero lo hacemos a mano para que vean como funciona por debajo.
 * @author Henry Wong
 */
public class PagoYapeController implements HttpHandler {

    private static final String RUTA_BASE = "/api/v1/yape/pagos";

    private final PagoYapeService pagoYapeService;
    private final ObjectMapper objectMapper;
    private final GlobalExceptionHandler globalExceptionHandler;

    public PagoYapeController(
            PagoYapeService pagoYapeService,
            ObjectMapper objectMapper,
            GlobalExceptionHandler globalExceptionHandler
    ) {
        this.pagoYapeService = Objects.requireNonNull(pagoYapeService, "PagoYapeService es obligatorio");
        this.objectMapper = Objects.requireNonNull(objectMapper, "ObjectMapper es obligatorio");
        this.globalExceptionHandler = Objects.requireNonNull(globalExceptionHandler, "GlobalExceptionHandler es obligatorio");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String ruta = exchange.getRequestURI().getPath();
        try {
            if (esRutaColeccion(ruta)) {
                manejarColeccion(exchange);
                return;
            }
            if (esRutaItem(ruta)) {
                manejarItem(exchange, ruta);
                return;
            }
            responder(
                    exchange,
                    404,
                    new ErrorResponseDTO(
                            404,
                            "Recurso no encontrado",
                            "La ruta solicitada no existe",
                            ruta,
                            LocalDateTime.now()
                    )
            );
        } catch (Exception exception) {
            ErrorResponseDTO error = globalExceptionHandler.manejar(exception, ruta);
            int estadoHttp = globalExceptionHandler.obtenerEstadoHttp(exception);
            responder(exchange, estadoHttp, error);
        }
    }

    private void manejarColeccion(HttpExchange exchange) throws IOException {
        String metodo = exchange.getRequestMethod();
        if ("POST".equalsIgnoreCase(metodo)) {
            CrearPagoYapeRequestDTO requestDTO = leerRequest(exchange);
            PagoYapeResponseDTO responseDTO = pagoYapeService.procesar(requestDTO);
            responder(exchange, 201, responseDTO);
            return;
        }
        if ("GET".equalsIgnoreCase(metodo)) {
            List<PagoYapeResponseDTO> responseDTOS = pagoYapeService.listar();
            responder(exchange, 200, responseDTOS);
            return;
        }
        responderMetodoNoPermitido(exchange);
    }

    private void manejarItem(HttpExchange exchange, String ruta) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            responderMetodoNoPermitido(exchange);
            return;
        }
        String codigoOperacion = ruta.substring((RUTA_BASE + "/").length());
        if (codigoOperacion.isBlank() || codigoOperacion.contains("/")) {
            throw new IllegalArgumentException("El código de operación es inválido");
        }
        PagoYapeResponseDTO responseDTO = pagoYapeService.buscar(codigoOperacion);
        responder(exchange, 200, responseDTO);
    }

    private CrearPagoYapeRequestDTO leerRequest(HttpExchange exchange) {
        try {
            return objectMapper.readValue(exchange.getRequestBody(), CrearPagoYapeRequestDTO.class);
        } catch (IOException exception) {
            throw new IllegalArgumentException("El cuerpo de la solicitud no contiene un JSON válido");
        }
    }

    private boolean esRutaColeccion(String ruta) {
        return RUTA_BASE.equals(ruta) || (RUTA_BASE + "/").equals(ruta);
    }

    private boolean esRutaItem(String ruta) {
        return ruta.startsWith(RUTA_BASE + "/");
    }

    private void responderMetodoNoPermitido(HttpExchange exchange) throws IOException {
        responder(
                exchange,
                405,
                new ErrorResponseDTO(
                        405,
                        "Método no permitido",
                        "Método HTTP no permitido para la ruta solicitada",
                        exchange.getRequestURI().getPath(),
                        LocalDateTime.now()
                )
        );
    }

    private void responder(HttpExchange exchange, int statusCode, Object body) throws IOException {
        byte[] response = objectMapper.writeValueAsBytes(body);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(response);
        }
    }
}
