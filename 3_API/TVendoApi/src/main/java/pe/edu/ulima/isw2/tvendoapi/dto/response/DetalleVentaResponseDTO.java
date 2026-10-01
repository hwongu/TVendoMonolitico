package pe.edu.ulima.isw2.tvendoapi.dto.response;

import java.math.BigDecimal;

/**
 * DTO de respuesta para los detalles de una venta.
 * Contiene información sobre el producto, la cantidad vendida, el precio unitario y el subtotal.
 * Se utiliza para transferir datos entre la capa de servicio y la capa de presentación.
 * @param productoId El identificador del producto vendido.
 * @param cantidad La cantidad de unidades vendidas del producto.
 * @param precioUnitario El precio unitario del producto en el momento de la venta.
 * @param subtotal El subtotal calculado como cantidad * precioUnitario.
 */
public record DetalleVentaResponseDTO(
        Long productoId,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
}
