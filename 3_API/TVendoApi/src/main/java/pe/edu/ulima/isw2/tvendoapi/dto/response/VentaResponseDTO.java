package pe.edu.ulima.isw2.tvendoapi.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para la entidad Venta.
 * Contiene información relevante de una venta, incluyendo detalles y pagos asociados.
 * @param codigo Código único de la venta.
 * @param fecha Fecha y hora en que se realizó la venta.
 * @param subtotal Subtotal de la venta antes de impuestos y costos de envío.
 * @param costoEnvio Costo del envío asociado a la venta.
 * @param total Total final de la venta, incluyendo impuestos y costos de envío.
 * @param estado Estado actual de la venta (e.g., "PENDIENTE", "COMPLETADA", "ANULADA").
 * @param observacion Observaciones adicionales sobre la venta.
 * @param detalles Lista de detalles de los productos vendidos en la venta.
 * @param pagos Lista de pagos realizados para la venta.
 */
public record VentaResponseDTO(
        String codigo,
        LocalDateTime fecha,
        BigDecimal subtotal,
        BigDecimal costoEnvio,
        BigDecimal total,
        String estado,
        String observacion,
        List<DetalleVentaResponseDTO> detalles,
        List<PagoResponseDTO> pagos
) {
}
