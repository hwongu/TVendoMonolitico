package pe.edu.ulima.isw2.tvendo.adapter.payment;

/**
 * Clase que representa el resultado de un pago realizado a través de un proveedor de pagos.
 * Contiene información sobre el proveedor, el código de operación y el estado del pago.
 * @param proveedor El nombre del proveedor de pagos utilizado para la transacción.
 * @param codigoOperacion El código único que identifica la operación de pago.
 * @param estado El estado del pago, indicando si fue exitoso, fallido o pendiente.
 */
public record ResultadoPago(
        String proveedor,
        String codigoOperacion,
        String estado
) {
}
