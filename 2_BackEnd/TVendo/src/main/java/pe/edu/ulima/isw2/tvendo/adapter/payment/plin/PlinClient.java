package pe.edu.ulima.isw2.tvendo.adapter.payment.plin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import pe.edu.ulima.isw2.tvendo.datasource.config.ApiConfig;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Cliente HTTP para interactuar con el servicio de pagos Plin.
 * <p>
 * Esta clase encapsula la lógica de comunicación con el servicio Plin, incluyendo la serialización
 * y deserialización de objetos JSON, así como el manejo de errores HTTP.
 * </p>
 * <p>
 * Se utiliza {@link HttpClient} para realizar solicitudes HTTP y {@link ObjectMapper} para convertir
 * entre objetos Java y JSON.
 * </p>
 * <p>
 * Este cliente está diseñado para ser utilizado en un entorno donde se requiere procesar pagos a través
 * del servicio Plin, enviando solicitudes POST y recibiendo respuestas que contienen información sobre
 * el estado del pago.
 * </p>
 *
 * @author Henry Wong
 */
public class PlinClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String url;

    public PlinClient(ObjectMapper objectMapper) {
        this(objectMapper, ApiConfig.getInstance().getPlinUrl());
    }

    public PlinClient(ObjectMapper objectMapper, String url) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = objectMapper;
        this.url = url;
    }

    public PlinResponseDTO procesarPago(PlinRequestDTO requestDTO) {
        String body = serializar(requestDTO);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 201) {
                throw new RuntimeException("Error al invocar Plin. HTTP " + response.statusCode() + " - body: " + response.body());
            }
            return deserializar(response.body());
        } catch (IOException e) {
            throw new RuntimeException("Plin no disponible o error de comunicación: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("La llamada HTTP a Plin fue interrumpida", e);
        }
    }

    private String serializar(PlinRequestDTO requestDTO) {
        try {
            return objectMapper.writeValueAsString(requestDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al serializar request de Plin", e);
        }
    }

    private PlinResponseDTO deserializar(String body) {
        try {
            return objectMapper.readValue(body, PlinResponseDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Respuesta inválida de Plin: " + body, e);
        }
    }
}
