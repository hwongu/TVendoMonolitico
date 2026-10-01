package pe.edu.ulima.isw2.tvendo.adapter.payment.yape;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import pe.edu.ulima.isw2.tvendo.datasource.config.ApiConfig;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class YapeClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String url;

    public YapeClient(ObjectMapper objectMapper) {
        this(objectMapper, ApiConfig.getInstance().getYapeUrl());
    }

    public YapeClient(ObjectMapper objectMapper, String url) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = objectMapper;
        this.url = url;
    }

    public YapeResponseDTO procesarPago(YapeRequestDTO requestDTO) {
        String body = serializar(requestDTO);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 201) {
                throw new RuntimeException("Error al invocar Yape. HTTP " + response.statusCode() + " - body: " + response.body());
            }
            return deserializar(response.body());
        } catch (IOException e) {
            throw new RuntimeException("Yape no disponible o error de comunicación: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("La llamada HTTP a Yape fue interrumpida", e);
        }
    }

    private String serializar(YapeRequestDTO requestDTO) {
        try {
            return objectMapper.writeValueAsString(requestDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al serializar request de Yape", e);
        }
    }

    private YapeResponseDTO deserializar(String body) {
        try {
            return objectMapper.readValue(body, YapeResponseDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Respuesta inválida de Yape: " + body, e);
        }
    }
}
