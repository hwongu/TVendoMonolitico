package pe.edu.ulima.isw2.tvendoapi.dto.request;

import java.util.Map;

/**
 * DTO (Data Transfer Object) para registrar una venta.
 * Contiene la información necesaria para procesar una venta, incluyendo el código de venta,
 * los productos involucrados, el tipo de envío, el tipo de pago, el número de destino,
 * el titular y la descripción del pago.
 * @param codigoVenta Código único de la venta.
 * @param productos Mapa que relaciona los códigos de los productos con sus cantidades.
 * @param tipoEnvio Tipo de envío seleccionado para la venta.
 * @param tipoPago Tipo de pago seleccionado para la venta.
 * @param numeroDestino Número de destino para el pago.
 * @param titular Nombre del titular del pago.
 * @param descripcionPago Descripción adicional del pago.
 */
public record RegistrarVentaRequestDTO(
        String codigoVenta,
        Map<String, Integer> productos,
        String tipoEnvio,
        String tipoPago,
        String numeroDestino,
        String titular,
        String descripcionPago
) {
}
