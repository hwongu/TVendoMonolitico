package pe.edu.ulima.isw2.tvendoapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import pe.edu.ulima.isw2.tvendo.entity.DetalleVenta;
import pe.edu.ulima.isw2.tvendo.entity.Pago;
import pe.edu.ulima.isw2.tvendo.entity.Venta;
import pe.edu.ulima.isw2.tvendo.facade.VentaFacade;
import pe.edu.ulima.isw2.tvendo.invoker.PanelCaja;
import pe.edu.ulima.isw2.tvendo.pattern.command.Command;
import pe.edu.ulima.isw2.tvendo.pattern.command.impl.AnularVentaCommand;
import pe.edu.ulima.isw2.tvendo.pattern.command.impl.RegistrarVentaCommand;
import pe.edu.ulima.isw2.tvendo.pattern.strategy.EstrategiaEnvio;
import pe.edu.ulima.isw2.tvendo.pattern.strategy.impl.EnvioEstandar;
import pe.edu.ulima.isw2.tvendo.pattern.strategy.impl.EnvioExpress;
import pe.edu.ulima.isw2.tvendo.pattern.strategy.impl.EnvioProgramado;
import pe.edu.ulima.isw2.tvendo.service.PagoService;
import pe.edu.ulima.isw2.tvendo.service.VentaService;
import pe.edu.ulima.isw2.tvendoapi.dto.request.RegistrarVentaRequestDTO;
import pe.edu.ulima.isw2.tvendoapi.dto.response.ComprobanteResponseDTO;
import pe.edu.ulima.isw2.tvendoapi.dto.response.VentaResponseDTO;
import pe.edu.ulima.isw2.tvendoapi.exception.GlobalExceptionHandler;
import pe.edu.ulima.isw2.tvendoapi.mapper.VentaMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Controlador para manejar las solicitudes HTTP relacionadas con las ventas.
 * <p>
 * Esta clase implementa la interfaz {@link HttpHandler} y se encarga de procesar las solicitudes
 * entrantes, delegando la lógica de negocio al servicio {@link VentaService} y al {@link VentaFacade}.
 * </p>
 * Esto se simplifica usando Spring Boot, pero lo hacemos a mano para que vean como funciona por debajo.
 * @author Henry Wong
 */
public class VentaController implements HttpHandler {

    private static final String BASE_PATH = "/api/ventas";

    private final VentaFacade ventaFacade;
    private final VentaService ventaService;
    private final PagoService pagoService;
    private final PanelCaja panelCaja;
    private final VentaMapper ventaMapper;
    private final ObjectMapper objectMapper;

    public VentaController(VentaFacade ventaFacade, VentaService ventaService, PagoService pagoService, PanelCaja panelCaja, VentaMapper ventaMapper, ObjectMapper objectMapper) {
        this.ventaFacade = ventaFacade;
        this.ventaService = ventaService;
        this.pagoService = pagoService;
        this.panelCaja = panelCaja;
        this.ventaMapper = ventaMapper;
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

            if (segmentos.isEmpty()) {
                if ("POST".equalsIgnoreCase(method)) {
                    registrarVenta(exchange);
                    return;
                }
                GlobalExceptionHandler.writeError(exchange, 405, "Método no permitido para esta ruta", objectMapper);
                return;
            }

            if (segmentos.size() == 1) {
                if ("GET".equalsIgnoreCase(method)) {
                    consultarVenta(exchange, segmentos.getFirst());
                    return;
                }
                GlobalExceptionHandler.writeError(exchange, 405, "Método no permitido para esta ruta", objectMapper);
                return;
            }

            if (segmentos.size() == 2 && "anular".equalsIgnoreCase(segmentos.get(1))) {
                if ("PUT".equalsIgnoreCase(method)) {
                    anularVenta(exchange, segmentos.getFirst());
                    return;
                }
                GlobalExceptionHandler.writeError(exchange, 405, "Método no permitido para esta ruta", objectMapper);
                return;
            }

            if (segmentos.size() == 2 && "comprobante".equalsIgnoreCase(segmentos.get(1))) {
                if ("GET".equalsIgnoreCase(method)) {
                    obtenerComprobante(exchange, segmentos.getFirst());
                    return;
                }
                GlobalExceptionHandler.writeError(exchange, 405, "Método no permitido para esta ruta", objectMapper);
                return;
            }

            GlobalExceptionHandler.writeError(exchange, 404, "Ruta no encontrada", objectMapper);
        } catch (Exception exception) {
            GlobalExceptionHandler.handle(exchange, exception, objectMapper);
        }
    }

    /**
     * Registra una nueva venta.
     *
     * @param exchange el objeto HttpExchange que representa la solicitud y respuesta HTTP
     * @throws IOException si ocurre un error de entrada/salida al leer la solicitud o escribir la respuesta
     */
    private void registrarVenta(HttpExchange exchange) throws IOException {
        RegistrarVentaRequestDTO request = leerRequestRegistrarVenta(exchange);
        validarRequestRegistrarVenta(request);

        EstrategiaEnvio estrategiaEnvio = resolverEstrategiaEnvio(request.tipoEnvio());

        Command command = new RegistrarVentaCommand(
                ventaFacade,
                request.codigoVenta(),
                request.productos(),
                estrategiaEnvio,
                request.tipoPago(),
                request.numeroDestino(),
                request.titular(),
                request.descripcionPago()
        );

        panelCaja.setCommand(command);
        panelCaja.ejecutar();

        VentaResponseDTO response = obtenerVentaResponse(request.codigoVenta());
        escribirJson(exchange, 201, response);
    }

    /**
     * Consulta una venta por su código.
     *
     * @param exchange el objeto HttpExchange que representa la solicitud y respuesta HTTP
     * @param codigoVenta el código de la venta a consultar
     * @throws IOException si ocurre un error de entrada/salida
     */
    private void consultarVenta(HttpExchange exchange, String codigoVenta) throws IOException {
        VentaResponseDTO response = obtenerVentaResponse(codigoVenta);
        escribirJson(exchange, 200, response);
    }

    /**
     * Anula una venta por su código.
     *
     * @param exchange el objeto HttpExchange que representa la solicitud y respuesta HTTP
     * @param codigoVenta el código de la venta a anular
     * @throws IOException si ocurre un error de entrada/salida
     */
    private void anularVenta(HttpExchange exchange, String codigoVenta) throws IOException {
        String codigoNormalizado = validarTextoObligatorio(codigoVenta, "El código de venta es obligatorio");
        Command command = new AnularVentaCommand(ventaFacade, codigoNormalizado);
        panelCaja.setCommand(command);
        panelCaja.ejecutar();
        VentaResponseDTO response = obtenerVentaResponse(codigoNormalizado);
        escribirJson(exchange, 200, response);
    }

    /**
     * Obtiene el comprobante de una venta por su código.
     *
     * @param exchange el objeto HttpExchange que representa la solicitud y respuesta HTTP
     * @param codigoVenta el código de la venta para obtener el comprobante
     * @throws IOException si ocurre un error de entrada/salida
     */
    private void obtenerComprobante(HttpExchange exchange, String codigoVenta) throws IOException {
        String codigoNormalizado = validarTextoObligatorio(codigoVenta, "El código de venta es obligatorio");
        String comprobante = ventaFacade.obtenerComprobante(codigoNormalizado);
        ComprobanteResponseDTO response = new ComprobanteResponseDTO(codigoNormalizado, comprobante);
        escribirJson(exchange, 200, response);
    }

    /**
     * Lee y deserializa el cuerpo de la solicitud para registrar una venta.
     *
     * @param exchange el objeto HttpExchange que representa la solicitud HTTP
     * @return un objeto RegistrarVentaRequestDTO con los datos de la solicitud
     * @throws IllegalArgumentException si el JSON es inválido o no se puede deserializar
     */
    private RegistrarVentaRequestDTO leerRequestRegistrarVenta(HttpExchange exchange) {
        try (InputStream inputStream = exchange.getRequestBody()) {
            return objectMapper.readValue(inputStream, RegistrarVentaRequestDTO.class);
        } catch (IOException exception) {
            throw new IllegalArgumentException("JSON inválido en la solicitud", exception);
        }
    }

    /**
     * Valida los datos del request para registrar una venta.
     *
     * @param request el objeto RegistrarVentaRequestDTO a validar
     * @throws IllegalArgumentException si algún campo obligatorio está ausente o inválido
     */
    private void validarRequestRegistrarVenta(RegistrarVentaRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("El cuerpo de la solicitud es obligatorio");
        }

        validarTextoObligatorio(request.codigoVenta(), "El código de venta es obligatorio");
        validarTextoObligatorio(request.tipoEnvio(), "El tipo de envío es obligatorio");
        validarTextoObligatorio(request.tipoPago(), "El tipo de pago es obligatorio");
        validarTextoObligatorio(request.numeroDestino(), "El número destino es obligatorio");
        validarTextoObligatorio(request.titular(), "El titular es obligatorio");
        validarTextoObligatorio(request.descripcionPago(), "La descripción de pago es obligatoria");

        Map<String, Integer> productos = request.productos();
        if (productos == null || productos.isEmpty()) {
            throw new IllegalArgumentException("Debe enviar al menos un producto");
        }
        for (Map.Entry<String, Integer> entry : productos.entrySet()) {
            if (entry.getKey() == null || entry.getKey().isBlank()) {
                throw new IllegalArgumentException("Cada producto debe tener código");
            }
            if (entry.getValue() == null || entry.getValue() <= 0) {
                throw new IllegalArgumentException("Cada producto debe tener cantidad mayor a cero");
            }
        }
    }

    /**
     * Resuelve la estrategia de envío según el tipo de envío.
     *
     * @param tipoEnvio el tipo de envío
     * @return la estrategia de envío correspondiente
     * @throws IllegalArgumentException si el tipo de envío no es soportado
     */
    private EstrategiaEnvio resolverEstrategiaEnvio(String tipoEnvio) {
        String tipoEnvioNormalizado = validarTextoObligatorio(tipoEnvio, "El tipo de envío es obligatorio")
                .toUpperCase(Locale.ROOT);

        return switch (tipoEnvioNormalizado) {
            case "ESTANDAR" -> new EnvioEstandar();
            case "EXPRESS" -> new EnvioExpress();
            case "PROGRAMADO" -> new EnvioProgramado();
            default -> throw new IllegalArgumentException("Tipo de envío no soportado: " + tipoEnvio);
        };
    }

    /**
     * Obtiene la información de una venta, incluyendo detalles y pagos.
     *
     * @param codigoVenta el código de la venta a consultar
     * @return un objeto VentaResponseDTO con la información de la venta
     * @throws NoSuchElementException si no se encuentra la venta con el código proporcionado
     */
    private VentaResponseDTO obtenerVentaResponse(String codigoVenta) {
        String codigoNormalizado = validarTextoObligatorio(codigoVenta, "El código de venta es obligatorio");
        Optional<Venta> ventaOptional = ventaService.buscarPorCodigo(codigoNormalizado);
        Venta venta = ventaOptional.orElseThrow(() -> new NoSuchElementException("No existe venta con código: " + codigoNormalizado));
        List<DetalleVenta> detalles = ventaService.listarDetallesPorVentaId(venta.getId());
        List<Pago> pagos = pagoService.listarPorVentaId(venta.getId());
        return ventaMapper.toResponse(venta, detalles, pagos);
    }

    /**
     * Valida que un texto obligatorio no sea nulo ni vacío.
     *
     * @param valor el valor a validar
     * @param mensajeError el mensaje de error a lanzar si la validación falla
     * @return el valor validado y recortado
     * @throws IllegalArgumentException si el valor es nulo o vacío
     */
    private String validarTextoObligatorio(String valor, String mensajeError) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensajeError);
        }
        return valor.trim();
    }

    /**
     * Responde a una solicitud OPTIONS con los encabezados CORS apropiados.
     *
     * @param exchange el objeto HttpExchange que representa la solicitud y respuesta HTTP
     * @throws IOException si ocurre un error de entrada/salida al enviar la respuesta
     */
    private void responderOptions(HttpExchange exchange) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        GlobalExceptionHandler.addCorsHeaders(headers);
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
    }

    /**
     * Escribe una respuesta JSON en el intercambio HTTP.
     *
     * @param exchange el objeto HttpExchange que representa la solicitud y respuesta HTTP
     * @param status el código de estado HTTP a enviar
     * @param body el cuerpo de la respuesta a serializar como JSON
     * @throws IOException si ocurre un error de entrada/salida al escribir la respuesta
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
     * Extrae los segmentos de la ruta a partir del path base.
     *
     * @param path la ruta completa de la solicitud
     * @return una lista de segmentos de la ruta, o null si el path no comienza con el BASE_PATH
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
