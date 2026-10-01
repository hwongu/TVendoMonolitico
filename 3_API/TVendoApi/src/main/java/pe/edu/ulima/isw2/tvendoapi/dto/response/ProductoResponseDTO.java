package pe.edu.ulima.isw2.tvendoapi.dto.response;

import java.math.BigDecimal;

/**
 * DTO de respuesta para representar la información de un producto.
 * Contiene los atributos esenciales del producto que se devolverán en las respuestas de la API.
 * @param id Identificador único del producto.
 * @param codigo Código único del producto.
 * @param nombre Nombre del producto.
 * @param descripcion Descripción detallada del producto.
 * @param precio Precio del producto.
 * @param stock Cantidad disponible en stock del producto.
 * @param activo Indica si el producto está activo o no.
 */
public record ProductoResponseDTO(
        Long id,
        String codigo,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Integer stock,
        Boolean activo
) {
}
