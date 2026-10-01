package pe.edu.ulima.isw2.tvendoapi.dto.response;

/**
 * DTO (Data Transfer Object) para la respuesta de comprobante.
 * Este registro encapsula la información del comprobante generado para una venta específica.
 * Contiene el código de venta y el comprobante en formato String.
 * @param codigoVenta El código único de la venta asociada al comprobante.
 * @param comprobante El contenido del comprobante en formato String.
 */
public record ComprobanteResponseDTO(
        String codigoVenta,
        String comprobante
) {
}
